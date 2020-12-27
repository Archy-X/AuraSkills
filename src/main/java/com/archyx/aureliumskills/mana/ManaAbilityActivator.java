package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.util.NumberUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

public class ManaAbilityActivator {

    private final AureliumSkills plugin;

    public ManaAbilityActivator(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    public void readyAbility(PlayerInteractEvent event, Skill skill, String matchMaterial) {
        if (OptionL.isEnabled(skill)) {
            if (plugin.getAbilityManager().isEnabled(skill.getManaAbility())) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    // Check for hoe tilling
                    if (matchMaterial.equals("HOE") && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        Block clickedBlock = event.getClickedBlock();
                        if (clickedBlock != null) {
                            if (XMaterial.isNewVersion()) {
                                if (clickedBlock.getType() == XMaterial.DIRT.parseMaterial()
                                        || clickedBlock.getType() == XMaterial.GRASS_BLOCK.parseMaterial()
                                        || clickedBlock.getType() == XMaterial.COARSE_DIRT.parseMaterial()
                                        || clickedBlock.getType() == XMaterial.GRASS_PATH.parseMaterial()
                                        || clickedBlock.getType() == XMaterial.FARMLAND.parseMaterial()) {
                                    return;
                                }
                            }
                            else {
                                if (clickedBlock.getType() == XMaterial.GRASS_BLOCK.parseMaterial()
                                        || clickedBlock.getType() == XMaterial.GRASS_PATH.parseMaterial()
                                        || clickedBlock.getType() == XMaterial.FARMLAND.parseMaterial()) {
                                    return;
                                }
                                else if (clickedBlock.getType() == Material.DIRT) {
                                    switch (clickedBlock.getData()) {
                                        case 0:
                                        case 1:
                                            return;
                                    }
                                }
                            }
                        }
                    }
                    Material mat = event.getPlayer().getInventory().getItemInMainHand().getType();
                    if (mat.name().toUpperCase().contains(matchMaterial)) {
                        Player player = event.getPlayer();
                        Locale locale = Lang.getLanguage(player);
                        // Check disabled worlds
                        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
                            return;
                        }
                        // Check permission
                        if (!player.hasPermission("aureliumskills." + skill.toString().toLowerCase())) {
                            return;
                        }
                        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                            if (SkillLoader.playerSkills.get(player.getUniqueId()).getManaAbilityLevel(skill.getManaAbility()) > 0) {
                                ManaAbilityManager manager = plugin.getManaAbilityManager();
                                // Checks if speed mine is already activated
                                if (manager.isActivated(player.getUniqueId(), skill.getManaAbility())) {
                                    return;
                                }
                                // Checks if speed mine is already ready
                                if (manager.isReady(player.getUniqueId(), skill.getManaAbility())) {
                                    return;
                                }
                                // Checks if cooldown is reached
                                if (manager.getPlayerCooldown(player.getUniqueId(), skill.getManaAbility()) == 0) {
                                    manager.setReady(player.getUniqueId(), skill.getManaAbility(), true);
                                    player.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.GRAY + Lang.getMessage(ManaAbilityMessage.valueOf(skill.getManaAbility().name() + "_RAISE"), locale));
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (!manager.isActivated(player.getUniqueId(), skill.getManaAbility())) {
                                                if (manager.isReady(player.getUniqueId(), skill.getManaAbility())) {
                                                    manager.setReady(player.getUniqueId(), skill.getManaAbility(), false);
                                                    player.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.GRAY + Lang.getMessage(ManaAbilityMessage.valueOf(skill.getManaAbility().name() + "_LOWER"), locale));
                                                }
                                            }
                                        }
                                    }.runTaskLater(plugin, 50L);
                                } else {
                                    if (manager.getErrorTimer(player.getUniqueId(), skill.getManaAbility()) == 0) {
                                        player.sendMessage(AureliumSkills.getPrefix(locale) + ChatColor.YELLOW + Lang.getMessage(ManaAbilityMessage.NOT_READY, locale).replace("{cooldown}", NumberUtil.format1((double) plugin.getManaAbilityManager().getPlayerCooldown(player.getUniqueId(), skill.getManaAbility()) / 20)));
                                        manager.setErrorTimer(player.getUniqueId(), skill.getManaAbility(), 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
