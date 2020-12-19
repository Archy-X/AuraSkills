package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.AbilityMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.util.LoreUtil;
import com.archyx.aureliumskills.util.PotionUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class AgilityAbilities extends AbilityProvider implements Listener {

    private final Random r = new Random();
    private static Plugin plugin;

    public AgilityAbilities(AureliumSkills plugin) {
        super(plugin, Skill.AGILITY);
        AgilityAbilities.plugin = plugin;
    }

    private void lightFall(EntityDamageEvent event, PlayerSkill playerSkill) {
        if (event.getFinalDamage() > 0.0) {
            double percentReduction = Ability.LIGHT_FALL.getValue(playerSkill.getAbilityLevel(Ability.LIGHT_FALL));
            event.setDamage(event.getDamage() * (1 - (percentReduction / 100)));
        }
    }

// For potion splashes
    @EventHandler(priority = EventPriority.HIGH)
    public void sugarRushSplash(PotionSplashEvent event) {
        if (!event.isCancelled()) {
            if (blockDisabled(Ability.SUGAR_RUSH)) return;
            for (PotionEffect effect : event.getPotion().getEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED) || effect.getType().equals(PotionEffectType.JUMP)) {
                    for (LivingEntity entity : event.getAffectedEntities()) {
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            if (blockAbility(player)) return;
                            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                            if (playerSkill != null) {
                                if (playerSkill.getAbilityLevel(Ability.SUGAR_RUSH) > 0) {
                                    double intensity = event.getIntensity(player);
                                    double multiplier = 1 + (Ability.SUGAR_RUSH.getValue(playerSkill.getAbilityLevel(Ability.SUGAR_RUSH)) / 100);
                                    PotionUtil.applyEffect(player, new PotionEffect(effect.getType(), (int) (effect.getDuration() * multiplier * intensity), effect.getAmplifier()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static double getSugarRushSplashMultiplier(Player player) {
        if (player.hasPermission("aureliumskills.agility") && AureliumSkills.abilityOptionManager.isEnabled(Ability.SUGAR_RUSH)) {
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                if (playerSkill.getAbilityLevel(Ability.SUGAR_RUSH) > 0) {
                    return 1 + (Ability.SUGAR_RUSH.getValue(playerSkill.getAbilityLevel(Ability.SUGAR_RUSH)) / 100);
                }
            }
        }
        return 1.0;
    }

    // For potion drinking
    @EventHandler(priority = EventPriority.HIGH)
    public void sugarRushDrink(PlayerItemConsumeEvent event) {
        if (!event.isCancelled()) {
            if (blockDisabled(Ability.SUGAR_RUSH)) return;
            Player player = event.getPlayer();
            if (blockAbility(player)) return;
            PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
            if (playerSkill != null) {
                if (playerSkill.getAbilityLevel(Ability.SUGAR_RUSH) > 0) {
                    ItemStack item = event.getItem();
                    if (item.getType() == Material.POTION) {
                        if (item.getItemMeta() instanceof PotionMeta) {
                            PotionMeta meta = (PotionMeta) item.getItemMeta();
                            PotionData potion = meta.getBasePotionData();
                            double multiplier = 1 + (Ability.SUGAR_RUSH.getValue(playerSkill.getAbilityLevel(Ability.SUGAR_RUSH)) / 100);
                            if (potion.getType() == PotionType.SPEED || potion.getType() == PotionType.JUMP) {
                                int amplifier = 0;
                                if (potion.isUpgraded()) {
                                    amplifier = 1;
                                }
                                PotionEffectType potionEffectType;
                                if (potion.getType() == PotionType.SPEED) {
                                    potionEffectType = PotionEffectType.SPEED;
                                } else {
                                    potionEffectType = PotionEffectType.JUMP;
                                }
                                int duration;
                                if (potion.isExtended()) {
                                    duration = 480;
                                } else if (potion.isUpgraded()) {
                                    duration = 90;
                                } else {
                                    duration = 180;
                                }
                                duration = (int) (multiplier * duration);
                                PotionUtil.applyEffect(player, new PotionEffect(potionEffectType, duration * 20, amplifier));
                            }
                            // Apply custom effects
                            if (meta.hasCustomEffects()) {
                                for (PotionEffect effect : meta.getCustomEffects()) {
                                    if (effect.getType().equals(PotionEffectType.SPEED) || effect.getType().equals(PotionEffectType.JUMP)) {
                                        PotionUtil.applyEffect(player, new PotionEffect(effect.getType(), (int) (effect.getDuration() * multiplier), effect.getAmplifier()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void fleeting(EntityDamageEvent event, PlayerSkill playerSkill, Player player) {
        AttributeInstance attribute = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH));
        double maxHealth = attribute.getValue();
        // If less than 20% health
        if (player.getHealth() - event.getFinalDamage() < 0.2 * maxHealth) {
            if (!player.hasMetadata("AureliumSkills-Fleeting")) {
                double percent = Ability.FLEETING.getValue(playerSkill.getAbilityLevel(Ability.FLEETING));
                float boostFactor = 1 + ((float) percent / 100);
                float newSpeed = player.getWalkSpeed() * boostFactor;
                if (newSpeed > 1) {
                    newSpeed = 1.0f;
                    percent = (newSpeed / player.getWalkSpeed() - 1) * 100;
                }
                float walkSpeedChange = newSpeed - player.getWalkSpeed();
                player.setWalkSpeed(newSpeed);
                player.setMetadata("AureliumSkills-Fleeting", new FixedMetadataValue(plugin, walkSpeedChange));
                Locale locale = Lang.getLanguage(player);
                player.sendMessage(AureliumSkills.getPrefix(locale) + LoreUtil.replace(Lang.getMessage(AbilityMessage.FLEETING_START, locale), "{value}", String.valueOf((int) percent)));
            }
        }
    }

    public static void removeFleeting(Player player) {
        AttributeInstance attribute = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH));
        double maxHealth = attribute.getValue();
        if (player.getHealth() >= 0.2 * maxHealth) {
            if (player.hasMetadata("AureliumSkills-Fleeting")) {
                float walkSpeedChange = player.getMetadata("AureliumSkills-Fleeting").get(0).asFloat();
                player.setWalkSpeed(player.getWalkSpeed() - walkSpeedChange);
                player.removeMetadata("AureliumSkills-Fleeting", plugin);
                Locale locale = Lang.getLanguage(player);
                player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(AbilityMessage.FLEETING_END, locale));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void fleetingEnd(EntityRegainHealthEvent event) {
        if (!event.isCancelled()) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (blockAbility(player)) return;
                AttributeInstance attribute = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH));
                double maxHealth = attribute.getValue();
                if (player.getHealth() + event.getAmount() >= 0.2 * maxHealth) {
                    if (player.hasMetadata("AureliumSkills-Fleeting")) {
                        float walkSpeedChange = player.getMetadata("AureliumSkills-Fleeting").get(0).asFloat();
                        player.setWalkSpeed(player.getWalkSpeed() - walkSpeedChange);
                        player.removeMetadata("AureliumSkills-Fleeting", plugin);
                        Locale locale = Lang.getLanguage(player);
                        player.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(AbilityMessage.FLEETING_END, locale));
                    }
                }
            }
        }
    }

    @EventHandler
    public void fleetingDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasMetadata("AureliumSkills-Fleeting")) {
            float walkSpeedChange = player.getMetadata("AureliumSkills-Fleeting").get(0).asFloat();
            player.setWalkSpeed(player.getWalkSpeed() - walkSpeedChange);
            player.removeMetadata("AureliumSkills-Fleeting", plugin);
        }
    }

    public void thunderFall(EntityDamageEvent event, PlayerSkill playerSkill, Player player) {
        if (player.isSneaking()) {
            // If chance
            if (r.nextDouble() < Ability.THUNDER_FALL.getValue(playerSkill.getAbilityLevel(Ability.THUNDER_FALL)) / 100) {
                // Get damage values
                double percent = Ability.THUNDER_FALL.getValue2(playerSkill.getAbilityLevel(Ability.THUNDER_FALL));
                double thunderFallDamage = (percent / 100) * event.getDamage();
                // Get entities nearby
                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 3, 3, 1)) {
                    if (entity instanceof LivingEntity && !entity.equals(player)) {
                        // Damage the entity
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.damage(thunderFallDamage, player);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void agilityListener(EntityDamageEvent event) {
        if (!event.isCancelled()) {
            if (OptionL.isEnabled(Skill.AGILITY)) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    if (blockAbility(player)) return;
                    PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                    if (playerSkill != null) {
                        // If from fall damage
                        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.THUNDER_FALL)) {
                                if (playerSkill.getAbilityLevel(Ability.THUNDER_FALL) > 0) {
                                    // Activate thunder fall
                                    thunderFall(event, playerSkill, player);
                                }
                            }
                            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.LIGHT_FALL)) {
                                if (playerSkill.getAbilityLevel(Ability.LIGHT_FALL) > 0) {
                                    // Activate light fall
                                    lightFall(event, playerSkill);
                                }
                            }
                        }
                        if (AureliumSkills.abilityOptionManager.isEnabled(Ability.FLEETING)) {
                            if (playerSkill.getAbilityLevel(Ability.FLEETING) > 0) {
                                // Activate fleeting
                                fleeting(event, playerSkill, player);
                            }
                        }
                    }
                }
            }
        }
    }

}
