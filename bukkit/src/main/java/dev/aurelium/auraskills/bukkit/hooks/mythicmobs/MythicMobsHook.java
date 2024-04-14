package dev.aurelium.auraskills.bukkit.hooks.mythicmobs;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.listeners.CriticalHandler;
import dev.aurelium.auraskills.bukkit.skills.archery.ArcheryAbilities;
import dev.aurelium.auraskills.bukkit.skills.defense.DefenseAbilities;
import dev.aurelium.auraskills.bukkit.skills.excavation.ExcavationAbilities;
import dev.aurelium.auraskills.bukkit.skills.farming.FarmingAbilities;
import dev.aurelium.auraskills.bukkit.skills.fighting.FightingAbilities;
import dev.aurelium.auraskills.bukkit.skills.foraging.ForagingAbilities;
import dev.aurelium.auraskills.bukkit.skills.mining.MiningAbilities;
import dev.aurelium.auraskills.bukkit.trait.AttackDamageTrait;
import dev.aurelium.auraskills.bukkit.trait.DamageReductionTrait;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.mechanics.DamageType;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.spongepowered.configurate.ConfigurationNode;

import java.lang.reflect.Constructor;

public class MythicMobsHook extends Hook implements Listener {
    private final AuraSkills plugin;
    private final CriticalHandler criticalHandler;

    public MythicMobsHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.plugin = plugin;
        this.criticalHandler = new CriticalHandler(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMythicSkillDamage(MythicDamageEvent event) {
        // This is always some sort of skill/mechanic damage.

        if (!getConfig().node("handle_fighting_abilities").getBoolean() &&
                !getConfig().node("handle_damage_reduction").getBoolean()) {
            return;
        }

        var damager = event.getCaster().getEntity();
        var target = event.getTarget();
        var fakeEvent = createDamageEvent(damager.getBukkitEntity(), target.getBukkitEntity(), event.getDamageMetadata().getDamageCause(), event.getDamage());

        // If the damager is a player, server probably has MythicCrucible addon, that let you use
        // mechanics/skills on weapons.
        if (damager.isPlayer() && getConfig().node("handle_fighting_abilities").getBoolean()) {
            var player = BukkitAdapter.adapt(damager.asPlayer());
            if (player.hasMetadata("NPC")) return;
            if (event.getDamageMetadata().getDamageCause() == EntityDamageEvent.DamageCause.THORNS) return;

            User user = plugin.getUser(player);

            DamageType damageType = getDamageType(player);
            var additive = 0;

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

            // First strike is impossible to activate here

            // Apply critical
            if (plugin.configBoolean(Option.valueOf("CRITICAL_ENABLED_" + damageType.name()))) {
                DamageModifier mod = criticalHandler.getCrit(player, user);
                additive += applyModifier(event, mod);
            }

            // Charged shot is probably impossible to activate here, or it is probably impossible to make it work
            // as it supposed to

            // Apply additive (ADD_COMBINED) operation
            fakeEvent.setDamage(event.getDamage() * (1 + additive));
            event.setDamage(event.getDamage() * (1 + additive));
        }

        // Handles being damaged
        if (target.isPlayer()) {
            var playerTarget = BukkitAdapter.adapt(target.asPlayer());
            if (plugin.getWorldManager().isDisabledWorld(playerTarget.getWorld().getName())) {
                return;
            }

            User user = plugin.getUser(playerTarget);

            // we don't need to handle absorption, since it cancels the entire event ahead of time

            // Handles parry
            if (getConfig().node("handle_fighting_abilities").getBoolean()) {
                FightingAbilities fightingAbilities = plugin.getAbilityManager().getAbilityImpl(FightingAbilities.class);
                fightingAbilities.handleParry(fakeEvent, playerTarget, user);
                event.setDamage(fakeEvent.getDamage());
            }

            if (getConfig().node("handle_damage_reduction").getBoolean()) {
                // Handles damage reduction trait
                var damageReduction = plugin.getTraitManager().getTraitImpl(DamageReductionTrait.class);
                damageReduction.onDamage(fakeEvent, user);

                DefenseAbilities defenseAbilities = plugin.getAbilityManager().getAbilityImpl(DefenseAbilities.class);

                // Handles mob master
                defenseAbilities.mobMaster(fakeEvent, user, playerTarget);

                // Handles shielding
                defenseAbilities.shielding(fakeEvent, user, playerTarget);

                event.setDamage(fakeEvent.getDamage());
            }
        }
    }

    public boolean shouldPreventEntityXp(Entity entity) {
        if(!getConfig().node("prevent_regular_xp").getBoolean()) return false;
        return MythicBukkit.inst().getMobManager().isMythicMob(entity);
    }

    @EventHandler
    public void onMechanicLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("takeMana")) {
            event.register(new TakeManaMechanic(plugin, event));
        } else if (event.getMechanicName().equalsIgnoreCase("giveSkillXP")) {
            event.register(new GiveSkillXpMechanic(plugin, event));
        }
    }

    @EventHandler
    public void onConditionLoad(MythicConditionLoadEvent event) {
        if (event.getConditionName().equalsIgnoreCase("hasMana")) {
            event.register(new HasManaCondition(plugin, event));
        }
    }

    private double applyModifier(MythicDamageEvent event, DamageModifier modifier) {
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

    @Override
    public Class<? extends Hook> getTypeClass() {
        return MythicMobsHook.class;
    }

    public EntityDamageByEntityEvent createDamageEvent(Entity damager, Entity damagee, EntityDamageEvent.DamageCause cause, double damage) {
        // This constructor only exists in 1.20.4+ but damageSource builder is experimental API
        // Right now it works with a simple null
        if (VersionUtils.isAtLeastVersion(20, 4)) {
            return new EntityDamageByEntityEvent(damager, damagee, cause, null, damage);
        }

        // This constructor is marked for removal. Use reflection to prevent future issues in code.
        // This way we can support older version than 1.20.4
        try {
            Class<?> clazz = Class.forName("org.bukkit.event.entity.EntityDamageByEntityEvent");
            Constructor<?> constructor = clazz.getConstructor(Entity.class, Entity.class, EntityDamageEvent.DamageCause.class, double.class);
            Object eventInstance = constructor.newInstance(damager, damagee, cause, damage);

            return (EntityDamageByEntityEvent) eventInstance;
        } catch (Exception e) {
            plugin.logger().warn("MythicMobs hook tried to create an EntityDamageByEntityEvent with a constructor that is not supported in this server version. Please report this.");
        }
        return null;
    }

    private DamageType getDamageType(Player player) {
        // With MythicDamageEvent we don't know if a projectile/trident is involved but
        // for the most part is doesn't really matter. Only thing that matters if what item was
        // used when the skill/mechanic was cast.
        // There can be edge cases if someone puts a damage mechanic on the armor for example
        // with the ~onDamaged trigger, although this is unlikely.
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
        } else if (material.equals(Material.BOW) || material.equals(Material.TRIDENT)) {
            return DamageType.BOW;
        }
        return DamageType.OTHER;
    }
}
