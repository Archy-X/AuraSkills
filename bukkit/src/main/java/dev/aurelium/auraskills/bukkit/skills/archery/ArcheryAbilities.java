package dev.aurelium.auraskills.bukkit.skills.archery;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.damage.DamageMeta;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import dev.aurelium.auraskills.api.damage.DamageType;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.util.AttributeCompat;
import dev.aurelium.auraskills.bukkit.util.CompatUtil;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.*;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ArcheryAbilities extends AbilityImpl {

    private final UUID LEGACY_STUN_ID = UUID.fromString("886ccad1-20f0-48e4-8634-53f3a76cf2ea");
    private final String LEGACY_STUN_NAME = "AureliumSkills-Stun";
    private final String STUN_KEY = "stun_ability";

    public ArcheryAbilities(AuraSkills plugin) {
        super(plugin, Abilities.RETRIEVAL, Abilities.ARCHER, Abilities.BOW_MASTER, Abilities.PIERCING, Abilities.STUN);
    }

    private DamageModifier bowMaster(Player player, User user) {
        var ability = Abilities.BOW_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
    }

    @EventHandler(ignoreCancelled = true)
    public void damageListener(DamageEvent event) {
        DamageMeta meta = event.getDamageMeta();
        Player attacker = meta.getAttackerAsPlayer();

        if (attacker != null) {
            User user = plugin.getUser(attacker);
            if (meta.getDamageType() == DamageType.BOW) {
                meta.addAttackModifier(bowMaster(attacker, user));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void archeryListener(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) return;

        if (!(arrow.getShooter() instanceof Player player)) return;
        if (player.hasMetadata("NPC")) return;

        // Applies abilities
        User user = plugin.getUser(player);

        if (event.getEntity() instanceof LivingEntity entity) {
            stun(player, user, entity);
        }

        piercing(player, event, user, arrow);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void pierceListener(EntityShootBowEvent event) {
        if (!(event.getProjectile() instanceof Arrow arrow)) return;

        if (!(arrow.getShooter() instanceof Player player)) return;

        User user = plugin.getUser(player);

        pierceInit(user, player, arrow);
    }

    public void stun(Player player, User user, LivingEntity entity) {
        var ability = Abilities.STUN;
        double STUN_SPEED_REDUCTION = 0.2;

        if (failsChecks(player, ability)) return;

        if (rand.nextDouble() < (getValue(ability, user) / 100)) {
            AttributeInstance speed = entity.getAttribute(AttributeCompat.MOVEMENT_SPEED);
            if (speed == null) return;
            // Check if there already is a stun modifier
            for (AttributeModifier existingModifier : speed.getModifiers()) {
                if (isStunModifier(existingModifier)) {
                    return;
                }
            }
            // Applies stun
            AttributeModifier modifier = getAttributeModifier(speed, STUN_SPEED_REDUCTION);
            speed.addModifier(modifier);

            scheduleStunRemoval(entity);
        }
    }

    @SuppressWarnings("removal")
    private @NotNull AttributeModifier getAttributeModifier(AttributeInstance speed, double STUN_SPEED_REDUCTION) {
        double reducedSpeed = speed.getValue() * STUN_SPEED_REDUCTION;
        double attributeValue = -1 * reducedSpeed;

        AttributeModifier modifier;
        if (VersionUtils.isAtLeastVersion(21)) {
            NamespacedKey key = new NamespacedKey(plugin, STUN_KEY);
            modifier = new AttributeModifier(key, attributeValue, Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
        } else {
            modifier = new AttributeModifier(LEGACY_STUN_ID, LEGACY_STUN_NAME, attributeValue, Operation.ADD_NUMBER);
        }
        return modifier;
    }

    private void scheduleStunRemoval(LivingEntity entity) {
        plugin.getScheduler().scheduleSync(() -> {
            AttributeInstance newSpeed = entity.getAttribute(AttributeCompat.MOVEMENT_SPEED);
            if (newSpeed == null) return;
            for (AttributeModifier attributeModifier : newSpeed.getModifiers()) {
                if (isStunModifier(attributeModifier)) {
                    newSpeed.removeModifier(attributeModifier);
                }
            }
        }, 40L * 50L, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void removeStun(PlayerQuitEvent event) {
        // Removes stun on logout
        AttributeInstance speed = event.getPlayer().getAttribute(AttributeCompat.MOVEMENT_SPEED);
        if (speed == null) return;

        for (AttributeModifier attributeModifier : speed.getModifiers()) {
            if (isStunModifier(attributeModifier)) {
                speed.removeModifier(attributeModifier);
            }
        }
    }

    private boolean isStunModifier(AttributeModifier modifier) {
        if (modifier.getName().equals(LEGACY_STUN_NAME)) {
            return true;
        }
        if (VersionUtils.isAtLeastVersion(21)) {
            final String pluginNamespace = "auraskills";
            String namespace = modifier.getKey().getNamespace();
            String key = modifier.getKey().getKey();

            return namespace.equals(pluginNamespace) && key.equals(STUN_KEY);
        }
        return false;
    }

    public void piercing(Player player, EntityDamageByEntityEvent event, User user, Arrow arrow) {
        var ability = Abilities.PIERCING;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;
        // Disable if enemy is blocking with a shield
        Entity damaged = event.getEntity();
        if (damaged instanceof Player damagedPlayer) {
            if (damagedPlayer.isBlocking()) {
                return;
            }
        }
        if (rand.nextDouble() < (getValue(ability, user) / 100)) {
            if (arrow.getPierceLevel() < 127) {
                arrow.setPierceLevel(arrow.getPierceLevel() + 1);
            }
        }
    }

    public void pierceInit(User user, Player player, Arrow arrow) {
        var ability = Abilities.PIERCING;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (rand.nextDouble() < (getValue(ability, user) / 100)) {
            // Adds 1 pierce to the initial shot otherwise it doesn't pierce on non-lethal damage.
            if (arrow.getPierceLevel() < 127) {
                arrow.setPierceLevel(arrow.getPierceLevel() + 1);
            }
        }
    }

    @Override
    public String replaceDescPlaceholders(String input, Ability ability, User user) {
        if (ability.equals(Abilities.RETRIEVAL)) {
            return TextUtil.replace(input, "{time}", NumberUtil.format1(ability.optionDouble("delay_sec", 3)));
        }
        return input;
    }

    @EventHandler
    public void retrieval(ProjectileHitEvent event) {
        // Ignore if an entity was hit
        if (event.getHitBlock() == null || event.getHitEntity() != null) return;
        if (!(event.getEntity() instanceof AbstractArrow arrow)) return;
        if (event.getEntity() instanceof Trident) return;
        if (arrow.getPickupStatus() != PickupStatus.ALLOWED) return;
        if (!(arrow.getShooter() instanceof Player player)) return;

        var ability = Abilities.RETRIEVAL;
        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        ItemStack item = getArrowItem(arrow);
        plugin.getScheduler().scheduleSync(() -> {
            if (!arrow.isValid()) return; // Ignore if the arrow has de-spawned or was picked up
            if (!arrow.getWorld().equals(player.getWorld())) return;

            double value = getValue(ability, plugin.getUser(player));
            // Check if arrow is close enough
            if (arrow.getLocation().distanceSquared(player.getLocation()) > value * value) {
                return;
            }

            item.setAmount(1);
            // Abort if the player doesn't have enough inventory space
            if (!player.getInventory().addItem(item).isEmpty()) {
                return;
            }

            arrow.getWorld().spawnParticle(CompatUtil.witchParticle(), arrow.getLocation(), 5, 0, 0, 0);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.4f, 1.9f);

            arrow.remove();
        }, Math.round(ability.optionDouble("delay_sec", 3) * 1000), TimeUnit.MILLISECONDS);
    }

    private ItemStack getArrowItem(AbstractArrow abstractArrow) {
        if (abstractArrow instanceof Arrow arrow) {
            if (isNormalArrow(arrow)) {
                return new ItemStack(Material.ARROW);
            } else {
                ItemStack item = new ItemStack(Material.TIPPED_ARROW);
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                if (meta != null) {
                    if (VersionUtils.isAtLeastVersion(20, 2)) {
                        meta.setBasePotionType(arrow.getBasePotionType());
                    } else {
                        setLegacyTippedArrow(arrow, meta);
                    }
                    item.setItemMeta(meta);
                }
                return item;
            }
        } else if (abstractArrow instanceof SpectralArrow) {
            return new ItemStack(Material.SPECTRAL_ARROW);
        }
        return new ItemStack(Material.ARROW);
    }

    private boolean isNormalArrow(Arrow arrow) {
        if (VersionUtils.isAtLeastVersion(20, 2)) {
            return arrow.getBasePotionType() == null || arrow.getBasePotionType().toString().equals("UNCRAFTABLE");
        } else {
            try {
                Method getBasePotionData = arrow.getClass().getDeclaredMethod("getBasePotionData");
                Object potionData = getBasePotionData.invoke(arrow);

                Method getType = potionData.getClass().getDeclaredMethod("getType");
                Object potionType = getType.invoke(potionData);

                return potionType.toString().equals("UNCRAFTABLE");
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Below 1.20.2
    private void setLegacyTippedArrow(Arrow arrow, PotionMeta meta) {
        try {
            Method getBasePotionData = arrow.getClass().getDeclaredMethod("getBasePotionData");
            Object potionData = getBasePotionData.invoke(arrow);
            // Set PotionData to PotionMeta
            Class<?> potionDataClass = Class.forName("org.bukkit.potion.PotionData");
            Method setBasePotionData = PotionMeta.class.getDeclaredMethod("setBasePotionData", potionDataClass);

            setBasePotionData.invoke(meta, potionData);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
