package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.mechanics.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackDamageTrait extends TraitImpl {

    AttackDamageTrait(AuraSkills plugin) {
        super(plugin, Traits.ATTACK_DAMAGE);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    public void strength(EntityDamageByEntityEvent event, User user, DamageType damageType) {
        Trait trait = Traits.ATTACK_DAMAGE;
        if (damageType == DamageType.HAND && trait.optionBoolean("hand_damage")) {
            applyStrength(event, user);

        } else if (damageType == DamageType.BOW && trait.optionBoolean("bow_damage")) {
            applyStrength(event, user);
        } else {
            applyStrength(event, user);
        }
    }

    @Override
    public String getMenuDisplay(double value, Trait trait) {
        if (Traits.ATTACK_DAMAGE.optionBoolean("use_percent")) {
            return "+" + NumberUtil.format1(value) + "%";
        } else {
            return "+" + NumberUtil.format1(value);
        }
    }

    private void applyStrength(EntityDamageByEntityEvent event, User user) {
        double value = user.getBonusTraitLevel(Traits.ATTACK_DAMAGE);
        if (Traits.ATTACK_DAMAGE.optionBoolean("use_percent")) {
            event.setDamage(event.getDamage() * (1 + (value) / 100));
        } else {
            event.setDamage(event.getDamage() + value);
        }
    }

}
