package com.archyx.aureliumskills.skills.abilities.mana_abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

public class ManaAbilityActivator {

    private final Plugin plugin;

    public ManaAbilityActivator(Plugin plugin) {
        this.plugin = plugin;
    }

    public void readyAbility(PlayerInteractEvent event, Skill skill, String matchMaterial) {
        if (OptionL.isEnabled(skill)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(skill.getManaAbility())) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    Material mat = event.getPlayer().getInventory().getItemInMainHand().getType();
                    if (mat.name().toUpperCase().contains(matchMaterial)) {
                        Player player = event.getPlayer();
                        Locale locale = Lang.getLanguage(player);
                        // Check disabled worlds
                        if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
                            return;
                        }
                        // Check permission
                        if (!player.hasPermission("aureliumskills." + skill.toString().toLowerCase())) {
                            return;
                        }
                        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                            if (SkillLoader.playerSkills.get(player.getUniqueId()).getManaAbilityLevel(skill.getManaAbility()) > 0) {
                                // Checks if speed mine is already activated
                                if (AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), skill.getManaAbility())) {
                                    return;
                                }
                                // Checks if speed mine is already ready
                                if (AureliumSkills.manaAbilityManager.isReady(player.getUniqueId(), skill.getManaAbility())) {
                                    return;
                                }
                                // Checks if cooldown is reached
                                if (AureliumSkills.manaAbilityManager.getCooldown(player.getUniqueId(), skill.getManaAbility()) == 0) {
                                    AureliumSkills.manaAbilityManager.setReady(player.getUniqueId(), skill.getManaAbility(), true);
                                    player.sendMessage(AureliumSkills.tag + ChatColor.GRAY + Lang.getMessage(ManaAbilityMessage.valueOf(skill.getManaAbility().name() + "_RAISE"), locale));
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (!AureliumSkills.manaAbilityManager.isActivated(player.getUniqueId(), skill.getManaAbility())) {
                                                if (AureliumSkills.manaAbilityManager.isReady(player.getUniqueId(), skill.getManaAbility())) {
                                                    AureliumSkills.manaAbilityManager.setReady(player.getUniqueId(), skill.getManaAbility(), false);
                                                    player.sendMessage(AureliumSkills.tag + ChatColor.GRAY + Lang.getMessage(ManaAbilityMessage.valueOf(skill.getManaAbility().name() + "_LOWER"), locale));
                                                }
                                            }
                                        }
                                    }.runTaskLater(plugin, 50L);
                                } else {
                                    if (AureliumSkills.manaAbilityManager.getErrorTimer(player.getUniqueId(), skill.getManaAbility()) == 0) {
                                        player.sendMessage(AureliumSkills.tag + ChatColor.YELLOW + Lang.getMessage(ManaAbilityMessage.NOT_READY, locale).replace("{cooldown}", String.valueOf(AureliumSkills.manaAbilityManager.getCooldown(player.getUniqueId(), skill.getManaAbility()))));
                                        AureliumSkills.manaAbilityManager.setErrorTimer(player.getUniqueId(), skill.getManaAbility(), 2);
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
