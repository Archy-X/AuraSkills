package com.archyx.aureliumskills.skills.abilities;


import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FightingAbilities implements Listener {

    private Plugin plugin;
    private Map<UUID, Integer> firstStrikeCounter;

    public FightingAbilities(Plugin plugin) {
        this.plugin = plugin;
        firstStrikeCounter = new HashMap<>();
    }

    public static double getModifiedXp(Player player, Source source) {
        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
        double output = Options.getXpAmount(source);
        if (AureliumSkills.abilityOptionManager.isEnabled(Ability.FIGHTER)) {
            double modifier = 1;
            modifier += Ability.FIGHTER.getValue(skill.getAbilityLevel(Ability.FIGHTER)) / 100;
            output *= modifier;
        }
        return output;
    }

    public void applyCrit(EntityDamageByEntityEvent event, PlayerSkill playerSkill, Player player) {
        //Checks if crit should be applied
        if (Critical.isCrit(playerSkill)) {
            //Modifies damage
            event.setDamage(event.getDamage() * Critical.getCritMultiplier(playerSkill));
            //Sets metadata for damage indicators
            player.setMetadata("skillsCritical", new FixedMetadataValue(plugin, true));
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.removeMetadata("skillsCritical", plugin);
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    public void swordMaster(EntityDamageByEntityEvent event, PlayerSkill playerSkill) {
        //Modifies damage
        double modifier = Ability.SWORD_MASTER.getValue(playerSkill.getAbilityLevel(Ability.SWORD_MASTER)) / 100;
        event.setDamage(event.getDamage() * (1 + modifier));
    }

    public void firstStrike(EntityDamageByEntityEvent event, PlayerSkill playerSkill, Player player) {
        if (!player.hasMetadata("AureliumSkills-FirstStrike")) {
            //Modifies damage
            double modifier = Ability.FIRST_STRIKE.getValue(playerSkill.getAbilityLevel(Ability.FIRST_STRIKE)) / 100;
            event.setDamage(event.getDamage() + (1 + modifier));
            //Adds metadata
            player.setMetadata("AureliumSkills-FirstStrike", new FixedMetadataValue(plugin, true));
            //Increments counter
            if (firstStrikeCounter.containsKey(player.getUniqueId())) {
                firstStrikeCounter.put(player.getUniqueId(), firstStrikeCounter.get(player) + 1);
            }
            else {
                firstStrikeCounter.put(player.getUniqueId(), 0);
            }
            int id = firstStrikeCounter.get(player.getUniqueId());
            //Schedules metadata removal
            new BukkitRunnable() {
                @Override
                public void run() {
                    //Remove if this event was the last hit
                    if (firstStrikeCounter.containsKey(player.getUniqueId())) {
                        if (firstStrikeCounter.get(player.getUniqueId()) == id) {
                            player.removeMetadata("AureliumSkills-FirstStrike", plugin);
                        }
                    }
                }
            }.runTaskLater(plugin, 6000L);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void fightingListener(EntityDamageByEntityEvent event) {
        if (Options.isEnabled(Skill.FIGHTING)) {
            if (!event.isCancelled()) {
                if (event.getDamager() instanceof Player) {
                    Player player = (Player) event.getDamager();
                    if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                        //If player used sword
                        if (player.getInventory().getItemInMainHand().getType().name().toUpperCase().contains("SWORD")) {
                            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                            AbilityOptionManager options = AureliumSkills.abilityOptionManager;
                            //Applies abilities
                            if (options.isEnabled(Ability.SWORD_MASTER)) {
                                swordMaster(event, playerSkill);
                            }
                            if (options.isEnabled(Ability.FIRST_STRIKE)) {
                                firstStrike(event, playerSkill, player);
                            }
                            if (options.isEnabled(Ability.CRIT_CHANCE)) {
                                applyCrit(event, playerSkill, player);
                            }
                        }
                    }
                }
            }
        }
    }
}
