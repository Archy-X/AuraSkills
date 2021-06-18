package com.archyx.aureliumskills.skills.fighting;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.ability.AbilityProvider;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.AbilityData;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.AbilityMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.mana.LightningBlade;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;
import java.util.Random;

public class FightingAbilities extends AbilityProvider implements Listener {

    private final Random r = new Random();

    public FightingAbilities(AureliumSkills plugin) {
        super(plugin, Skills.FIGHTING);
    }

    public void swordMaster(EntityDamageByEntityEvent event, Player player, PlayerData playerData) {
        if (OptionL.isEnabled(Skills.FIGHTING)) {
            if (plugin.getAbilityManager().isEnabled(Ability.SWORD_MASTER)) {
                if (!player.hasPermission("aureliumskills.fighting")) {
                    return;
                }
                if (playerData.getAbilityLevel(Ability.SWORD_MASTER) > 0) {
                    //Modifies damage
                    double modifier = getValue(Ability.SWORD_MASTER, playerData) / 100;
                    event.setDamage(event.getDamage() * (1 + modifier));
                }
            }
        }
    }

    public void firstStrike(EntityDamageByEntityEvent event, PlayerData playerData, Player player) {
        if (OptionL.isEnabled(Skills.FIGHTING)) {
            if (plugin.getAbilityManager().isEnabled(Ability.FIRST_STRIKE)) {
                if (!player.hasMetadata("AureliumSkills-FirstStrike")) {
                    if (playerData.getAbilityLevel(Ability.FIRST_STRIKE) > 0) {
                        Locale locale = playerData.getLocale();
                        //Modifies damage
                        double modifier = getValue(Ability.FIRST_STRIKE, playerData) / 100;
                        event.setDamage(event.getDamage() * (1 + modifier));
                        if (plugin.getAbilityManager().getOptionAsBooleanElseTrue(Ability.FIRST_STRIKE, "enable_message")) {
                            plugin.getAbilityManager().sendMessage(player, Lang.getMessage(AbilityMessage.FIRST_STRIKE_DEALT, locale));
                        }
                        //Adds metadata
                        player.setMetadata("AureliumSkills-FirstStrike", new FixedMetadataValue(plugin, true));
                        //Increments counter
                        AbilityData abilityData = playerData.getAbilityData(Ability.FIRST_STRIKE);
                        if (abilityData.containsKey("counter")) {
                            abilityData.setData("counter", abilityData.getInt("counter") + 1);
                        } else {
                            abilityData.setData("counter", 0);
                        }
                        int id = abilityData.getInt("counter");
                        //Schedules metadata removal
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (playerData.getAbilityData(Ability.FIRST_STRIKE).containsKey("counter")) {
                                    if (playerData.getAbilityData(Ability.FIRST_STRIKE).getInt("counter") == id) {
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

    public void bleed(EntityDamageByEntityEvent event, PlayerData playerData, LivingEntity entity) {
        if (r.nextDouble() < (getValue(Ability.BLEED, playerData) / 100)) {
            if (event.getFinalDamage() < entity.getHealth()) {
                if (!entity.hasMetadata("AureliumSkills-BleedTicks")) {
                    entity.setMetadata("AureliumSkills-BleedTicks", new FixedMetadataValue(plugin, 3));
                    if (plugin.getAbilityManager().getOptionAsBooleanElseTrue(Ability.BLEED, "enable_enemy_message")) {
                        Locale locale = playerData.getLocale();
                        if (event.getDamager() instanceof Player) {
                            Player player = (Player) event.getDamager();
                            plugin.getAbilityManager().sendMessage(player, Lang.getMessage(AbilityMessage.BLEED_ENEMY_BLEEDING, locale));
                        }
                    }
                    if (entity instanceof Player) {
                        if (plugin.getAbilityManager().getOptionAsBooleanElseTrue(Ability.BLEED, "enable_self_message")) {
                            Player player = (Player) entity;
                            Locale locale = plugin.getLang().getLocale(player);
                            plugin.getAbilityManager().sendMessage(player, Lang.getMessage(AbilityMessage.BLEED_SELF_BLEEDING, locale));
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
                                    double damage = plugin.getAbilityManager().getValue2(Ability.BLEED, playerData.getAbilityLevel(Ability.BLEED));
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
                                    Player player = (Player) entity;
                                    Locale locale = plugin.getLang().getLocale(player);
                                    plugin.getAbilityManager().sendMessage(player, Lang.getMessage(AbilityMessage.BLEED_STOP, locale));
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
    public void onDeath(PlayerRespawnEvent event) {
        event.getPlayer().removeMetadata("AureliumSkills-BleedTicks", plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void fightingListener(EntityDamageByEntityEvent event) {
        if (OptionL.isEnabled(Skills.FIGHTING)) {
            if (!event.isCancelled()) {
                if (event.getDamager() instanceof Player) {
                    Player player = (Player) event.getDamager();
                    if (blockAbility(player)) return;
                    //If player used sword
                    if (player.getInventory().getItemInMainHand().getType().name().toUpperCase().contains("SWORD")) {
                        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                        if (playerData == null) return;
                        if (isEnabled(Ability.BLEED)) {
                            if (event.getEntity() instanceof LivingEntity) {
                                bleed(event, playerData, (LivingEntity) event.getEntity());
                                applyLightningBlade(player);
                            }
                        }
                    }
                }
            }
        }
    }

    public void applyLightningBlade(Player player) {
        // Checks if already activated
        ManaAbilityManager manager = plugin.getManaAbilityManager();
        if (manager.isActivated(player.getUniqueId(), MAbility.LIGHTNING_BLADE)) {
            return;
        }
        // Checks if ready
        if (manager.isReady(player.getUniqueId(), MAbility.LIGHTNING_BLADE)) {
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData == null) return;
            Locale locale = playerData.getLocale();
            if (playerData.getMana() >= getManaCost(MAbility.LIGHTNING_BLADE, playerData)) {
                // Calculate duration
                double baseDuration = plugin.getManaAbilityManager().getOptionAsDouble(MAbility.LIGHTNING_BLADE, "base_duration");
                double durationPerLevel = plugin.getManaAbilityManager().getOptionAsDouble(MAbility.LIGHTNING_BLADE, "duration_per_level");
                double durationSeconds = baseDuration + (durationPerLevel * (playerData.getManaAbilityLevel(MAbility.LIGHTNING_BLADE) - 1));
                manager.activateAbility(player, MAbility.LIGHTNING_BLADE, (int) Math.round(durationSeconds * 20), new LightningBlade(plugin));
            }
            else {
                plugin.getAbilityManager().sendMessage(player, TextUtil.replace(Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale)
                        ,"{mana}", NumberUtil.format0(plugin.getManaAbilityManager().getManaCost(MAbility.LIGHTNING_BLADE, playerData))
                        , "{current_mana}", String.valueOf(Math.round(playerData.getMana()))
                        , "{max_mana}", String.valueOf(Math.round(playerData.getMaxMana()))));
            }
        }
    }

    @EventHandler
    public void readyLightningBlade(PlayerInteractEvent event) {
        plugin.getManaAbilityManager().getActivator().readyAbility(event, Skills.FIGHTING, new String[] {"SWORD"}, Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR);
    }

    @EventHandler
    public void lightningBladeJoin(PlayerJoinEvent event) {
        // Only remove if not activated
        Player player = event.getPlayer();
        if (plugin.getManaAbilityManager().isActivated(player.getUniqueId(), MAbility.LIGHTNING_BLADE)) {
            return;
        }
        // Remove attack speed attribute modifier
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute == null) return;
        for (AttributeModifier modifier : attribute.getModifiers()) {
            if (modifier.getName().equals("AureliumSkills-LightningBlade")) {
                attribute.removeModifier(modifier);
            }
        }
    }

}
