package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.modifier.DamageModifier;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.mechanics.DamageType;
import org.bukkit.entity.Player;

public class AttackDamageTrait extends TraitImpl {

    AttackDamageTrait(AuraSkills plugin) {
        super(plugin, Traits.ATTACK_DAMAGE);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    public DamageModifier strength(User user, DamageType damageType) {
        Trait trait = Traits.ATTACK_DAMAGE;
        if (!trait.isEnabled()) return DamageModifier.none();

        if (damageType == DamageType.HAND && !trait.optionBoolean("hand_damage")) {
            return DamageModifier.none();
        } else if (damageType == DamageType.BOW && !trait.optionBoolean("bow_damage")) {
            return DamageModifier.none();
        }
        return applyStrength(user);
    }

    @Override
    public String getMenuDisplay(double value, Trait trait) {
        if (Traits.ATTACK_DAMAGE.optionBoolean("use_percent")) {
            return "+" + NumberUtil.format1(value) + "%";
        } else {
            return "+" + NumberUtil.format1(value);
        }
    }

    private DamageModifier applyStrength(User user) {
        double value = user.getBonusTraitLevel(Traits.ATTACK_DAMAGE);
        if (Traits.ATTACK_DAMAGE.optionBoolean("use_percent")) {
            return new DamageModifier(value / 100, DamageModifier.Operation.MULTIPLY);
        } else {
            return new DamageModifier(value, DamageModifier.Operation.ADD_BASE);
        }
    }

}
