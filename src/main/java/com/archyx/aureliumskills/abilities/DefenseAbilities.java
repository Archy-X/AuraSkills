package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.ManaAbilityMessage;
import com.archyx.aureliumskills.mana.Absorption;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.NumberUtil;
import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class DefenseAbilities extends AbilityProvider implements Listener {

    private final Random r = new Random();
    private final Set<Player> absorptionActivated;

    public DefenseAbilities(AureliumSkills plugin) {
        super(plugin, Skill.DEFENSE);
        this.absorptionActivated = new HashSet<>();
    }

    public void shielding(EntityDamageByEntityEvent event, PlayerData playerData, Player player) {
        if (plugin.getAbilityManager().isEnabled(Ability.SHIELDING)) {
            if (player.isSneaking()) {
                if (playerData.getAbilityLevel(Ability.SHIELDING) > 0) {
                    double damageReduction = 1 - (getValue(Ability.SHIELDING, playerData) / 100);
                    event.setDamage(event.getDamage() * damageReduction);
                }
            }
        }
    }

    public void mobMaster(EntityDamageByEntityEvent event, PlayerData playerData) {
        if (plugin.getAbilityManager().isEnabled(Ability.MOB_MASTER)) {
            if (event.getDamager() instanceof LivingEntity && !(event.getDamager() instanceof Player)) {
                if (playerData.getAbilityLevel(Ability.MOB_MASTER) > 0) {
                    double damageReduction = 1 - (getValue(Ability.MOB_MASTER, playerData) / 100);
                    event.setDamage(event.getDamage() * damageReduction);
                }
            }
        }
    }

    public void immunity(EntityDamageByEntityEvent event, PlayerData playerData) {
        double chance = getValue(Ability.IMMUNITY, playerData) / 100;
        if (r.nextDouble() < chance) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void noDebuff(PotionSplashEvent event) {
        if (blockDisabled(Ability.NO_DEBUFF)) return;
        for (LivingEntity entity : event.getAffectedEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (blockAbility(player)) return;
                for (PotionEffect effect : event.getPotion().getEffects()) {
                    PotionEffectType type = effect.getType();
                    if (type.equals(PotionEffectType.POISON) || type.equals(PotionEffectType.UNLUCK) || type.equals(PotionEffectType.WITHER) ||
                            type.equals(PotionEffectType.WEAKNESS) || type.equals(PotionEffectType.SLOW_DIGGING) || type.equals(PotionEffectType.SLOW) ||
                            type.equals(PotionEffectType.HUNGER) || type.equals(PotionEffectType.HARM) || type.equals(PotionEffectType.CONFUSION) ||
                            type.equals(PotionEffectType.BLINDNESS)) {
                        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                        if (playerData == null) return;
                        double chance = getValue(Ability.NO_DEBUFF, playerData) / 100;
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

    public void noDebuffFire(PlayerData playerData, Player player, LivingEntity entity) {
        if (entity.getEquipment() != null) {
            if (entity.getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT) > 0) {
                double chance = getValue(Ability.NO_DEBUFF, playerData) / 100;
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

    @EventHandler
    public void defenseListener(EntityDamageByEntityEvent event) {
        if (OptionL.isEnabled(Skill.DEFENSE)) {
            if (!event.isCancelled()) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();
                    if (blockAbility(player)) return;
                    PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
                    if (playerData == null) return;
                    if (plugin.getAbilityManager().isEnabled(Ability.NO_DEBUFF)) {
                        if (event.getDamager() instanceof LivingEntity) {
                            LivingEntity entity = (LivingEntity) event.getDamager();
                            noDebuffFire(playerData, player, entity);
                        }
                    }
                    if (plugin.getAbilityManager().isEnabled(Ability.IMMUNITY)) {
                        immunity(event, playerData);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        absorptionActivated.remove(event.getPlayer());
    }

    @EventHandler
    public void readyAbsorption(PlayerInteractEvent event) {
        plugin.getManaAbilityManager().getActivator().readyAbility(event, Skill.DEFENSE, new String[] {"SHIELD"}, Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK);
    }

    public void handleAbsorption(EntityDamageByEntityEvent event, Player player, PlayerData playerData) {
        ManaAbilityManager manager = plugin.getManaAbilityManager();
        if (absorptionActivated.contains(player)) {
            handleAbsorbedHit(event, player);
        } else if (manager.isReady(player.getUniqueId(), MAbility.ABSORPTION)) {
            // Activate ability if ready
            if (manager.isActivated(player.getUniqueId(), MAbility.ABSORPTION)) {
                return;
            }
            if (playerData.getMana() >= manager.getManaCost(MAbility.ABSORPTION, playerData)) {
                manager.activateAbility(player, MAbility.ABSORPTION, (int) (getValue(MAbility.ABSORPTION, playerData) * 20), new Absorption(plugin, this));
                handleAbsorbedHit(event, player);
            }
            else {
                Locale locale = Lang.getLanguage(player);
                plugin.getAbilityManager().sendMessage(player, Lang.getMessage(ManaAbilityMessage.NOT_ENOUGH_MANA, locale)
                        .replace("{mana}", NumberUtil.format0(manager.getManaCost(MAbility.ABSORPTION, playerData))));
            }
        }
    }

    private void handleAbsorbedHit(EntityDamageByEntityEvent event, Player player) {
        // Decrease mana and cancel event
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) return;
        double mana = playerData.getMana() - event.getDamage() * 2;
        if (mana > 0) {
            playerData.setMana(mana);
            event.setCancelled(true);
            // Particle effects and sound
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GUARDIAN_HURT, 1f, 1f);
            if (plugin.getManaAbilityManager().getOptionAsBooleanElseTrue(MAbility.ABSORPTION, "enable_particles")) {
                XParticle.circle(1, 1, 1, 20, 0, ParticleDisplay.colored(player.getLocation().add(0, 1, 0), Color.MAGENTA, 1));
            }
        }
    }

    public Set<Player> getAbsorptionActivated() {
        return absorptionActivated;
    }
}
