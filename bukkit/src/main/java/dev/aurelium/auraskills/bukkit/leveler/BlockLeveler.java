package dev.aurelium.auraskills.bukkit.leveler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.source.SourceType;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BlockLeveler extends AbstractLeveler {

    public BlockLeveler(AuraSkills plugin) {
        super(plugin, SourceType.BLOCK);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (disabled()) return;
        Player player = event.getPlayer();
        User user = plugin.getUser(player);

        Pair<BlockXpSource, Skill> sourcePair = getSource(event.getBlock(), BlockXpSource.BlockTriggers.BREAK);
        if (sourcePair == null) {
            return;
        }

        BlockXpSource source = sourcePair.getFirst();
        Skill skill = sourcePair.getSecond();

        if (failsChecks(event, player, event.getBlock().getLocation(), skill)) return;

        plugin.getLevelManager().addXp(user, skill, source.getXp());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (disabled()) return;
        Player player = event.getPlayer();
        User user = plugin.getUser(player);

        Block block = event.getClickedBlock();
        if (block == null) return;

        Pair<BlockXpSource, Skill> sourcePair = getSource(block, BlockXpSource.BlockTriggers.INTERACT);
        if (sourcePair == null) {
            return;
        }

        BlockXpSource source = sourcePair.getFirst();
        Skill skill = sourcePair.getSecond();

        if (failsChecks(event, player, block.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(user, skill, source.getXp());
    }

    public boolean matchesSource(Block block, BlockXpSource source, BlockXpSource.BlockTriggers trigger) {
        var sourcePair = getSource(block, trigger);
        if (sourcePair == null) {
            return false;
        }
        return sourcePair.getFirst().equals(source);
    }

    @Nullable
    public Pair<BlockXpSource, Skill> getSource(Block block, BlockXpSource.BlockTriggers trigger) {
        Map<BlockXpSource, Skill> sources = plugin.getSkillManager().getSourcesOfType(BlockXpSource.class);
        sources = filterByTrigger(sources, trigger);
        for (Map.Entry<BlockXpSource, Skill> entry : sources.entrySet()) {
            BlockXpSource source = entry.getKey();
            Skill skill = entry.getValue();

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
            boolean anyStateMatches = true;
            if (source.getStates() != null) {
                anyStateMatches = false;
                // Convert block data to json
                String blockDataString = block.getBlockData().getAsString(true);
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(blockDataString, JsonObject.class);
                // Check if block data matches defined states
                for (BlockXpSource.BlockXpSourceState state : source.getStates()) {
                    if (state == null) continue;
                    boolean stateMatches = true;
                    for (Map.Entry<String, Object> stateEntry : state.getStateMap().entrySet()) {
                        String key = stateEntry.getKey();
                        Object value = stateEntry.getValue();
                        if (!jsonObject.has(key)) {
                            stateMatches = false;
                            break;
                        }
                        if (!jsonObject.get(key).getAsString().equals(String.valueOf(value))) {
                            stateMatches = false;
                            break;
                        }
                    }
                    // If one state matches, then the block matches and we can stop checking
                    if (stateMatches) {
                        anyStateMatches = true;
                        break;
                    }
                }
            }
            // Skip if no state matches
            if (!anyStateMatches) {
                continue;
            }

            return new Pair<>(source, skill);
        }
        return null;
    }

    private Map<BlockXpSource, Skill> filterByTrigger(Map<BlockXpSource, Skill> sources, BlockXpSource.BlockTriggers trigger) {
        Map<BlockXpSource, Skill> filtered = new HashMap<>();
        for (Map.Entry<BlockXpSource, Skill> entry : sources.entrySet()) {
            BlockXpSource source = entry.getKey();
            Skill skill = entry.getValue();
            // Check if trigger matches any of the source triggers
            for (BlockXpSource.BlockTriggers sourceTrigger : source.getTriggers()) {
                if (sourceTrigger == trigger) {
                    filtered.put(source, skill);
                    break;
                }
            }
        }
        return filtered;
    }

}
