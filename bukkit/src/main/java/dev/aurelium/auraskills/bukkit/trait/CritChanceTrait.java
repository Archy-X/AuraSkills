package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Random;

public class CritChanceTrait extends TraitImpl {

    private final Random rand = new Random();

    CritChanceTrait(AuraSkills plugin) {
        super(plugin, Traits.CRIT_CHANCE);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return Traits.CRIT_CHANCE.optionDouble("base");
    }

    @Override
    public String getMenuDisplay(double value, Trait trait, Locale locale) {
        return NumberUtil.format1(value) + "%";
    }

    public boolean isCrit(User user) {
        return isCrit(user, false);
    }

    public boolean isCrit(User user, boolean isPvP) {
        double critChance;
        // Check if PvP equipment-only mode is enabled
        if (isPvP && plugin.configBoolean(Option.PVP_ONLY_EQUIPMENT_STATS)) {
            // In PvP mode, use base (from config) + equipment-only bonus
            double base = Traits.CRIT_CHANCE.optionDouble("base");
            critChance = base + user.getBonusTraitLevelEquipmentOnly(Traits.CRIT_CHANCE);
        } else {
            critChance = user.getEffectiveTraitLevel(Traits.CRIT_CHANCE);
        }
        return rand.nextDouble() < (critChance / 100);
    }
}
