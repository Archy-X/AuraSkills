package dev.aurelium.auraskills.bukkit.skills.archery;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ArcheryAbilities extends AbilityImpl {

    private final String STUN_MODIFIER_NAME = "AureliumSkills-Stun";

    public ArcheryAbilities(AuraSkills plugin) {
        super(plugin, Abilities.CRIT_CHANCE, Abilities.ARCHER, Abilities.BOW_MASTER, Abilities.PIERCING, Abilities.STUN);
    }

    public void reloadCritChance(Player player, User user) {
        Ability ability = Abilities.CRIT_CHANCE;
        String modifierName = "archery_ability";
        user.removeTraitModifier(modifierName, false);

        if (isDisabled(ability)) return;
        if (failsChecks(player, ability)) return;

        double value = getValue(ability, user);
        user.addTraitModifier(new TraitModifier(modifierName, Traits.CRIT_CHANCE, value), false);
    }

    @EventHandler
    public void onLevelUp(SkillLevelUpEvent event) {
        if (!event.getSkill().equals(Skills.ARCHERY)) {
            return;
        }
        Player player = event.getPlayer();
        User user = plugin.getUser(player);
        reloadCritChance(player, user);
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
                arrow.setBounce(false);
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

}
