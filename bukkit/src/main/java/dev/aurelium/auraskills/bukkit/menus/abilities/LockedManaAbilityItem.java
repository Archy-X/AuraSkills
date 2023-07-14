package dev.aurelium.auraskills.bukkit.menus.abilities;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
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
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderData data, ManaAbility manaAbility) {
        Locale locale = plugin.getUser(player).getLocale();
        Skill skill = (Skill) menu.getProperty("skill");
        switch (placeholder) {
            case "name":
                return manaAbility.getDisplayName(locale);
            case "locked_desc":
                return TextUtil.replace(plugin.getMsg(MenuMessage.LOCKED_DESC, locale),
                        "{desc}", TextUtil.replace(manaAbility.getDescription(locale),
                            "{value}", NumberUtil.format1(manaAbility.getDisplayValue(1)),
                            "{haste_level}", String.valueOf(manaAbility.optionInt("haste_level", 10)),
                            "{duration}", NumberUtil.format1(getDuration(manaAbility))));
            case "unlocked_at":
                return TextUtil.replace(plugin.getMsg(MenuMessage.UNLOCKED_AT, locale),
                        "{skill}", skill.getDisplayName(locale),
                        "{level}", RomanNumber.toRoman(manaAbility.getUnlock(), plugin));
            case "locked":
                return plugin.getMsg(MenuMessage.LOCKED, locale);
        }
        return placeholder;
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
