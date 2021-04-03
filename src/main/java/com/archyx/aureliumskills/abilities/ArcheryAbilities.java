package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.AbilityData;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.mana.ChargedShot;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.item.LoreUtil;
import com.archyx.aureliumskills.util.math.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Locale;
import java.util.Random;

public class ArcheryAbilities extends AbilityProvider implements Listener {

    private final Random r = new Random();

    public ArcheryAbilities(AureliumSkills plugin) {
        super(plugin, Skill.ARCHERY);
        tickChargedShotCooldown();
    }

    public void bowMaster(EntityDamageByEntityEvent event, Player player, PlayerData playerData) {
        if (OptionL.isEnabled(Skill.ARCHERY)) {
            if (plugin.getAbilityManager().isEnabled(Ability.BOW_MASTER)) {
                if (!player.hasPermission("aureliumskills.archery")) {
                    return;
                }
                if (playerData.getAbilityLevel(Ability.BOW_MASTER) > 0) {
                    double multiplier = 1 + (getValue(Ability.BOW_MASTER, playerData) / 100);
                    event.setDamage(event.getDamage() * multiplier);
                }
            }
        }
    }

    public void stun(PlayerData playerData, LivingEntity entity) {
        if (r.nextDouble() < (getValue(Ability.STUN, playerData) / 100)) {
            if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
                AttributeInstance speed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                if (speed != null) {
                    //Applies stun
                    double reducedSpeed = speed.getValue() * 0.2;
                    AttributeModifier modifier = new AttributeModifier("AureliumSkills-Stun", -1 * reducedSpeed, AttributeModifier.Operation.ADD_NUMBER);
                    speed.addModifier(modifier);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            AttributeInstance newSpeed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                            if (newSpeed != null) {
                                for (AttributeModifier attributeModifier : newSpeed.getModifiers()) {
                                    if (attributeModifier.getName().equals("AureliumSkills-Stun")) {
                                        newSpeed.removeModifier(attributeModifier);
                                    }
                                }
                            }
                        }
                    }.runTaskLater(plugin, 40L);
                }
            }
        }
    }

    @EventHandler
    public void removeStun(PlayerQuitEvent event) {
        //Removes stun on logout
        AttributeInstance speed = event.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed != null) {
            for (AttributeModifier attributeModifier : speed.getModifiers()) {
                if (attributeModifier.getName().equals("AureliumSkills-Stun")) {
                    speed.removeModifier(attributeModifier);
                }
            }
        }
    }

    public void piercing(EntityDamageByEntityEvent event, PlayerData playerData, Player player, Arrow arrow) {
        if (r.nextDouble() < (getValue(Ability.PIERCING, playerData) / 100)) {
            arrow.setBounce(false);
            Vector velocity = arrow.getVelocity();
            Arrow newArrow = event.getEntity().getWorld().spawnArrow(arrow.getLocation(), velocity, (float) velocity.length(), 0.0f);
            newArrow.setShooter(player);
            newArrow.setKnockbackStrength(arrow.getKnockbackStrength());
            newArrow.setFireTicks(arrow.getFireTicks());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void archeryListener(EntityDamageByEntityEvent event) {
        if (OptionL.isEnabled(Skill.ARCHERY)) {
            if (!event.isCancelled()) {
                if (event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    if (arrow.getShooter() instanceof Player) {
                        Player player = (Player) arrow.getShooter();
                        if (blockAbility(player)) return;
                        // Applies abilities
                        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                        if (playerData == null) return;
                        AbilityManager options = plugin.getAbilityManager();
                        if (options.isEnabled(Ability.STUN)) {
                            if (event.getEntity() instanceof LivingEntity) {
                                LivingEntity entity = (LivingEntity) event.getEntity();
                                stun(playerData, entity);
                            }
                        }
                        if (options.isEnabled(Ability.PIERCING)) {
                            piercing(event, playerData, player, arrow);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void chargedShotToggle(PlayerInteractEvent event) {
        if (blockDisabled(MAbility.CHARGED_SHOT)) return;
        Player player = event.getPlayer();
        if (blockAbility(player)) return;
        ItemStack item = event.getItem();
        if (item != null) {
            if (item.getType() == Material.BOW) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                    PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                    if (playerData == null) return;
                    if (playerData.getManaAbilityLevel(MAbility.CHARGED_SHOT) == 0) return;
                    Locale locale = playerData.getLocale();
                    AbilityData abilityData = playerData.getAbilityData(MAbility.CHARGED_SHOT);
                    if (abilityData.getInt("cooldown") == 0) {
                        if (!abilityData.getBoolean("enabled")) { // Toggle on
                            abilityData.setData("enabled", true);
                            plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.CHARGED_SHOT_ENABLE, locale));
                        } else { // Toggle off
                            abilityData.setData("enabled", false);
                            plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.CHARGED_SHOT_DISABLE, locale));
                        }
                        abilityData.setData("cooldown", 8);
                    }
                }
            }
        }
    }

    private void tickChargedShotCooldown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                    if (playerData != null) {
                        if (playerData.containsAbilityData(MAbility.CHARGED_SHOT)) {
                            AbilityData abilityData = playerData.getAbilityData(MAbility.CHARGED_SHOT);
                            int cooldown = abilityData.getInt("cooldown");
                            if (cooldown != 0) {
                                abilityData.setData("cooldown", cooldown - 1);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 3L, 5L);
    }

    @EventHandler
    public void chargedShotActivate(EntityShootBowEvent event) {
        if (blockDisabled(MAbility.CHARGED_SHOT)) return;
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (blockAbility(player)) return;
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
            if (playerData == null) return;
            if (playerData.getAbilityData(MAbility.CHARGED_SHOT).getBoolean("enabled")) {
                if (playerData.getManaAbilityLevel(MAbility.CHARGED_SHOT) == 0) return;
                ManaAbilityManager manager = plugin.getManaAbilityManager();
                int cooldown = manager.getPlayerCooldown(player.getUniqueId(), MAbility.SHARP_HOOK);
                if (cooldown == 0) {
                    manager.activateAbility(player, MAbility.CHARGED_SHOT, (int) (manager.getCooldown(MAbility.CHARGED_SHOT, playerData) * 20)
                            , new ChargedShot(plugin, event.getProjectile(), event.getForce()));
                } else {
                    if (manager.getErrorTimer(player.getUniqueId(), MAbility.SHARP_HOOK) == 0) {
                        Locale locale = playerData.getLocale();
                        plugin.getAbilityManager().sendMessage(player, LoreUtil.replace(Lang.getMessage(ManaAbilityMessage.NOT_READY, locale), "{cooldown}", NumberUtil.format1((double) (cooldown) / 20)));
                        manager.setErrorTimer(player.getUniqueId(), MAbility.SHARP_HOOK, 2);
                    }
                }
            }
        }
    }

    public void applyChargedShot(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata("ChargedShotMultiplier")) {
            double multiplier = event.getDamager().getMetadata("ChargedShotMultiplier").get(0).asDouble();
            event.setDamage(event.getDamage() * multiplier);
        }
    }

}
