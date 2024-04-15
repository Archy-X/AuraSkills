package dev.aurelium.auraskills.bukkit.skills.foraging;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.damage.DamageType;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ForagingAbilities extends AbilityImpl {

    public ForagingAbilities(AuraSkills plugin) {
        super(plugin, Abilities.LUMBERJACK, Abilities.FORAGER, Abilities.AXE_MASTER, Abilities.SHREDDER, Abilities.VALOR);
    }

    private DamageModifier axeMaster(Player player, User user) {
        var ability = Abilities.AXE_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
    }

    @EventHandler(ignoreCancelled = true)
    public void damageListener(DamageEvent event) {
        var meta = event.getDamageMeta();
        var attacker = meta.getAttackerAsPlayer();

        if (attacker != null) {
            if (meta.getDamageType() == DamageType.AXE) {
                var user = plugin.getUser(attacker);
                meta.addAttackModifier(axeMaster(attacker, user));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void shredder(PlayerItemDamageEvent event) {
        var ability = Abilities.SHREDDER;

        if (isDisabled(ability)) return;

        if (event.isCancelled()) return;

        if (!event.getPlayer().isOnline()) return;

        // If is item taking durabilty damage is armor
        if (!ItemUtils.isArmor(event.getItem().getType())) {
            return;
        }
        // If last damage was from entity
        if (!(event.getPlayer().getLastDamageCause() instanceof EntityDamageByEntityEvent e)) return;
        // If last damage was from player
        if (!(e.getDamager() instanceof Player player)) return;

        if (!player.isOnline()) return;

        if (failsChecks(player, ability)) return;
        // If damage was an attack
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
        // If item used was an axe
        Material mat = player.getInventory().getItemInMainHand().getType();

        boolean isAxe = false;
        switch (mat) {
            case NETHERITE_AXE, DIAMOND_AXE, IRON_AXE, GOLDEN_AXE, STONE_AXE, WOODEN_AXE -> isAxe = true;
        }
        if (!isAxe) {
            return;
        }

        User user = plugin.getUser(player);
        // Checks if shredder is used
        if (user.getAbilityLevel(ability) == 0) return;
        if (rand.nextDouble() < (getValue(ability, user)) / 100) {
            event.setDamage(event.getDamage() * 3);
        }
    }

    public void applyValor(User user) {
        var ability = Abilities.VALOR;
        if (isDisabled(ability)) return;

        if (user.getAbilityLevel(ability) == 0) return;

        user.addStatModifier(new StatModifier("foraging-valor", Stats.STRENGTH, (int) getValue(ability, user)));
    }

    public void removeValor(User user) {
        user.removeStatModifier("foraging-valor");
    }

}
