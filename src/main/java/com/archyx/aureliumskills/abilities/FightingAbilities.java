package com.archyx.aureliumskills.abilities;


import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.AbilityMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FightingAbilities extends AbilityProvider implements Listener {

    private static final Map<UUID, Integer> firstStrikeCounter = new HashMap<>();
    private final Random r = new Random();

    public FightingAbilities(AureliumSkills plugin) {
        super(plugin, Skill.FIGHTING);
    }

    public void swordMaster(EntityDamageByEntityEvent event, Player player, PlayerSkill playerSkill) {
        if (OptionL.isEnabled(Skill.FIGHTING)) {
            if (plugin.getAbilityManager().isEnabled(Ability.SWORD_MASTER)) {
                if (!player.hasPermission("aureliumskills.fighting")) {
                    return;
                }
                if (playerSkill.getAbilityLevel(Ability.SWORD_MASTER) > 0) {
                    //Modifies damage
                    double modifier = getValue(Ability.SWORD_MASTER, playerSkill) / 100;
                    event.setDamage(event.getDamage() * (1 + modifier));
                }
            }
        }
    }

    public void firstStrike(EntityDamageByEntityEvent event, PlayerSkill playerSkill, Player player) {
        if (OptionL.isEnabled(Skill.FIGHTING)) {
            if (plugin.getAbilityManager().isEnabled(Ability.FIRST_STRIKE)) {
                if (!player.hasMetadata("AureliumSkills-FirstStrike")) {
                    if (playerSkill.getAbilityLevel(Ability.FIRST_STRIKE) > 0) {
                        Locale locale = Lang.getLanguage(player);
                        //Modifies damage
                        double modifier = getValue(Ability.FIRST_STRIKE, playerSkill) / 100;
                        event.setDamage(event.getDamage() * (1 + modifier));
                        if (plugin.getAbilityManager().getOptionAsBooleanElseTrue(Ability.FIRST_STRIKE, "enable_message")) {
                            event.getDamager().sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(AbilityMessage.FIRST_STRIKE_DEALT, locale));
                        }
                        //Adds metadata
                        player.setMetadata("AureliumSkills-FirstStrike", new FixedMetadataValue(plugin, true));
                        //Increments counter
                        if (firstStrikeCounter.containsKey(player.getUniqueId())) {
                            firstStrikeCounter.put(player.getUniqueId(), firstStrikeCounter.get(player.getUniqueId()) + 1);
                        } else {
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
            }
        }
    }

    public void bleed(EntityDamageByEntityEvent event, PlayerSkill playerSkill, LivingEntity entity) {
        if (r.nextDouble() < (getValue(Ability.BLEED, playerSkill) / 100)) {
            if (event.getFinalDamage() < entity.getHealth()) {
                if (!entity.hasMetadata("AureliumSkills-BleedTicks")) {
                    entity.setMetadata("AureliumSkills-BleedTicks", new FixedMetadataValue(plugin, 3));
                    if (plugin.getAbilityManager().getOptionAsBooleanElseTrue(Ability.BLEED, "enable_enemy_message")) {
                        Locale locale = Lang.getLanguage(event.getDamager());
                        event.getDamager().sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(AbilityMessage.BLEED_ENEMY_BLEEDING, locale));
                    }
                    if (entity instanceof Player) {
                        if (plugin.getAbilityManager().getOptionAsBooleanElseTrue(Ability.BLEED, "enable_self_message")) {
                            Locale damagedLocale = Lang.getLanguage(entity);
                            entity.sendMessage(AureliumSkills.getPrefix(damagedLocale) + Lang.getMessage(AbilityMessage.BLEED_SELF_BLEEDING, damagedLocale));
                        }
                    }
                    //Schedules bleed ticks
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (entity.hasMetadata("AureliumSkills-BleedTicks")) {
                                int bleedTicks = entity.getMetadata("AureliumSkills-BleedTicks").get(0).asInt();
                                if (bleedTicks > 0) {
                                    //Apply bleed
                                    double damage = plugin.getAbilityManager().getValue2(Ability.BLEED, playerSkill.getAbilityLevel(Ability.BLEED));
                                    entity.damage(damage);
                                    //Decrement bleed ticks
                                    if (bleedTicks != 1) {
                                        entity.setMetadata("AureliumSkills-BleedTicks", new FixedMetadataValue(plugin, bleedTicks - 1));
                                    } else {
                                        entity.removeMetadata("AureliumSkills-BleedTicks", plugin);
                                    }
                                    return;
                                }
                            }
                            if (entity instanceof Player) {
                                if (plugin.getAbilityManager().getOptionAsBooleanElseTrue(Ability.BLEED, "enable_stop_message")) {
                                    Locale damagedLocale = Lang.getLanguage(entity);
                                    entity.sendMessage(AureliumSkills.getPrefix(damagedLocale) + Lang.getMessage(AbilityMessage.BLEED_STOP, damagedLocale));
                                }
                            }
                            cancel();
                        }
                    }.runTaskTimer(plugin, 40L, 40L);
                } else {
                    int bleedTicks = entity.getMetadata("AureliumSkills-BleedTicks").get(0).asInt();
                    entity.setMetadata("AureliumSkills-BleedTicks", new FixedMetadataValue(plugin, bleedTicks + 2));
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getEntity().removeMetadata("AureliumSkills-BleedTicks", plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void fightingListener(EntityDamageByEntityEvent event) {
        if (OptionL.isEnabled(Skill.FIGHTING)) {
            if (!event.isCancelled()) {
                if (event.getDamager() instanceof Player) {
                    Player player = (Player) event.getDamager();
                    if (blockAbility(player)) return;
                    if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                        //If player used sword
                        if (player.getInventory().getItemInMainHand().getType().name().toUpperCase().contains("SWORD")) {
                            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                            if (isEnabled(Ability.BLEED)) {
                                if (event.getEntity() instanceof LivingEntity) {
                                    bleed(event, playerSkill, (LivingEntity) event.getEntity());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
