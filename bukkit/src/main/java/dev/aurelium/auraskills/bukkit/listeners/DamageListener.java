package dev.aurelium.auraskills.bukkit.listeners;

import dev.aurelium.auraskills.api.damage.DamageType;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.damage.DamageHandler;
import dev.aurelium.auraskills.bukkit.damage.DamageResult;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

public class DamageListener implements Listener {

    private final AuraSkills plugin;
    private final DamageHandler damageHandler;

    public DamageListener(AuraSkills plugin) {
        this.plugin = plugin;
        this.damageHandler = new DamageHandler();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        // Check if not cancelled
        if (event.isCancelled()) {
            return;
        }

        // Gets the player who dealt damage
        Player player = getDamager(event.getDamager());
        if (player != null) {
            if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
                return;
            }
            if (player.hasMetadata("NPC")) return;
            if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) return;
        }

        // Handles being damaged
        if (event.getEntity() instanceof Player target) {
            if (plugin.getWorldManager().isInDisabledWorld(target.getLocation())) {
                return;
            }
            if (target.hasMetadata("NPC")) return;
        }

        if (player == null && !(event.getEntity() instanceof Player)) {
            // We have nothing to do here
            return;
        }

        DamageResult result = damageHandler.handleDamage(
                event.getDamager(), event.getEntity(), getDamageType(event, player),
                event.getCause(), event.getDamage(), "vanilla");

        if (result.cancel()) {
            event.setCancelled(true);
        } else {
            event.setDamage(result.damage());
            // Correct last damage to fix repeated attacks that bypass the invulnerable frame
            if (event.getEntity() instanceof Player damaged && plugin.configBoolean(Option.DAMAGE_CORRECT_LAST_DAMAGE)) {
                plugin.getScheduler().executeSync(() -> {
                    User user = plugin.getUser(damaged);
                    double lastDamage = Math.max(event.getFinalDamage(), user.getCurrentOriginalDamage());
                    damaged.setLastDamage(lastDamage);
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageLow(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && plugin.configBoolean(Option.DAMAGE_CORRECT_LAST_DAMAGE)) {
            User user = plugin.getUser(player);
            user.setCurrentOriginalDamage(event.getDamage());
        }
    }

    @SuppressWarnings("deprecation")
    private DamageType getDamageType(EntityDamageByEntityEvent event, Player player) {
        if (player == null) return DamageType.OTHER;
        if (event.getDamager() instanceof Arrow || event.getDamager() instanceof SpectralArrow || event.getDamager() instanceof TippedArrow) {
            return DamageType.BOW;
        }
        Material material = player.getInventory().getItemInMainHand().getType();
        if (material.name().contains("SWORD")) {
            return DamageType.SWORD;
        } else if (material.name().contains("_AXE")) {
            return DamageType.AXE;
        } else if (material.name().contains("PICKAXE")) {
            return DamageType.PICKAXE;
        } else if (material.name().contains("SHOVEL") || material.name().contains("SPADE")) {
            return DamageType.SHOVEL;
        } else if (material.name().contains("HOE")) {
            return DamageType.HOE;
        } else if (material.equals(Material.AIR)) {
            return DamageType.HAND;
        } else if (event.getDamager() instanceof Trident) {
            return DamageType.BOW;
        }
        return DamageType.OTHER;
    }

    @Nullable
    private Player getDamager(Entity entity) {
        Player player = null;
        if (entity instanceof Player) {
            player = (Player) entity;
        } else if (entity instanceof Projectile projectile) {
            EntityType type = projectile.getType();
            if (type == EntityType.ARROW || type == EntityType.SPECTRAL_ARROW || type.toString().equals("TRIDENT") ||
                    type.toString().equals("TIPPED_ARROW")) {
                if (projectile.getShooter() instanceof Player) {
                    player = (Player) projectile.getShooter();
                }
            }
        }
        return player;
    }
}
