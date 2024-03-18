package dev.aurelium.auraskills.bukkit.menus.abilities;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.Replacer;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LockedManaAbilityItem extends AbstractManaAbilityItem implements TemplateItemProvider<ManaAbility> {

    public LockedManaAbilityItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ManaAbility manaAbility) {
        Locale locale = plugin.getUser(player).getLocale();
        return switch (placeholder) {
            case "name" -> manaAbility.getDisplayName(locale);
            case "desc" -> TextUtil.replace(plugin.getManaAbilityManager().getBaseDescription(manaAbility, locale, plugin.getUser(player)),
                    "{value}", NumberUtil.format1(manaAbility.getDisplayValue(1)),
                    "{duration}", NumberUtil.format1(getDuration(manaAbility)));
            default -> replaceMenuMessage(placeholder, player, activeMenu, new Replacer()
                    .map("{skill}", () -> {
                        Skill skill = ((Skill) activeMenu.getProperty("skill"));
                        return skill.getDisplayName(locale);
                    })
                    .map("{level}", () -> RomanNumber.toRoman(manaAbility.getUnlock(), plugin)));
        };
    }

    @Override
    public Set<ManaAbility> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Set<ManaAbility> lockedManaAbilities = new HashSet<>();
        Skill skill = (Skill) activeMenu.getProperty("skill");
        ManaAbility manaAbility = skill.getManaAbility();
        User user = plugin.getUser(player);
        if (manaAbility != null) {
            if (user.getManaAbilityLevel(manaAbility) <= 0) {
                lockedManaAbilities.add(manaAbility);
            }
        }
        return lockedManaAbilities;
    }

    private double getDuration(ManaAbility manaAbility) {
        if (manaAbility == ManaAbilities.LIGHTNING_BLADE) {
            return ManaAbilities.LIGHTNING_BLADE.optionDouble("base_duration");
        } else {
            return manaAbility.getValue(1);
        }
    }

}
