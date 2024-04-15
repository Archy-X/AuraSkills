package dev.aurelium.auraskills.bukkit.skills.mining;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.damage.DamageType;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class MiningAbilities extends AbilityImpl {

    public MiningAbilities(AuraSkills plugin) {
        super(plugin, Abilities.LUCKY_MINER, Abilities.MINER, Abilities.PICK_MASTER, Abilities.HARDENED_ARMOR, Abilities.STAMINA);
    }

    public boolean dropsMineralDirectly(Block block) {
        Material mat = block.getType();
        switch (mat) {
            case DIAMOND_ORE, REDSTONE_ORE, EMERALD_ORE, COAL_ORE, LAPIS_ORE, NETHER_QUARTZ_ORE, NETHER_GOLD_ORE:
                return true;
        }
        if (VersionUtils.isAtLeastVersion(17)) {
            switch (mat) {
                case IRON_ORE, DEEPSLATE_IRON_ORE, GOLD_ORE, DEEPSLATE_GOLD_ORE, COPPER_ORE, DEEPSLATE_COPPER_ORE,
                        DEEPSLATE_DIAMOND_ORE, DEEPSLATE_REDSTONE_ORE, DEEPSLATE_EMERALD_ORE,
                        DEEPSLATE_COAL_ORE, DEEPSLATE_LAPIS_ORE:
                    return true;
            }
        }
        return false;
    }

    private DamageModifier pickMaster(Player player, User user) {
        var ability = Abilities.PICK_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
    }

    @EventHandler(ignoreCancelled = true)
    public void damageListener(DamageEvent event) {
        var meta = event.getDamageMeta();
        var attacker = meta.getAttackerAsPlayer();

        if (attacker != null) {
            if (meta.getDamageType() == DamageType.PICKAXE) {
                var user = plugin.getUser(attacker);
                meta.addAttackModifier(pickMaster(attacker, user));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void hardenedArmor(PlayerItemDamageEvent event) {
        var ability = Abilities.HARDENED_ARMOR;

        if (isDisabled(ability)) return;

        Player player = event.getPlayer();

        // Checks if item damaged is armor
        if (!ItemUtils.isArmor(event.getItem().getType())) return;

        User user = plugin.getUser(player);

        if (failsChecks(player, ability)) return;

        // Applies ability
        if (rand.nextDouble() < (getValue(ability, user) / 100)) {
            event.setCancelled(true);
        }
    }

    public void applyStamina(Player player, User user) {
        var ability = Abilities.STAMINA;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (user.getAbilityLevel(ability) == 0) return;

        user.addStatModifier(new StatModifier("mining-stamina", Stats.TOUGHNESS, (int) getValue(ability, user)));
    }

    public void removeStamina(User user) {
        user.removeStatModifier("mining-stamina");
    }

}
