package dev.aurelium.auraskills.bukkit.listeners;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skills.archery.ArcheryAbilities;
import dev.aurelium.auraskills.bukkit.skills.archery.ChargedShot;
import dev.aurelium.auraskills.bukkit.skills.defense.Absorption;
import dev.aurelium.auraskills.bukkit.skills.defense.DefenseAbilities;
import dev.aurelium.auraskills.bukkit.skills.excavation.ExcavationAbilities;
import dev.aurelium.auraskills.bukkit.skills.farming.FarmingAbilities;
import dev.aurelium.auraskills.bukkit.skills.fighting.FightingAbilities;
import dev.aurelium.auraskills.bukkit.skills.foraging.ForagingAbilities;
import dev.aurelium.auraskills.bukkit.skills.mining.MiningAbilities;
import dev.aurelium.auraskills.bukkit.trait.AttackDamageTrait;
import dev.aurelium.auraskills.bukkit.trait.DamageReductionTrait;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.mechanics.DamageType;
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
    private final CriticalHandler criticalHandler;

    public DamageListener(AuraSkills plugin) {
        this.plugin = plugin;
        this.criticalHandler = new CriticalHandler(plugin);
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
            handleDamage(event, player);
        }

        // Handles being damaged
        if (event.getEntity() instanceof Player) {
            handleBeingDamaged(event, (Player) event.getEntity());
        }
    }

    private void handleDamage(EntityDamageByEntityEvent event, Player player) {
        // Check disabled world
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return;
        }
        if (player.hasMetadata("NPC")) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) return;
        // Gets player skill
        User user = plugin.getUser(player);

        DamageType damageType = getDamageType(event, player);

        double additive = 0.0;
        // Applies attack damage trait
        var attackDamage = plugin.getTraitManager().getTraitImpl(AttackDamageTrait.class);
        DamageModifier strengthMod = attackDamage.strength(user, damageType);
        additive += applyModifier(event, strengthMod);

        // Apply master abilities
        var abManager = plugin.getAbilityManager();
        switch (damageType) {
            case SWORD -> {
                DamageModifier mod = abManager.getAbilityImpl(FightingAbilities.class).swordMaster(player, user);
                additive += applyModifier(event, mod);
            }
            case BOW -> {
                DamageModifier mod = abManager.getAbilityImpl(ArcheryAbilities.class).bowMaster(player, user);
                additive += applyModifier(event, mod);
            }
            case HOE -> {
                DamageModifier mod = abManager.getAbilityImpl(FarmingAbilities.class).scytheMaster(player, user);
                additive += applyModifier(event, mod);
            }
            case AXE -> {
                DamageModifier mod = abManager.getAbilityImpl(ForagingAbilities.class).axeMaster(player, user);
                additive += applyModifier(event, mod);
            }
            case PICKAXE -> {
                DamageModifier mod = abManager.getAbilityImpl(MiningAbilities.class).pickMaster(player, user);
                additive += applyModifier(event, mod);
            }
            case SHOVEL -> {
                DamageModifier mod = abManager.getAbilityImpl(ExcavationAbilities.class).spadeMaster(player, user);
                additive += applyModifier(event, mod);
            }
        }

        // Apply First Strike
        if (damageType == DamageType.SWORD) {
            DamageModifier mod = abManager.getAbilityImpl(FightingAbilities.class).firstStrike(user, player);
            additive += applyModifier(event, mod);
        }

        // Apply critical
        if (plugin.configBoolean(Option.valueOf("CRITICAL_ENABLED_" + damageType.name()))) {
            DamageModifier mod = criticalHandler.getCrit(player, user);
            additive += applyModifier(event, mod);
        }

        // Charged shot
        if (damageType == DamageType.BOW) {
            DamageModifier mod = plugin.getManaAbilityManager().getProvider(ChargedShot.class).applyChargedShot(event);
            additive += applyModifier(event, mod);
        }

        // Apply additive (ADD_COMBINED) operation
        event.setDamage(event.getDamage() * (1 + additive));
    }

    private void handleBeingDamaged(EntityDamageByEntityEvent event, Player player) {
        // Check disabled world
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return;
        }
        User user = plugin.getUser(player);

        // Handles absorption
        plugin.getManaAbilityManager().getProvider(Absorption.class).handleAbsorption(event, player, user);
        if (event.isCancelled()) return;

        // Handles parry
        FightingAbilities fightingAbilities = plugin.getAbilityManager().getAbilityImpl(FightingAbilities.class);
        fightingAbilities.handleParry(event, player, user);

        // Handles damage reduction trait
        var damageReduction = plugin.getTraitManager().getTraitImpl(DamageReductionTrait.class);
        damageReduction.onDamage(event, user);

        DefenseAbilities defenseAbilities = plugin.getAbilityManager().getAbilityImpl(DefenseAbilities.class);

        // Handles mob master
        defenseAbilities.mobMaster(event, user, player);

        // Handles shielding
        defenseAbilities.shielding(event, user, player);
    }

    @SuppressWarnings("deprecation")
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

    // Returns the value if ADD_COMBINED
    private double applyModifier(EntityDamageByEntityEvent event, DamageModifier modifier) {
        switch (modifier.operation()) {
            case MULTIPLY -> {
                double multiplier = 1.0 + modifier.value();
                event.setDamage(event.getDamage() * multiplier);
            }
            case ADD_BASE -> event.setDamage(event.getDamage() + modifier.value());
            case ADD_COMBINED -> {
                return modifier.value();
            }
        }
        return 0.0;
    }
}
