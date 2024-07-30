package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.api.source.type.BlockXpSource.BlockTriggers;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skills.mining.MiningAbilities;
import dev.aurelium.auraskills.bukkit.trait.GatheringLuckTraits;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class BlockLeveler extends SourceLeveler {

    private final BlockLevelerHelper helper;
    private final Map<UniqueBlock, SkillSource<BlockXpSource>> sourceCache;

    public BlockLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.BLOCK);
        this.helper = new BlockLevelerHelper(plugin);
        this.sourceCache = new HashMap<>();
    }

    public void clearSourceCache() {
        this.sourceCache.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (disabled()) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();

        handleBreak(player, block, event, trait -> event.isDropItems() ? trait.getUniqueDrops(block, player) : Collections.emptySet());
    }

    public void handleBreak(Player player, Block block, Cancellable event, Function<GatheringLuckTraits, Set<ItemStack>> dropFunction) {
        User user = plugin.getUser(player);

        SkillSource<BlockXpSource> skillSource = getSource(block, BlockXpSource.BlockTriggers.BREAK);
        if (skillSource == null) {
            return;
        }

        BlockXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        if (failsChecks(event, player, block.getLocation(), skill)) return;

        // Check for player placed blocks
        if (source.checkReplace() && plugin.getRegionManager().isPlacedBlock(block)) {
            // Allow Lucky Miner drops for ores that don't drop in block form (Silk Touch check handled by Lucky Miner)
            var miningAbilities = plugin.getAbilityManager().getAbilityImpl(MiningAbilities.class);
            if (miningAbilities.dropsMineralDirectly(block)) {
                applyBlockLuck(skill, player, user, block, source, dropFunction);
            }
            return;
        }

        double multiplier = helper.getBlocksBroken(block, source);
        multiplier *= helper.getStateMultiplier(block, source);

        plugin.getLevelManager().addXp(user, skill, source, source.getXp() * multiplier);
        applyBlockLuck(skill, player, user, block, source, dropFunction);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        if (disabled()) return;
        Player player = event.getPlayer();
        User user = plugin.getUser(player);

        Block block = event.getClickedBlock();
        if (block == null) return;

        SkillSource<BlockXpSource> skillSource = getSource(block, BlockXpSource.BlockTriggers.INTERACT);
        if (skillSource == null) {
            return;
        }

        BlockXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        if (failsChecks(event, player, block.getLocation(), skill)) return;

        final double multiplier = helper.getBlocksBroken(block, source) * helper.getStateMultiplier(block, source);

        Material materialBefore = block.getType();
        if (source.getAfterStates() != null) {
            plugin.getScheduler().scheduleSync(() -> {
                // Checks that the block after one tick is the same material and matches the after_state/after_states
                if (materialBefore == block.getType() && matchesStates(block, source.getAfterStates())) {
                    plugin.getLevelManager().addXp(user, skill, source, source.getXp() * multiplier);
                    applyBlockLuck(skill, player, user, block, source);
                }
            }, 50, TimeUnit.MILLISECONDS);
        } else { // Handle sources without after_state/after_states
            plugin.getLevelManager().addXp(user, skill, source, source.getXp() * multiplier);
            applyBlockLuck(skill, player, user, block, source);
        }
    }

    private void applyBlockLuck(Skill skill, Player player, User user, Block block, XpSource source) {
        applyBlockLuck(skill, player, user, block, source, trait -> trait.getUniqueDrops(block, player));
    }

    private void applyBlockLuck(Skill skill, Player player, User user, Block block, XpSource source, Function<GatheringLuckTraits, Set<ItemStack>> dropFunction) {
        // Don't drop extra for silk touch + ores
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0 && plugin.getAbilityManager().getAbilityImpl(MiningAbilities.class).dropsMineralDirectly(block)) {
            return;
        }

        GatheringLuckTraits traitImpl = plugin.getTraitManager().getTraitImpl(GatheringLuckTraits.class);
        Trait blockLuckTrait = traitImpl.getTrait(skill);
        if (blockLuckTrait != null) {
            traitImpl.apply(blockLuckTrait, block, player, user, source, dropFunction.apply(traitImpl));
        }
    }

    public boolean isDifferentSource(Block block, BlockXpSource source, BlockXpSource.BlockTriggers trigger) {
        var skillSource = getSource(block, trigger);
        if (skillSource == null) {
            return true;
        }
        return !skillSource.source().equals(source);
    }

    @Nullable
    public SkillSource<BlockXpSource> getSource(Block block, BlockXpSource.BlockTriggers trigger) {
        // Optimize by immediately rejecting air blocks
        if (block.getType().isAir()) {
            return null;
        }

        var cacheKey = new UniqueBlock(block.getType(), block.getBlockData().getAsString(true), trigger);
        SkillSource<BlockXpSource> cachedSource = sourceCache.get(cacheKey);
        // Cache hit
        if (cachedSource != null) {
            return cachedSource;
        }

        List<SkillSource<BlockXpSource>> sources = plugin.getSkillManager().getSourcesOfType(BlockXpSource.class);
        sources = filterByTrigger(sources, trigger);
        for (SkillSource<BlockXpSource> entry : sources) {
            BlockXpSource source = entry.source();

            // Check block type (material)
            boolean blockMatches = false;
            for (String blockName : source.getBlocks()) {
                if (block.getType().name().equalsIgnoreCase(blockName)) {
                    blockMatches = true;
                    break;
                }
            }
            if (!blockMatches) {
                continue;
            }

            // Check block state
            if (source.getStates() != null) {
                // Skip if no state matches
                if (!matchesStates(block, source.getStates())) {
                    continue;
                }
            }
            // Update cache
            sourceCache.put(cacheKey, entry);
            return entry;
        }
        return null;
    }

    private boolean matchesStates(Block block, BlockXpSource.BlockXpSourceState[] states) {
        String blockDataString = block.getBlockData().getAsString(true);
        Map<String, Object> blockDataMap = parseFromBlockData(blockDataString);
        // Check if block data matches defined states
        for (BlockXpSource.BlockXpSourceState state : states) {
            if (state == null) continue;
            boolean stateMatches = true;
            for (Map.Entry<String, Object> stateEntry : state.getStateMap().entrySet()) {
                String key = stateEntry.getKey();
                Object value = stateEntry.getValue();
                if (!blockDataMap.containsKey(key)) {
                    stateMatches = false;
                    break;
                }
                if (!blockDataMap.get(key).equals(value)) {
                    stateMatches = false;
                    break;
                }
            }
            // If one state matches, then the block matches and we can stop checking
            if (stateMatches) {
                return true;
            }
        }
        return false;
    }

    private List<SkillSource<BlockXpSource>> filterByTrigger(List<SkillSource<BlockXpSource>> sources, BlockXpSource.BlockTriggers trigger) {
        List<SkillSource<BlockXpSource>> filtered = new ArrayList<>();
        for (SkillSource<BlockXpSource> entry : sources) {
            BlockXpSource source = entry.source();
            // Check if trigger matches any of the source triggers
            for (BlockXpSource.BlockTriggers sourceTrigger : source.getTriggers()) {
                if (sourceTrigger == trigger) {
                    filtered.add(entry);
                    break;
                }
            }
        }
        return filtered;
    }

    public static Map<String, Object> parseFromBlockData(String input) {
        Map<String, Object> result = new HashMap<>();
        // Check if the input is valid
        if (input == null || input.isEmpty()) {
            return result;
        }
        // Find the index of the first bracket
        int bracketIndex = input.indexOf("[");
        // Check if the bracket exists
        if (bracketIndex == -1) {
            return result;
        }
        // Get the part of the input after the bracket and remove the closing bracket
        String data = input.substring(bracketIndex + 1).replace("]", "");
        // Find the index of the first comma
        int commaIndex = data.indexOf(",");
        // Loop until there are no more commas
        while (commaIndex != -1) {
            // Get the pair before the comma
            String pair = data.substring(0, commaIndex);
            // Find the index of the equal sign
            int equalIndex = pair.indexOf("=");
            // Check if the equal sign exists
            if (equalIndex != -1) {
                // Get the key and value and trim any whitespace
                String key = pair.substring(0, equalIndex).trim();
                String value = pair.substring(equalIndex + 1).trim();
                // Parse the value and put it in the result map with the key
                result.put(key, parseValue(value));
            }
            // Remove the pair and the comma from the data
            data = data.substring(commaIndex + 1);
            // Find the next comma index
            commaIndex = data.indexOf(",");
        }
        // Check if there is any remaining data
        if (!data.isEmpty()) {
            // Find the index of the equal sign
            int equalIndex = data.indexOf("=");
            // Check if the equal sign exists
            if (equalIndex != -1) {
                // Get the key and value and trim any whitespace
                String key = data.substring(0, equalIndex).trim();
                String value = data.substring(equalIndex + 1).trim();
                // Parse the value and put it in the result map with the key
                result.put(key, parseValue(value));
            }
        }
        // Return the result map
        return result;
    }

    private static Object parseValue(String value) {
        // Try to parse as an int
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            // Ignore and continue
        }
        // Try to parse as a double
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            // Ignore and continue
        }
        // Try to parse as a boolean
        if (value.equals("true")) {
            return true;
        }
        if (value.equals("false")) {
            return false;
        }
        // Return as a String otherwise
        return value;
    }

    public record UniqueBlock(Material material, String blockData, BlockTriggers trigger) {

    }

}
