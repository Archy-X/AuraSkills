package dev.aurelium.auraskills.bukkit.skills.foraging;

import com.sk89q.worldedit.WorldEdit;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.mana.ReadiedManaAbility;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.bukkit.util.BlockFaceUtil;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.source.SourceTag;
import dev.aurelium.auraskills.common.source.type.BlockSource;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Treecapitator extends ReadiedManaAbility {

    private final boolean giveXp;

    public Treecapitator(AuraSkills plugin) {
        super(plugin, ManaAbilities.TREECAPITATOR, ManaAbilityMessage.TREECAPITATOR_START, ManaAbilityMessage.TREECAPITATOR_END,
                new String[]{"_AXE"}, new Action[]{Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK});
        this.giveXp = ManaAbilities.TREECAPITATOR.optionBoolean("give_xp", true);
    }

    @Override
    public void onActivate(Player player, User user) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    @Override
    public void onStop(Player player, User user) {

    }

    @Override
    protected boolean materialMatches(String checked, Player player) {
        // Don't ready WorldEdit axe if WorldEdit is available and the player has permission for the wand.
        if (plugin.getServer().getPluginManager().isPluginEnabled("WorldEdit") && player.hasPermission("worldedit.wand")) {
            String wandItem = WorldEdit.getInstance().getConfiguration().wandItem;
            String wandString = wandItem.contains(":") ? wandItem.split(":")[1] : wandItem;
            Material wandMaterial = Material.matchMaterial(wandString.toUpperCase(Locale.ROOT));
            Material checkMaterial = Material.matchMaterial(checked.toUpperCase(Locale.ROOT));

            if (wandMaterial == checkMaterial) {
                return false;
            }
        }

        for (String material : getMaterials()) {
            if (checked.contains(material)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if (isDisabled()) return;
        if (event.isCancelled()) return;
        // Checks if block broken is log
        Block block = event.getBlock();
        if (plugin.getRegionManager().isPlacedBlock(block)) {
            return;
        }

        BlockXpSource source = getNonStrippedSource(block);
        if (source == null) {
            return;
        }
        if (plugin.getSkillManager().hasTag(source, SourceTag.TRUNKS)) {
            Player player = event.getPlayer();

            if (failsChecks(player)) return;

            User user = plugin.getUser(player);

            if (isActivated(user)) {
                breakTree(user, block, source);
                return;
            }
            if (isHoldingMaterial(player) && checkActivation(player)) {
                breakTree(user, block, source);
            }
        }
    }

    public void breakTree(User user, Block block, BlockXpSource source) {
        breakBlock(user, block, new TreecapitatorTree(block, source));
    }

    private void breakBlock(User user, Block block, TreecapitatorTree tree) {
        if (tree.getBlocksBroken() > tree.getMaxBlocks()) {
            return;
        }
        for (Block adjacentBlock : BlockFaceUtil.getSurroundingBlocks(block)) {
            BlockXpSource adjSource = getSource(adjacentBlock);
            if (!plugin.getSkillManager().hasTag(adjSource, SourceTag.TREECAPITATOR_APPLICABLE))
                continue; // Check block is leaf or trunk
            // Make sure block was not placed
            if (plugin.getRegionManager().isPlacedBlock(adjacentBlock)) {
                continue;
            }
            adjacentBlock.breakNaturally();
            tree.incrementBlocksBroken();
            if (adjSource != null && giveXp) {
                plugin.getLevelManager().addXp(user, manaAbility.getSkill(), adjSource, adjSource.getXp());
            }
            // Continue breaking blocks
            Block originalBlock = tree.getOriginalBlock();
            if (adjacentBlock.getX() > originalBlock.getX() + 6 || adjacentBlock.getZ() > originalBlock.getZ() + 6 || adjacentBlock.getY() > originalBlock.getY() + 31) {
                return;
            }
            // Break the next blocks
            plugin.getScheduler().scheduleSync(() -> breakBlock(user, adjacentBlock, tree), 50, TimeUnit.MILLISECONDS);
        }
    }

    @Nullable
    private BlockXpSource getNonStrippedSource(Block block) {
        String target = block.getType().toString();
        if (target.contains("STRIPPED")) {
            String blockOrigin = target.replace("STRIPPED_", "");
            Material newTarget = Material.matchMaterial(blockOrigin);
            if (newTarget != null && newTarget.isBlock()) {
                XpSource newSource = plugin.getSkillManager().getSourceById(NamespacedId.fromDefault(newTarget.toString().toLowerCase(Locale.ROOT)));
                if (newSource instanceof BlockXpSource blockXpSource) {
                    return blockXpSource;
                }
            }
        } else {
            return getSource(block);
        }
        return null;
    }

    @Nullable
    private BlockXpSource getSource(Block block) {
        var skillSource = plugin.getLevelManager().getLeveler(BlockLeveler.class).getSource(block, BlockXpSource.BlockTriggers.BREAK);

        if (skillSource != null) {
            return skillSource.source();
        }
        return null;
    }

    private static class TreecapitatorTree {

        private final Block originalBlock;
        private int blocksBroken;
        private int maxBlocks;

        public TreecapitatorTree(Block originalBlock, BlockXpSource source) {
            this.originalBlock = originalBlock;
            setMaxBlocks(source);
        }

        public Block getOriginalBlock() {
            return originalBlock;
        }

        public int getBlocksBroken() {
            return blocksBroken;
        }

        public void incrementBlocksBroken() {
            blocksBroken++;
        }

        public int getMaxBlocks() {
            return maxBlocks;
        }

        private void setMaxBlocks(BlockXpSource source) {
            int maxBlocks = (source != null && source.getMaxBlocks() >= 1) ? source.getMaxBlocks() : BlockSource.DEFAULT_MAX_BLOCKS;
            double multiplier = ManaAbilities.TREECAPITATOR.optionDouble("max_blocks_multiplier", 1.0);
            this.maxBlocks = (int) (maxBlocks * multiplier);
        }

    }

}
