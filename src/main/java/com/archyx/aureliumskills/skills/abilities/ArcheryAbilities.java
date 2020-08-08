package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.Options;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class ArcheryAbilities implements Listener {

    private static Random r = new Random();

    private Plugin plugin;

    public ArcheryAbilities(Plugin plugin) {
        this.plugin = plugin;
    }

    public static double getModifiedXp(Player player, Source source) {
        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
        double output = Options.getXpAmount(source);
        if (AureliumSkills.abilityOptionManager.isEnabled(Ability.ARCHER)) {
            double modifier = 1;
            modifier += Ability.ARCHER.getValue(skill.getAbilityLevel(Ability.ARCHER)) / 100;
            output *= modifier;
        }
        return output;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void applyCrit(EntityDamageByEntityEvent event) {
        if (Options.isEnabled(Skill.ARCHERY)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.CRIT_CHANCE)) {
                //Checks if damage is from arrow
                if (event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    //Checks if player shot the arrow
                    if (arrow.getShooter() instanceof Player) {
                        Player player = (Player) arrow.getShooter();
                        //Applies damage multiplier
                        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                            if (Critical.isCrit(player)) {
                                event.setDamage(event.getDamage() * Critical.getCritMultiplier(player));
                                player.setMetadata("skillsCritical", new FixedMetadataValue(plugin, true));
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        player.removeMetadata("skillsCritical", plugin);
                                    }
                                }.runTaskLater(plugin, 1L);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void bowMaster(EntityDamageByEntityEvent event) {
        if (Options.isEnabled(Skill.ARCHERY)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.BOW_MASTER)) {
                //Checks if damage is from arrow
                if (event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    //Checks if player shot the arrow
                    if (arrow.getShooter() instanceof Player) {
                        Player player = (Player) arrow.getShooter();
                        //Applies damage multiplier
                        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                            PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
                            double multiplier = 1 + (Ability.BOW_MASTER.getValue(skill.getAbilityLevel(Ability.BOW_MASTER)) / 100);
                            event.setDamage(event.getDamage() * multiplier);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void stun(EntityDamageByEntityEvent event) {
        if (Options.isEnabled(Skill.ARCHERY)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.STUN)) {
                if (event.getEntity() instanceof LivingEntity) {
                    LivingEntity entity = (LivingEntity) event.getEntity();
                    //Checks if damage is from arrow
                    if (event.getDamager() instanceof Arrow) {
                        Arrow arrow = (Arrow) event.getDamager();
                        //Checks if player shot the arrow
                        if (arrow.getShooter() instanceof Player) {
                            Player player = (Player) arrow.getShooter();
                            if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                                PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
                                if (r.nextDouble() < (Ability.STUN.getValue(skill.getAbilityLevel(Ability.STUN)) / 100)) {
                                    if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
                                        //Applies stun
                                        double reducedSpeed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 0.2;
                                        AttributeModifier modifier = new AttributeModifier("skillsStun", -1 * reducedSpeed, AttributeModifier.Operation.ADD_NUMBER);
                                        player.setMetadata("skillsStun", new FixedMetadataValue(plugin, true));
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                player.removeMetadata("skillsStun", plugin);
                                            }
                                        }.runTaskLater(plugin, 1L);
                                        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(modifier);
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
                                                    entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(modifier);
                                                }
                                            }
                                        }.runTaskLater(plugin, 40L);
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
    public void piercing(EntityDamageByEntityEvent event) {
        if (Options.isEnabled(Skill.ARCHERY)) {
            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.PIERCING)) {
                //Checks if damage is from arrow
                if (event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    //Checks if player shot the arrow
                    if (arrow.getShooter() instanceof Player) {
                        Player player = (Player) arrow.getShooter();
                        if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
                            PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
                            if (r.nextDouble() < (Ability.PIERCING.getValue(skill.getAbilityLevel(Ability.PIERCING)) / 100)) {
                                arrow.setBounce(false);
                                Vector velocity = arrow.getVelocity();
                                Arrow newArrow = event.getEntity().getWorld().spawnArrow(arrow.getLocation(), velocity, (float) velocity.length(), 0.0f);
                                newArrow.setShooter(player);
                                newArrow.setKnockbackStrength(arrow.getKnockbackStrength());
                                newArrow.setFireTicks(arrow.getFireTicks());
                            }
                        }
                    }
                }
            }
        }
    }

}
