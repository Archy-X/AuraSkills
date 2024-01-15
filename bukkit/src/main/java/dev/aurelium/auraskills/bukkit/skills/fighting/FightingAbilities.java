package dev.aurelium.auraskills.bukkit.skills.fighting;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.common.ability.AbilityData;
import dev.aurelium.auraskills.common.message.type.AbilityMessage;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FightingAbilities extends AbilityImpl {

    public FightingAbilities(AuraSkills plugin) {
        super(plugin, Abilities.CRIT_DAMAGE, Abilities.FIGHTER, Abilities.SWORD_MASTER, Abilities.FIRST_STRIKE, Abilities.BLEED);
    }

    public void reloadCritDamage(Player player, User user) {
        Ability ability = Abilities.CRIT_DAMAGE;
        String modifierName = "fighting_ability";
        user.removeTraitModifier(modifierName, false);

        if (isDisabled(ability)) return;
        if (failsChecks(player, ability)) return;

        double value = getValue(ability, user);
        user.addTraitModifier(new TraitModifier(modifierName, Traits.CRIT_DAMAGE, value), false);
    }

    @EventHandler
    public void onLevelUp(SkillLevelUpEvent event) {
        if (!event.getSkill().equals(Skills.FIGHTING)) {
            return;
        }
        Player player = event.getPlayer();
        User user = plugin.getUser(player);
        reloadCritDamage(player, user);
    }

    public DamageModifier swordMaster(Player player, User user) {
        var ability = Abilities.SWORD_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
    }

    public DamageModifier firstStrike(User user, Player player) {
        var ability = Abilities.FIRST_STRIKE;
        
        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        // Player is on cooldown
        if (player.hasMetadata("AureliumSkills-FirstStrike")) return DamageModifier.none();
        
        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();
        
        Locale locale = user.getLocale();

        if (ability.optionBoolean("enable_message", true)) {
            plugin.getAbilityManager().sendMessage(player, plugin.getMsg(AbilityMessage.FIRST_STRIKE_DEALT, locale));
        }
        // Adds metadata
        player.setMetadata("AureliumSkills-FirstStrike", new FixedMetadataValue(plugin, true));
        // Increments counter
        AbilityData abilityData = user.getAbilityData(ability);
        if (abilityData.containsKey("counter")) {
            abilityData.setData("counter", abilityData.getInt("counter") + 1);
        } else {
            abilityData.setData("counter", 0);
        }
        int id = abilityData.getInt("counter");
        // Schedules metadata removal
        long cooldown = ability.optionInt("cooldown_ticks", 6000);
        plugin.getScheduler().scheduleSync(() -> {
            if (user.getAbilityData(ability).containsKey("counter")) {
                if (user.getAbilityData(ability).getInt("counter") == id) {
                    player.removeMetadata("AureliumSkills-FirstStrike", plugin);
                }
            }
        }, cooldown * 50, TimeUnit.MILLISECONDS);

        double modifier = getValue(ability, user) / 100;
        return new DamageModifier(modifier, DamageModifier.Operation.ADD_COMBINED);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void bleedListener(EntityDamageByEntityEvent event) {
        var ability = Abilities.BLEED;

        if (isDisabled(ability)) return;

        if (!(event.getDamager() instanceof Player player)) return;

        if (failsChecks(player, ability)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) return;
        // If player used sword
        if (player.getInventory().getItemInMainHand().getType().name().toUpperCase(Locale.ROOT).contains("SWORD")) {
            User user = plugin.getUser(player);
  
            if (event.getEntity() instanceof LivingEntity) {
                checkBleed(event, user, (LivingEntity) event.getEntity(), ability);
            }
        }
    }

    public void checkBleed(EntityDamageByEntityEvent event, User user, LivingEntity entity, Ability ability) {
        if (rand.nextDouble() < (getValue(ability, user) / 100)) {
            // Return if damage is fatal
            if (event.getFinalDamage() >= entity.getHealth()) return;

            PersistentDataContainer container = entity.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(plugin, "bleed_ticks");

            if (!container.has(key, PersistentDataType.INTEGER)) {
                int baseTicks = ability.optionInt("base_ticks", 3);
                container.set(key, PersistentDataType.INTEGER, baseTicks);
                // Send messages
                if (ability.optionBoolean("enable_enemy_message", true)) {
                    Locale locale = user.getLocale();
                    if (event.getDamager() instanceof Player player) {
                        plugin.getAbilityManager().sendMessage(player, plugin.getMsg(AbilityMessage.BLEED_ENEMY_BLEEDING, locale));
                    }
                }
                if (entity instanceof Player) {
                    if (ability.optionBoolean("enable_self_message", true)) {
                        Player player = (Player) entity;
                        Locale locale = user.getLocale();
                        plugin.getAbilityManager().sendMessage(player, plugin.getMsg(AbilityMessage.BLEED_SELF_BLEEDING, locale));
                    }
                }
                // Schedule applying bleed tick damage
                scheduleBleedTicks(entity, user, ability);
            } else {
                int currentTicks = container.getOrDefault(key, PersistentDataType.INTEGER, 0);
                int addedTicks = ability.optionInt("added_ticks", 2);
                int maxTicks = ability.optionInt("max_ticks", 11);
                int resultingTicks = currentTicks + addedTicks;
                if (resultingTicks <= maxTicks) { // Check that resulting bleed ticks does not exceed maximum
                    container.set(key, PersistentDataType.INTEGER, resultingTicks);
                }
            }
        }
    }

    private void scheduleBleedTicks(LivingEntity entity, User user, Ability ability) {
        // Schedules bleed ticks
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!entity.isValid()) { // Stop if entity died/transformed
                    cancel();
                    return;
                }
                PersistentDataContainer container = entity.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(plugin, "bleed_ticks");

                if (container.has(key, PersistentDataType.INTEGER)) {
                    int bleedTicks = container.getOrDefault(key, PersistentDataType.INTEGER, 0);
                    if (bleedTicks > 0) {
                        // Apply bleed
                        double damage = ability.getSecondaryValue(user.getAbilityLevel(ability));
                        double healthBefore = entity.getHealth();
                        entity.damage(damage);
                        double healthAfter = entity.getHealth();
                        if (healthAfter != healthBefore) { // Only display particles if damage was actually done
                            displayBleedParticles(entity, ability);
                        }
                        // Decrement bleed ticks
                        if (bleedTicks != 1) {
                            container.set(key, PersistentDataType.INTEGER, bleedTicks - 1);
                        } else {
                            container.remove(key);
                        }
                        return;
                    } else {
                        container.remove(key);
                    }
                }
                if (entity instanceof Player) {
                    if (ability.optionBoolean("enable_stop_message", true)) {
                        Player player = (Player) entity;
                        Locale locale = user.getLocale();
                        plugin.getAbilityManager().sendMessage(player, plugin.getMsg(AbilityMessage.BLEED_STOP, locale));
                    }
                }
                cancel();
            }
        }.runTaskTimer(plugin, 40L, ability.optionInt("tick_period", 40));
    }

    private void displayBleedParticles(LivingEntity entity, Ability ability) {
        // Check if disabled
        if (!ability.optionBoolean("show_particles", true)) {
            return;
        }
        Location location = entity.getLocation().add(0, entity.getHeight() * 0.6, 0);
        BlockData particleData = Material.REDSTONE_BLOCK.createBlockData();

        entity.getWorld().spawnParticle(Particle.BLOCK_DUST, location, 30, particleData);
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent event) {
        PersistentDataContainer container = event.getPlayer().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "bleed_ticks");
        container.remove(key);
    }

}
