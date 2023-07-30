package dev.aurelium.auraskills.bukkit.listeners;

import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skills.excavation.ExcavationAbilities;
import dev.aurelium.auraskills.bukkit.skills.farming.FarmingAbilities;
import dev.aurelium.auraskills.bukkit.skills.fighting.FightingAbilities;
import dev.aurelium.auraskills.bukkit.skills.foraging.ForagingAbilities;
import dev.aurelium.auraskills.bukkit.skills.mining.MiningAbilities;
import dev.aurelium.auraskills.bukkit.trait.AttackDamageTrait;
import dev.aurelium.auraskills.bukkit.trait.DamageReductionTrait;
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

        // Apply master abilities
        var abManager = plugin.getAbilityManager();
        switch (damageType) {
            case SWORD ->
                abManager.getAbilityImpl(FightingAbilities.class).swordMaster(event, player, user);
            case HOE ->
                abManager.getAbilityImpl(FarmingAbilities.class).scytheMaster(event, player, user);
            case AXE ->
                abManager.getAbilityImpl(ForagingAbilities.class).axeMaster(event, player, user);
            case PICKAXE ->
                abManager.getAbilityImpl(MiningAbilities.class).pickMaster(event, player, user);
            case SHOVEL ->
                abManager.getAbilityImpl(ExcavationAbilities.class).spadeMaster(event, player, user);
        }

        // Apply First Strike
        if (damageType == DamageType.SWORD) {
            abManager.getAbilityImpl(FightingAbilities.class).firstStrike(event, user, player);
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
        if (plugin.getTraitManager().getTraitImpl(Traits.DAMAGE_REDUCTION) instanceof DamageReductionTrait defense) {
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
