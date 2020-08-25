package com.archyx.aureliumskills.skills.abilities;


import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class FightingAbilities implements Listener {

    private final Plugin plugin;
    private static final Map<UUID, Integer> firstStrikeCounter = new HashMap<>();
    private final Random r = new Random();

    public FightingAbilities(Plugin plugin) {
        this.plugin = plugin;
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

    public static void swordMaster(EntityDamageByEntityEvent event, Player player, PlayerSkill playerSkill) {
        if (Options.isEnabled(Skill.FIGHTING)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.SWORD_MASTER)) {
                if (!player.hasPermission("aureliumskills.fighting")) {
                    return;
                }
                if (playerSkill.getAbilityLevel(Ability.SWORD_MASTER) > 0) {
                    //Modifies damage
                    double modifier = Ability.SWORD_MASTER.getValue(playerSkill.getAbilityLevel(Ability.SWORD_MASTER)) / 100;
                    event.setDamage(event.getDamage() * (1 + modifier));
                }
            }
        }
    }

    public static void firstStrike(EntityDamageByEntityEvent event, PlayerSkill playerSkill, Player player, Plugin plugin) {
        if (Options.isEnabled(Skill.FIGHTING)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.FIRST_STRIKE)) {
                if (!player.hasMetadata("AureliumSkills-FirstStrike")) {
                    if (playerSkill.getAbilityLevel(Ability.FIRST_STRIKE) > 0) {
                        //Modifies damage
                        double modifier = Ability.FIRST_STRIKE.getValue(playerSkill.getAbilityLevel(Ability.FIRST_STRIKE)) / 100;
                        event.setDamage(event.getDamage() * (1 + modifier));
                        event.getDamager().sendMessage(AureliumSkills.tag + Lang.getMessage(Message.FIRST_STRIKE_ACTIVATE).replace("&", "§"));
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
        if (r.nextDouble() < (Ability.BLEED.getValue(playerSkill.getAbilityLevel(Ability.BLEED)) / 100)) {
            if (event.getFinalDamage() < entity.getHealth()) {
                if (!entity.hasMetadata("AureliumSkills-BleedTicks")) {
                    entity.setMetadata("AureliumSkills-BleedTicks", new FixedMetadataValue(plugin, 3));
                    event.getDamager().sendMessage(AureliumSkills.tag + Lang.getMessage(Message.BLEED_ACTIVATE).replace("&", "§"));
                    if (entity instanceof Player) {
                        entity.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.BLEED_BLEEDING).replace("&", "§"));
                    }
                    //Schedules bleed ticks
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (entity.hasMetadata("AureliumSkills-BleedTicks")) {
                                int bleedTicks = entity.getMetadata("AureliumSkills-BleedTicks").get(0).asInt();
                                if (bleedTicks > 0) {
                                    //Apply bleed
                                    double damage = Ability.BLEED.getValue2(playerSkill.getAbilityLevel(Ability.BLEED));
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
                                entity.sendMessage(AureliumSkills.tag + Lang.getMessage(Message.BLEED_STOP).replace("&", "§"));
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
        if (Options.isEnabled(Skill.FIGHTING)) {
            if (!event.isCancelled()) {
                if (event.getDamager() instanceof Player) {
                    Player player = (Player) event.getDamager();
                    //Check for permission
                    if (!player.hasPermission("aureliumskills.fighting")) {
                        return;
                    }
                    //Check disabled worlds
                    if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
                        return;
                    }
                    if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                        //If player used sword
                        if (player.getInventory().getItemInMainHand().getType().name().toUpperCase().contains("SWORD")) {
                            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                            AbilityOptionManager options = AureliumSkills.abilityOptionManager;
                            if (options.isEnabled(Ability.BLEED)) {
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
