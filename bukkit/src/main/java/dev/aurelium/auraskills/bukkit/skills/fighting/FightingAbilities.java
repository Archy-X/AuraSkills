package dev.aurelium.auraskills.bukkit.skills.fighting;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.damage.DamageType;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.util.CompatUtil;
import dev.aurelium.auraskills.common.ability.AbilityData;
import dev.aurelium.auraskills.common.message.type.AbilityMessage;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FightingAbilities extends AbilityImpl {

    public static final String BLEED_DAMAGER_KEY = "bleed_damager";
    private final String PARRY_KEY = "parry_ready";
    private final String PARRY_VECTOR = "parry_vector";

    public FightingAbilities(AuraSkills plugin) {
        super(plugin, Abilities.PARRY, Abilities.FIGHTER, Abilities.SWORD_MASTER, Abilities.FIRST_STRIKE, Abilities.BLEED);
    }

    @Override
    public String replaceDescPlaceholders(String input, Ability ability, User user) {
        if (ability.equals(Abilities.BLEED)) {
            return TextUtil.replace(input,
                    "{base_ticks}", String.valueOf(ability.optionInt("base_ticks", 3)),
                    "{added_ticks}", String.valueOf(ability.optionInt("added_ticks", 2)));
        } else if (ability.equals(Abilities.PARRY)) {
            String secDisplay = NumberUtil.format2((double) ability.optionInt("time_ms", 250) / 1000);
            return TextUtil.replace(input,
                    "{time}", secDisplay);
        }
        return input;
    }

    private DamageModifier swordMaster(Player player, User user) {
        var ability = Abilities.SWORD_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
    }

    private DamageModifier firstStrike(User user, Player player) {
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

    @EventHandler(ignoreCancelled = true)
    public void damageListener(DamageEvent event) {
        var meta = event.getDamageMeta();
        var attacker = meta.getAttackerAsPlayer();
        var target = meta.getTargetAsPlayer();

        if (attacker != null) {
            if (meta.getDamageType() == DamageType.SWORD) {
                var user = plugin.getUser(attacker);
                meta.addAttackModifier(swordMaster(attacker, user));
                meta.addAttackModifier(firstStrike(user, attacker));
            }
            if (target != null) {
                var user = plugin.getUser(target);
                meta.addDefenseModifier(handleParry(event, target, user));
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
        var task = new TaskRunnable() {
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
                        // Mark entity with damager UUID for leveler
                        NamespacedKey damagerKey = new NamespacedKey(plugin, BLEED_DAMAGER_KEY);
                        container.set(damagerKey, PersistentDataType.STRING, user.getUuid().toString());

                        entity.damage(damage);
                        // Disable invulnerable frames
                        entity.setNoDamageTicks(0);
                        // Remove damager data
                        container.remove(damagerKey);
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
        };
        plugin.getScheduler().timerSync(task, 40 * 50L, ability.optionInt("tick_period", 40) * 50L, TimeUnit.MILLISECONDS);
    }

    private void displayBleedParticles(LivingEntity entity, Ability ability) {
        // Check if disabled
        if (!ability.optionBoolean("show_particles", true)) {
            return;
        }
        Location location = entity.getLocation().add(0, entity.getHeight() * 0.6, 0);
        BlockData particleData = Material.REDSTONE_BLOCK.createBlockData();

        entity.getWorld().spawnParticle(CompatUtil.dustParticle(), location, 30, particleData);
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent event) {
        PersistentDataContainer container = event.getPlayer().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "bleed_ticks");
        container.remove(key);
        container.remove(new NamespacedKey(plugin, BLEED_DAMAGER_KEY));
    }

    @EventHandler
    public void parryReady(PlayerInteractEvent event) {
        var ability = Abilities.PARRY;
        if (isDisabled(ability)) return;

        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (!player.getInventory().getItemInMainHand().getType().toString().contains("SWORD")) {
            return;
        }

        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);

        // Return if already ready
        if (user.metadataBoolean(PARRY_KEY)) {
            return;
        }
        Vector facing = player.getLocation().getDirection();
        user.getMetadata().put(PARRY_KEY, true);
        user.getMetadata().put(PARRY_VECTOR, facing);

        scheduleUnready(user);
    }

    public DamageModifier handleParry(DamageEvent event, Player player, User user) {
        var ability = Abilities.PARRY;
        if (failsChecks(player, ability)) return DamageModifier.none();
        // Return if not parry ready
        if (!user.metadataBoolean(PARRY_KEY)) return DamageModifier.none();

        if (event.getDamageMeta().getAttacker() != null &&
                !isFacingCloseEnough(user, player, event.getDamageMeta().getAttacker())) {
            return DamageModifier.none();
        }

        double value = getValue(ability, user);

        plugin.getUiProvider().sendActionBar(user, plugin.getMsg(AbilityMessage.PARRY_PARRIED, user.getLocale()));
        plugin.getUiProvider().getActionBarManager().setPaused(user, 1500, TimeUnit.MILLISECONDS);
        if (ability.optionBoolean("enable_sound", true)) {
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1f, 1.4f);
        }

        Vector velBefore = player.getVelocity();
        // Disable knockback
        plugin.getScheduler().scheduleSync(() -> player.setVelocity(velBefore),
                50, TimeUnit.MILLISECONDS);

        return new DamageModifier((1 - value / 100) - 1, DamageModifier.Operation.MULTIPLY);
    }

    private boolean isFacingCloseEnough(User user, Player player, Entity damager) {
        Vector va = damager.getLocation().toVector();
        Vector vb = player.getLocation().toVector();
        Vector playerToDamager = va.subtract(vb).normalize();

        Object facingObj = user.getMetadata().get(PARRY_VECTOR);
        if (facingObj == null) return false;

        Vector facing = (Vector) facingObj;

        return playerToDamager.dot(facing) >= 0.7;
    }

    private void scheduleUnready(User user) {
        plugin.getScheduler().scheduleSync(() -> {
            user.getMetadata().remove(PARRY_KEY);
            user.getMetadata().remove(PARRY_VECTOR);
        }, Abilities.PARRY.optionInt("time_ms", 250), TimeUnit.MILLISECONDS);
    }

}
