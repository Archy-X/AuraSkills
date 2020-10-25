package com.archyx.aureliumskills.skills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.AbilityMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.Source;
import com.archyx.aureliumskills.util.LoreUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.GameMode;
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

public class AgilityAbilities implements Listener {

    private final Random r = new Random();
    private final Plugin plugin;

    public AgilityAbilities(Plugin plugin) {
        this.plugin = plugin;
    }

    public static double getModifiedXp(Player player, Source source) {
        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
        double output = OptionL.getXp(source);
        if (AureliumSkills.abilityOptionManager.isEnabled(Ability.JUMPER)) {
            double modifier = 1;
            modifier += Ability.JUMPER.getValue(skill.getAbilityLevel(Ability.JUMPER)) / 100;
            output *= modifier;
        }
        return output;
    }

    public static double getModifiedXp(Player player, double xp) {
        PlayerSkill skill = SkillLoader.playerSkills.get(player.getUniqueId());
        double output = xp;
        if (AureliumSkills.abilityOptionManager.isEnabled(Ability.JUMPER)) {
            double modifier = 1;
            modifier += Ability.JUMPER.getValue(skill.getAbilityLevel(Ability.JUMPER)) / 100;
            output *= modifier;
        }
        return output;
    }

    private void lightFall(EntityDamageEvent event, PlayerSkill playerSkill) {
        if (event.getFinalDamage() > 0.0) {
            double percentReduction = Ability.LIGHT_FALL.getValue(playerSkill.getAbilityLevel(Ability.LIGHT_FALL));
            event.setDamage(event.getDamage() * (1 - (percentReduction / 100)));
        }
    }

    // For potion splashes
    @EventHandler(priority = EventPriority.HIGHEST)
    public void sugarRush(PotionSplashEvent event) {
        if (!event.isCancelled()) {
            if (OptionL.isEnabled(Skill.ALCHEMY)) {
                if (AureliumSkills.abilityOptionManager.isEnabled(Ability.SUGAR_RUSH)) {
                    for (PotionEffect effect : event.getPotion().getEffects()) {
                        if (effect.getType().equals(PotionEffectType.SPEED) || effect.getType().equals(PotionEffectType.JUMP)) {
                            for (LivingEntity entity : event.getAffectedEntities()) {
                                if (entity instanceof Player) {
                                    Player player = (Player) entity;
                                    //Checks if in blocked world
                                    if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
                                        return;
                                    }
                                    //Check for permission
                                    if (!player.hasPermission("aureliumskills.agility")) {
                                        return;
                                    }
                                    //Check creative mode disable
                                    if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
                                        if (player.getGameMode().equals(GameMode.CREATIVE)) {
                                            return;
                                        }
                                    }
                                    PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                                    if (playerSkill != null) {
                                        if (playerSkill.getAbilityLevel(Ability.SUGAR_RUSH) > 0) {
                                            double intensity = event.getIntensity(player);
                                            double multiplier = 1 + (Ability.SUGAR_RUSH.getValue(playerSkill.getAbilityLevel(Ability.SUGAR_RUSH)) / 100);
                                            applyEffect(player, new PotionEffect(effect.getType(), (int) (effect.getDuration() * multiplier * intensity), effect.getAmplifier()));
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

    // For potion drinking
    @EventHandler(priority = EventPriority.MONITOR)
    public void sugarRushDrink(PlayerItemConsumeEvent event) {
        if (!event.isCancelled()) {
            if (OptionL.isEnabled(Skill.ALCHEMY)) {
                if (AureliumSkills.abilityOptionManager.isEnabled(Ability.SUGAR_RUSH)) {
                    Player player = event.getPlayer();
                    //Checks if in blocked world
                    if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
                        return;
                    }
                    //Check for permission
                    if (!player.hasPermission("aureliumskills.agility")) {
                        return;
                    }
                    //Check creative mode disable
                    if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
                        if (player.getGameMode().equals(GameMode.CREATIVE)) {
                            return;
                        }
                    }
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
                                        applyEffect(player, new PotionEffect(potionEffectType, duration * 20, amplifier));
                                    }
                                    // Apply custom effects
                                    if (meta.hasCustomEffects()) {
                                        for (PotionEffect effect : meta.getCustomEffects()) {
                                            if (effect.getType().equals(PotionEffectType.SPEED) || effect.getType().equals(PotionEffectType.JUMP)) {
                                                applyEffect(player, new PotionEffect(effect.getType(), (int) (effect.getDuration() * multiplier), effect.getAmplifier()));
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
    }

    private void applyEffect(Player player, PotionEffect effect) {
        if (XMaterial.isNewVersion()) {
            player.addPotionEffect(effect);
        }
        else {
            PotionEffect currentEffect = player.getPotionEffect(effect.getType());
            // Force apply the effect if effect has greater amplifier or longer duration if same amplifier
            if (currentEffect != null) {
                if (effect.getDuration() > currentEffect.getDuration() && effect.getAmplifier() == currentEffect.getAmplifier()) {
                    player.addPotionEffect(effect, true);
                }
                else if (effect.getAmplifier() > currentEffect.getAmplifier()) {
                    player.addPotionEffect(effect, true);
                }
            }
            else {
                player.addPotionEffect(effect);
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
                float boostFactor = 1 + ((float) percent/ 100);
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void fleetingEnd(EntityRegainHealthEvent event) {
        if (!event.isCancelled()) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                //Checks if in blocked world
                if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
                    return;
                }
                //Check for permission
                if (!player.hasPermission("aureliumskills.agility")) {
                    return;
                }
                //Check creative mode disable
                if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
                    if (player.getGameMode().equals(GameMode.CREATIVE)) {
                        return;
                    }
                }
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
                // If from fall damage
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    if (event.getEntity() instanceof Player) {
                        Player player = (Player) event.getEntity();
                        //Checks if in blocked world
                        if (AureliumSkills.worldManager.isInDisabledWorld(player.getLocation())) {
                            return;
                        }
                        //Check for permission
                        if (!player.hasPermission("aureliumskills.agility")) {
                            return;
                        }
                        //Check creative mode disable
                        if (OptionL.getBoolean(Option.DISABLE_IN_CREATIVE_MODE)) {
                            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                                return;
                            }
                        }
                        PlayerSkill playerSkill = SkillLoader.playerSkills.get(player.getUniqueId());
                        if (playerSkill != null) {
                            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.THUNDER_FALL)) {
                                // Activate thunder fall
                                thunderFall(event, playerSkill, player);
                            }
                            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.LIGHT_FALL)) {
                                // Activate light fall
                                lightFall(event, playerSkill);
                            }
                            if (AureliumSkills.abilityOptionManager.isEnabled(Ability.FLEETING)) {
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
