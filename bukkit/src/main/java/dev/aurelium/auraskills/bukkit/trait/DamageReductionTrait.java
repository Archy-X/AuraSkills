package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Locale;

public class DamageReductionTrait extends TraitImpl {

    DamageReductionTrait(AuraSkills plugin) {
        super(plugin, Traits.DAMAGE_REDUCTION);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    @Override
    public String getMenuDisplay(double value, Trait trait, Locale locale) {
        return NumberUtil.format1(getReductionValue(value) * 100) + "%";
    }

    public void onDamage(EntityDamageByEntityEvent event, User user) {
        double reduction = user.getEffectiveTraitLevel(Traits.DAMAGE_REDUCTION);
        event.setDamage(event.getDamage() * (1 - getReductionValue(reduction)));
    }

    private double getReductionValue(double value) {
        return -1.0 * Math.pow(1.01, -1.0 * value) + 1;
    }

}
