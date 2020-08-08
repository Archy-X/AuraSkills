package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class DefenseAbilities implements Listener {

    private Random r = new Random();
    private Plugin plugin;

    public DefenseAbilities(Plugin plugin) {
        this.plugin = plugin;
    }

    public static double getModifiedXp(Player player, Source source) {
        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
        double output = Options.getXpAmount(source);
        if (AureliumSkills.abilityOptionManager.isEnabled(Ability.DEFENDER)) {
            double modifier = 1;
            modifier += Ability.DEFENDER.getValue(skill.getAbilityLevel(Ability.DEFENDER)) / 100;
            output *= modifier;
        }
        return output;
    }

    public static double getModifiedXp(Player player, double base) {
        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
        double output = base;
        if (AureliumSkills.abilityOptionManager.isEnabled(Ability.DEFENDER)) {
            double modifier = 1;
            modifier += Ability.DEFENDER.getValue(skill.getAbilityLevel(Ability.DEFENDER)) / 100;
            output *= modifier;
        }
        return output;
    }

    @EventHandler
    public void shielding(EntityDamageByEntityEvent event) {
        if (Options.isEnabled(Skill.DEFENSE)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.SHIELDING)) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    if (player.isSneaking()) {
                        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                            PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
                            double damageReduction = 1 - (Ability.SHIELDING.getValue(skill.getAbilityLevel(Ability.SHIELDING)) / 100);
                            event.setDamage(event.getDamage() * damageReduction);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void mobMaster(EntityDamageByEntityEvent event) {
        if (Options.isEnabled(Skill.DEFENSE)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.MOB_MASTER)) {
                if (event.getEntity() instanceof Player && event.getDamager() instanceof LivingEntity) {
                    Player player = (Player) event.getEntity();
                    if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
                        double damageReduction = 1 - (Ability.MOB_MASTER.getValue(skill.getAbilityLevel(Ability.MOB_MASTER)) / 100);
                        event.setDamage(event.getDamage() * damageReduction);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void immunity(EntityDamageByEntityEvent event) {
        if (Options.isEnabled(Skill.DEFENSE)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.IMMUNITY)) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
                        double chance = Ability.IMMUNITY.getValue(skill.getAbilityLevel(Ability.IMMUNITY)) / 100;
                        if (r.nextDouble() < chance) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void noDebuff(PotionSplashEvent event) {
        if (Options.isEnabled(Skill.DEFENSE)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.NO_DEBUFF)) {
                for (LivingEntity entity : event.getAffectedEntities()) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        for (PotionEffect effect : event.getPotion().getEffects()) {
                            PotionEffectType type = effect.getType();
                            if (type.equals(PotionEffectType.POISON) || type.equals(PotionEffectType.UNLUCK) || type.equals(PotionEffectType.WITHER) ||
                                    type.equals(PotionEffectType.WEAKNESS) || type.equals(PotionEffectType.SLOW_DIGGING) || type.equals(PotionEffectType.SLOW) ||
                                    type.equals(PotionEffectType.HUNGER) || type.equals(PotionEffectType.HARM) || type.equals(PotionEffectType.CONFUSION) ||
                                    type.equals(PotionEffectType.BLINDNESS)) {
                                if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                                    PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
                                    double chance = Ability.NO_DEBUFF.getValue(skill.getAbilityLevel(Ability.NO_DEBUFF)) / 100;
                                    if (r.nextDouble() < chance) {
                                        if (!player.hasPotionEffect(type)) {
                                            event.setIntensity(entity, 0);
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

    @EventHandler
    public void noDebuffFire(EntityDamageByEntityEvent event) {
        if (Options.isEnabled(Skill.DEFENSE)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.NO_DEBUFF)) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    if (event.getDamager() instanceof LivingEntity) {
                        LivingEntity entity = (LivingEntity) event.getDamager();
                        if (entity.getEquipment() != null) {
                            if (entity.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT) > 0) {
                                if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                                    PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
                                    double chance = Ability.NO_DEBUFF.getValue(skill.getAbilityLevel(Ability.NO_DEBUFF)) / 100;
                                    if (r.nextDouble() < chance) {
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                player.setFireTicks(0);
                                            }
                                        }.runTaskLater(plugin, 1L);
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
