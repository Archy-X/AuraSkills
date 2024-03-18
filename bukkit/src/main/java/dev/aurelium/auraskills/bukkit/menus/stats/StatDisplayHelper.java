package dev.aurelium.auraskills.bukkit.menus.stats;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.bukkit.BukkitTraitHandler;

public class StatDisplayHelper {

    private final AuraSkills plugin;

    public StatDisplayHelper(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public String getDisplayLevel(Stat stat, User user) {
        if (isOneToOneWithTrait(stat) && plugin.configBoolean(Option.MENUS_STATS_SHOW_TRAIT_VALUES_DIRECTLY)) {
            // Displays the trait value directly instead of the stat level if the stat has exactly one trait and its modifier is 1
            Trait trait = stat.getTraits().get(0);
            double value = user.getEffectiveTraitLevel(trait);

            BukkitTraitHandler impl = plugin.getTraitManager().getTraitImpl(trait);
            if (impl != null) {
                return impl.getMenuDisplay(value, trait, user.getLocale());
            } else {
                return NumberUtil.format1(value);
            }
        } else {
            return NumberUtil.format1(user.getStatLevel(stat));
        }
    }

    private boolean isOneToOneWithTrait(Stat stat) {
        if (stat.getTraits().size() > 1) return false;
        return stat.getTraitModifier(stat.getTraits().get(0)) == 1.0;
    }

}
