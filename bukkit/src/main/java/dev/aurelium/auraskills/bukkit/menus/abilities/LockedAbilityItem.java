package dev.aurelium.auraskills.bukkit.menus.abilities;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.ability.Ability;
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

public class LockedAbilityItem extends AbstractAbilityItem {

    public LockedAbilityItem(AuraSkills plugin) {
        super(plugin, "locked_ability");
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderData data, Ability ability) {
        Locale locale = plugin.getUser(player).getLocale();
        switch (placeholder) {
            case "name":
                return ability.getDisplayName(locale);
            case "locked_desc":
                return TextUtil.replace(plugin.getMsg(MenuMessage.LOCKED_DESC, locale),
                        "{desc}", TextUtil.replace(ability.getDescription(locale),
                                "{value}", NumberUtil.format1(ability.getValue(1)),
                                "{value_2}", NumberUtil.format1(ability.getSecondaryValue(1))));
            case "unlocked_at":
                Skill skill = (Skill) menu.getProperty("skill");
                return TextUtil.replace(plugin.getMsg(MenuMessage.UNLOCKED_AT, locale),
                        "{skill}", skill.getDisplayName(locale),
                        "{level}", RomanNumber.toRoman(ability.getUnlock(), plugin));
            case "locked":
                return plugin.getMsg(MenuMessage.LOCKED, locale);
        }
        return placeholder;
    }

    @Override
    public Set<Ability> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        User user = plugin.getUser(player);
        Set<Ability> lockedAbilities = new HashSet<>();
        // Add abilities that player has not unlocked yet
        for (Ability ability : skill.getAbilities()) {
            if (user.getAbilityLevel(ability) <= 0) {
                lockedAbilities.add(ability);
            }
        }
        return lockedAbilities;
    }
}
