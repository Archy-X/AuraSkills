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
    public void readyAbility(PlayerInteractEvent event, Skill skill, String[] matchMaterial, Action... actions) {
        if (OptionL.isEnabled(skill)) {
            MAbility mAbility = skill.getManaAbility();
            if (mAbility == null) return;
            if (plugin.getAbilityManager().isEnabled(mAbility)) {
                boolean matched = false;
                for (Action action : actions) {
                    if (event.getAction() == action) {
                        matched = true;
                        break;
                    }
                }
                if (matched) {
                    // Check for hoe tilling
                    if (hasMatch(matchMaterial, "HOE") && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
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
                            } else {
                                if (clickedBlock.getType() == XMaterial.GRASS_BLOCK.parseMaterial()
                                        || clickedBlock.getType() == XMaterial.GRASS_PATH.parseMaterial()
                                        || clickedBlock.getType() == XMaterial.FARMLAND.parseMaterial()) {
                                    return;
                                } else if (clickedBlock.getType() == Material.DIRT) {
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
                    if (hasMatch(matchMaterial, mat.name())) {
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
                            if (SkillLoader.playerSkills.get(player.getUniqueId()).getManaAbilityLevel(mAbility) > 0) {
                                ManaAbilityManager manager = plugin.getManaAbilityManager();
                                // Checks if speed mine is already activated
                                if (manager.isActivated(player.getUniqueId(), mAbility)) {
                                    return;
                                }
                                // Checks if speed mine is already ready
                                if (manager.isReady(player.getUniqueId(), mAbility)) {
                                    return;
                                }
                                // Checks if cooldown is reached
                                if (manager.getPlayerCooldown(player.getUniqueId(), mAbility) == 0) {
                                    manager.setReady(player.getUniqueId(), mAbility, true);
                                    plugin.getAbilityManager().sendMessage(player, ChatColor.GRAY + Lang.getMessage(ManaAbilityMessage.valueOf(mAbility.name() + "_RAISE"), locale));
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (!manager.isActivated(player.getUniqueId(), mAbility)) {
                                                if (manager.isReady(player.getUniqueId(), mAbility)) {
                                                    manager.setReady(player.getUniqueId(), mAbility, false);
                                                    plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.valueOf(mAbility.name() + "_LOWER"), locale));
                                                }
                                            }
                                        }
                                    }.runTaskLater(plugin, 80L);
                                } else {
                                    if (manager.getErrorTimer(player.getUniqueId(), mAbility) == 0) {
                                        plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.NOT_READY, locale).replace("{cooldown}", NumberUtil.format0((double) plugin.getManaAbilityManager().getPlayerCooldown(player.getUniqueId(), mAbility) / 20)));
                                        manager.setErrorTimer(player.getUniqueId(), mAbility, 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasMatch(String[] matchMaterials, String checked) {
        for (String matchMaterial : matchMaterials) {
            if (checked.contains(matchMaterial)) {
                return true;
            }
        }
        return false;
    }

}
