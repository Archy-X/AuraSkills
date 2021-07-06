package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.foraging.ForagingSource;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Treecapitator extends ReadiedManaAbility {

    public Treecapitator(AureliumSkills plugin) {
        super(plugin, MAbility.TREECAPITATOR, ManaAbilityMessage.TREECAPITATOR_START, ManaAbilityMessage.TREECAPITATOR_END,
                new String[] {"_AXE"}, new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK});
    }

    @Override
    public void onActivate(Player player, PlayerData playerData) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    @Override
    public void onStop(Player player, PlayerData playerData) {

    }

    @Override
    protected boolean materialMatches(String checked) {
        // Don't ready world edit axe
        if (checked.equals("WOODEN_AXE") || checked.equals("WOOD_AXE")) {
            if (plugin.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
                return false;
            }
        }

        for (String material : materials) {
            if (checked.contains(material)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        // Checks if block broken is log
        Material blockMat = event.getBlock().getType();
        if (blockMat.equals(XMaterial.OAK_LOG.parseMaterial()) || blockMat.equals(XMaterial.BIRCH_LOG.parseMaterial()) || blockMat.equals(XMaterial.SPRUCE_LOG.parseMaterial())
                || blockMat.equals(XMaterial.JUNGLE_LOG.parseMaterial()) || blockMat.equals(XMaterial.ACACIA_LOG.parseMaterial()) || blockMat.equals(XMaterial.DARK_OAK_LOG.parseMaterial())) {
            Player player = event.getPlayer();
            if (isActivated(player)) {
                breakTree(player, event.getBlock());
                return;
            }
            if (isReady(player) && isHoldingMaterial(player) && hasEnoughMana(player)) {
                if (hasEnoughMana(player)) {
                    activate(player);
                    breakTree(player, event.getBlock());
                }
            }
        }
    }

    public void breakTree(Player player, Block block) {
        if (plugin.getManaAbilityManager().isActivated(player.getUniqueId(), MAbility.TREECAPITATOR)) {
            breakBlock(player, block.getState(), 0);
        }
    }

    private void breakBlock(Player player, BlockState state, int num) {
        if (num > 20) {
            return;
        }
        BlockState above = state.getBlock().getRelative(BlockFace.UP).getState();
        Material matAbove = above.getType();
        if (matAbove.equals(XMaterial.OAK_LOG.parseMaterial()) || matAbove.equals(XMaterial.SPRUCE_LOG.parseMaterial()) || matAbove.equals(XMaterial.BIRCH_LOG.parseMaterial())
                || matAbove.equals(XMaterial.JUNGLE_LOG.parseMaterial()) || matAbove.equals(XMaterial.ACACIA_LOG.parseMaterial()) || matAbove.equals(XMaterial.DARK_OAK_LOG.parseMaterial())) {
            // Break log and give XP
            ForagingSource source = ForagingSource.getSource(above.getBlock());
            above.getBlock().breakNaturally();
            plugin.getLeveler().addXp(player, Skills.FORAGING, getXp(player, source, Ability.FORAGER));
            new BukkitRunnable() {
                @Override
                public void run() {
                    breakBlock(player, above, num + 1);
                }
            }.runTaskLater(plugin, 1L);
        }
        else {
            checkLeaf(player, above);
            checkLeaf(player, above.getBlock().getRelative(BlockFace.NORTH).getState());
            checkLeaf(player, above.getBlock().getRelative(BlockFace.SOUTH).getState());
            checkLeaf(player, above.getBlock().getRelative(BlockFace.EAST).getState());
            checkLeaf(player, above.getBlock().getRelative(BlockFace.WEST).getState());

        }
        checkLeaf(player, state.getBlock().getRelative(BlockFace.NORTH).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.EAST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.SOUTH).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.WEST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.NORTH_EAST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.NORTH_WEST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.SOUTH_EAST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.SOUTH_WEST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.NORTH_NORTH_EAST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.NORTH_NORTH_WEST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.EAST_NORTH_EAST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.EAST_SOUTH_EAST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.SOUTH_SOUTH_EAST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.SOUTH_SOUTH_WEST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.WEST_NORTH_WEST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.WEST_SOUTH_WEST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.NORTH_NORTH_EAST).getRelative(BlockFace.EAST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.SOUTH_SOUTH_EAST).getRelative(BlockFace.EAST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.NORTH_NORTH_WEST).getRelative(BlockFace.WEST).getState());
        checkLeaf(player, state.getBlock().getRelative(BlockFace.SOUTH_SOUTH_WEST).getRelative(BlockFace.WEST).getState());
    }

    private void checkLeaf(Player player, BlockState state) {
        Material material = state.getType();
        if (material.equals(XMaterial.OAK_LEAVES.parseMaterial()) || material.equals(XMaterial.SPRUCE_LEAVES.parseMaterial()) || material.equals(XMaterial.BIRCH_LEAVES.parseMaterial())
                || material.equals(XMaterial.JUNGLE_LEAVES.parseMaterial()) || material.equals(XMaterial.ACACIA_LEAVES.parseMaterial()) || material.equals(XMaterial.DARK_OAK_LEAVES.parseMaterial())) {
            ForagingSource source = ForagingSource.getSource(state.getBlock());
            state.getBlock().breakNaturally();
            plugin.getLeveler().addXp(player, Skills.FORAGING, getXp(player, source, Ability.FORAGER));
        }
    }
}
