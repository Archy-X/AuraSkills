package dev.aurelium.auraskills.bukkit.listeners;

import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.trait.AttackDamageTrait;
import dev.aurelium.auraskills.bukkit.trait.DefenseTrait;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.mechanics.DamageType;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {

    private final AuraSkills plugin;

    public DamageListener(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        // Check if not cancelled
        if (event.isCancelled()) {
            return;
        }

        // Gets the player who dealt damage
        Player player = getDamager(event.getDamager());
        if (player == null) {
            if (event.getEntity() instanceof Player) {
                onDamaged(event, (Player) event.getEntity());
            }
            return;
        }

        // Check disabled world
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return;
        }
        // Gets player skill
        User user = plugin.getUser(player);

        DamageType damageType = getDamageType(event, player);

        // Applies attack damage trait
        if (plugin.getTraitManager().getTraitImpl(Traits.ATTACK_DAMAGE) instanceof AttackDamageTrait attackDamage) {
            attackDamage.strength(event, user, damageType);
        }
    }

    private void onDamaged(EntityDamageByEntityEvent event, Player player) {
        // Check disabled world
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return;
        }
        if (event.isCancelled()) return;
        User user = plugin.getUser(player);

        // Handles incoming damage reduction trait
        if (plugin.getTraitManager().getTraitImpl(Traits.DEFENSE) instanceof DefenseTrait defense) {
            defense.onDamage(event, user);
        }
    }

    private DamageType getDamageType(EntityDamageByEntityEvent event, Player player) {
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

    private Player getDamager(Entity entity) {
        Player player = null;
        if (entity instanceof Player) {
            player = (Player) entity;
        }
        else if (entity instanceof Projectile projectile) {
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
