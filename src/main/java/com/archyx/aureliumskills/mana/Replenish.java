package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.util.block.BlockUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Replenish extends ReadiedManaAbility {

    public Replenish(AureliumSkills plugin) {
        super(plugin, MAbility.REPLENISH, ManaAbilityMessage.REPLENISH_START, ManaAbilityMessage.REPLENISH_END, new String[] {"HOE"},
                new Action[] {Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK});
    }

    @Override
    public void onActivate(Player player, PlayerData playerData) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    @Override
    public void onStop(Player player, PlayerData playerData) {

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void activationListener(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (!BlockUtil.isReplenishable(event.getBlock().getType())) return;
        Player player = event.getPlayer();
        if (isActivated(player)) return;
        if (isReady(player) && isHoldingMaterial(player) && hasEnoughMana(player)) {
            activate(player);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (isActivated(player) && BlockUtil.isFullyGrown(block) && BlockUtil.isReplenishable(block.getType())) {
            replantCrop(block);
        }
    }

    private void replantCrop(Block block) {
        Material material = block.getType();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!BlockUtil.isNetherWart(material)) {
                    if (block.getRelative(BlockFace.DOWN).getType().equals(XMaterial.FARMLAND.parseMaterial())) {
                        block.setType(material);
                    }
                } else {
                    if (block.getRelative(BlockFace.DOWN).getType().equals(XMaterial.SOUL_SAND.parseMaterial())) {
                        block.setType(material);
                    }
                }
            }
        }.runTaskLater(plugin, plugin.getManaAbilityManager().getOptionAsInt(MAbility.REPLENISH, "replant_delay", 4));
    }

}
