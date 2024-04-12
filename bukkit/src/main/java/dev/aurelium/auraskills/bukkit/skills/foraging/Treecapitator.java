package dev.aurelium.auraskills.bukkit.skills.foraging;

import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.mana.ReadiedManaAbility;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.bukkit.util.BlockFaceUtil;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.user.User;
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

    public Treecapitator(AuraSkills plugin) {
        super(plugin, ManaAbilities.TREECAPITATOR, ManaAbilityMessage.TREECAPITATOR_START, ManaAbilityMessage.TREECAPITATOR_END,
                new String[]{"_AXE"}, new Action[]{Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK});
    }

    @Override
    public void onActivate(Player player, User user) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    @Override
    public void onStop(Player player, User user) {

    }

    @Override
    protected boolean materialMatches(String checked) {
        // Don't ready world edit axe
        if (checked.equals("WOODEN_AXE") || checked.equals("WOOD_AXE")) {
            if (plugin.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
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
        if (isTrunk(block) || block.getType().toString().contains("STRIPPED")) {
            Player player = event.getPlayer();

            if (failsChecks(player)) return;

            User user = plugin.getUser(player);

            if (isActivated(user)) {
                breakTree(user, block);
                return;
            }
            if (isHoldingMaterial(player) && checkActivation(player)) {
                breakTree(user, block);
            }
        }
    }

    public void breakTree(User user, Block block) {
        breakBlock(user, block, new TreecapitatorTree(plugin, block));
    }

    private void breakBlock(User user, Block block, TreecapitatorTree tree) {
        if (tree.getBlocksBroken() > tree.getMaxBlocks()) {
            return;
        }
        for (Block adjacentBlock : BlockFaceUtil.getSurroundingBlocks(block)) {
            boolean isTrunk = isTrunk(adjacentBlock);
            boolean isLeaf = isLeaf(adjacentBlock);
            if (!isTrunk && !isLeaf && !adjacentBlock.getType().toString().equals("SHROOMLIGHT")) continue; // Check block is leaf or trunk
            // Make sure block was not placed
            if (plugin.getRegionManager().isPlacedBlock(adjacentBlock)) {
                continue;
            }
            BlockXpSource source = getSource(block);
            adjacentBlock.breakNaturally();
            tree.incrementBlocksBroken();
            if (source != null) {
                plugin.getLevelManager().addXp(user, manaAbility.getSkill(), source, source.getXp());
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
    private BlockXpSource getSource(Block block) {
        var skillSource = plugin.getLevelManager().getLeveler(BlockLeveler.class).getSource(block, BlockXpSource.BlockTriggers.BREAK);

        if (skillSource != null) {
            return skillSource.source();
        }
        return null;
    }

    private boolean isTrunk(Block block) {
        BlockXpSource source = getSource(block);
        if (source != null) {
            return source.isTrunk();
        }
        return false;
    }

    private boolean isLeaf(Block block) {
        BlockXpSource source = getSource(block);
        if (source != null) {
            return source.isLeaf();
        }
        return false;
    }

    private static class TreecapitatorTree {

        private final AuraSkills plugin;
        private final Block originalBlock;
        private int blocksBroken;
        private int maxBlocks;

        public TreecapitatorTree(AuraSkills plugin, Block originalBlock) {
            this.plugin = plugin;
            this.originalBlock = originalBlock;
            setMaxBlocks();
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

        private void setMaxBlocks() {
            var skillSource = plugin.getLevelManager().getLeveler(BlockLeveler.class).getSource(originalBlock, BlockXpSource.BlockTriggers.BREAK);

            XpSource source = skillSource != null ? skillSource.source() : null;

            String matName = originalBlock.getType().toString();
            if (source == null && matName.contains("STRIPPED")) {
                String[] woodNames = new String[] {"OAK", "SPRUCE", "BIRCH", "JUNGLE", "ACACIA", "DARK_OAK", "CRIMSON", "WARPED", "MANGROVE", "CHERRY"};
                for (String woodName : woodNames) {
                    if (matName.contains(woodName)) {
                        if (woodName.equals("CRIMSON") || woodName.equals("WARPED")) {
                            source = plugin.getSkillManager().getSourceById(NamespacedId.fromDefault(woodName.toLowerCase(Locale.ROOT) + "_stem"));
                        } else {
                            source = plugin.getSkillManager().getSourceById(NamespacedId.fromDefault(woodName.toLowerCase(Locale.ROOT) + "_log"));
                        }
                        break;
                    }
                }
            }
            if (source != null) {
                if (matName.contains("OAK") || matName.contains("BIRCH") || matName.contains("ACACIA") || matName.contains("CHERRY")) {
                    maxBlocks = 100;
                } else if (matName.contains("SPRUCE") || matName.contains("CRIMSON") || matName.contains("WARPED") || matName.contains("MANGROVE")) {
                    maxBlocks = 125;
                } else if (matName.contains("JUNGLE")) {
                    maxBlocks = 200;
                } else if (matName.contains("DARK_OAK")) {
                    maxBlocks = 150;
                }
            } else {
                maxBlocks = 100;
            }
            double multiplier = ManaAbilities.TREECAPITATOR.optionDouble("max_blocks_multiplier", 1.0);
            maxBlocks = (int) (maxBlocks * multiplier);
        }

    }

}
