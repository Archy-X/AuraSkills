package dev.aurelium.auraskills.bukkit.hooks.mythicmobs;

import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skills.defense.DefenseAbilities;
import dev.aurelium.auraskills.bukkit.skills.fighting.FightingAbilities;
import dev.aurelium.auraskills.bukkit.trait.AttackDamageTrait;
import dev.aurelium.auraskills.bukkit.trait.DamageReductionTrait;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.mechanics.DamageType;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicDamageEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.spongepowered.configurate.ConfigurationNode;

public class MythicMobsHook extends Hook implements Listener {
    public MythicMobsHook(AuraSkillsPlugin plugin, ConfigurationNode config) {
        super(plugin, config);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMythicSkillDamage(MythicDamageEvent event) {
        // This is always some sort of skill damage.

        var damager = event.getCaster().getEntity();
        var target = event.getTarget();


        // We don't need to apply strength here.
        // Seems like it is already working fine.

        if (target.isPlayer()) {
            var playerTarget = BukkitAdapter.adapt(target.asPlayer());
            if (plugin.getWorldManager().isDisabledWorld(playerTarget.getWorld().getName())) {
                return;
            }
            User user = plugin.getUserManager().getUser(playerTarget.getUniqueId());


            var plugin = (AuraSkills) this.plugin;

            // we don't need to handle absorption, since it cancels the entire event ahead of time

            var fakeEvent = new EntityDamageByEntityEvent(damager.getBukkitEntity(), target.getBukkitEntity(), event.getDamageMetadata().getDamageCause(), null, event.getDamage());

            // Handles parry
            if (getConfig().node("handle-fighting-abilities").getBoolean()) {
                FightingAbilities fightingAbilities = plugin.getAbilityManager().getAbilityImpl(FightingAbilities.class);
                fightingAbilities.handleParry(fakeEvent, playerTarget, user);
                event.setDamage(fakeEvent.getDamage());
            }

            if (getConfig().node("handle-damage-reduction").getBoolean()) {
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

    @EventHandler
    public void onMechanicLoad(MythicMechanicLoadEvent e) {
        if(e.getMechanicName().equalsIgnoreCase("takeMana")) {
            e.register(new TakeManaMechanic(e));
        }
    }

    @EventHandler
    public void onConditionLoad(MythicConditionLoadEvent e) {
        if(e.getConditionName().equalsIgnoreCase("hasMana")) {
            e.register(new HasManaCondition(e));
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
}
