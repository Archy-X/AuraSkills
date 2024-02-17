package dev.aurelium.auraskills.bukkit.skills.excavation;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;

public class ExcavationAbilities extends AbilityImpl {

    public ExcavationAbilities(AuraSkills plugin) {
        super(plugin, Abilities.METAL_DETECTOR, Abilities.EXCAVATOR, Abilities.SPADE_MASTER, Abilities.BIGGER_SCOOP, Abilities.LUCKY_SPADES);
    }

    public DamageModifier spadeMaster(Player player, User user) {
        var ability = Abilities.SPADE_MASTER;

        if (isDisabled(ability) || failsChecks(player, ability)) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        return new DamageModifier(getValue(ability, user) / 100, DamageModifier.Operation.ADD_COMBINED);
    }

}
