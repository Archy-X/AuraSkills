package dev.aurelium.auraskills.bukkit.skills.archery;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class ArcheryAbilities extends AbilityImpl {

    private final String STUN_MODIFIER_NAME = "AureliumSkills-Stun";

    public ArcheryAbilities(AuraSkills plugin) {
        super(plugin, Abilities.RETRIEVAL, Abilities.ARCHER, Abilities.BOW_MASTER, Abilities.PIERCING, Abilities.STUN);
    }

    public DamageModifier bowMaster(Player player, User user) {
        var ability = Abilities.BOW_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
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
            AttributeInstance speed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (speed == null) return;
            // Applies stun
            double reducedSpeed = speed.getValue() * STUN_SPEED_REDUCTION;
            AttributeModifier modifier = new AttributeModifier(STUN_MODIFIER_NAME, -1 * reducedSpeed, AttributeModifier.Operation.ADD_NUMBER);
            speed.addModifier(modifier);
            new BukkitRunnable() {
                @Override
                public void run() {
                    AttributeInstance newSpeed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                    if (newSpeed == null) return;
                    for (AttributeModifier attributeModifier : newSpeed.getModifiers()) {
                        if (attributeModifier.getName().equals(STUN_MODIFIER_NAME)) {
                            newSpeed.removeModifier(attributeModifier);
                        }
                    }
                }
            }.runTaskLater(plugin, 40L);
        }
    }

    @EventHandler
    public void removeStun(PlayerQuitEvent event) {
        // Removes stun on logout
        AttributeInstance speed = event.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed == null) return;

        for (AttributeModifier attributeModifier : speed.getModifiers()) {
            if (attributeModifier.getName().equals(STUN_MODIFIER_NAME)) {
                speed.removeModifier(attributeModifier);
            }
        }
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
            return TextUtil.replace(input, "{time}", NumberUtil.format1(ability.optionDouble("delay_sec")));
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

            arrow.getWorld().spawnParticle(Particle.SPELL_WITCH, arrow.getLocation(), 5, 0, 0, 0);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.4f, 1.9f);

            arrow.remove();
        }, Math.round(ability.optionDouble("delay_sec") * 1000), TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("deprecation")
    private ItemStack getArrowItem(AbstractArrow abstractArrow) {
        if (abstractArrow instanceof Arrow arrow) {
            if (arrow.getBasePotionData().getType() == PotionType.UNCRAFTABLE) {
                return new ItemStack(Material.ARROW);
            } else {
                ItemStack item = new ItemStack(Material.TIPPED_ARROW);
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                if (meta != null) {
                    if (VersionUtils.isAtLeastVersion(20, 4)) {
                        meta.setBasePotionType(arrow.getBasePotionType());
                    } else {
                        meta.setBasePotionData(arrow.getBasePotionData());
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

}
