package dev.aurelium.auraskills.bukkit.menus.abilities;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.player.User;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class UnlockedAbilityItem extends AbstractAbilityItem {

    public UnlockedAbilityItem(AuraSkills plugin) {
        super(plugin, "unlocked_ability");
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderData data, Ability ability) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        switch (placeholder) {
            case "name":
                return ability.getDisplayName(locale);
            case "your_ability_level":
                if (isNotMaxed(user, ability)) {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.YOUR_ABILITY_LEVEL, locale),
                            "{level}", String.valueOf(user.getAbilityLevel(ability)));
                } else {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.YOUR_ABILITY_LEVEL_MAXED, locale),
                            "{level}", String.valueOf(user.getAbilityLevel(ability)));
                }
            case "unlocked_desc":
                if (isNotMaxed(user, ability)) {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.UNLOCKED_DESC, locale),
                            "{skill}", ability.getSkill().getDisplayName(locale),
                            "{level}", RomanNumber.toRoman(getNextUpgradeLevel(ability, user), plugin),
                            "{desc}", TextUtil.replace(ability.getDescription(locale),
                                    "{value}", getUpgradeValue(ability, user),
                                    "{value_2}", getUpgradeValue2(ability, user)));
                } else {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.UNLOCKED_DESC_MAXED, locale),
                            "{desc}", TextUtil.replace(ability.getDescription(locale),
                                    "{value}", getCurrentValue(ability, user),
                                    "{value_2}", getCurrentValue2(ability, user)));
                }
            case "unlocked":
                return plugin.getMsg(MenuMessage.UNLOCKED, locale);
        }
        return placeholder;
    }

    private int getNextUpgradeLevel(Ability ability, User user) {
        int unlock = ability.getUnlock();
        int levelUp = ability.getLevelUp();
        return unlock + levelUp * user.getAbilityLevel(ability);
    }

    private String getCurrentValue(Ability ability, User user) {
        return NumberUtil.format1(ability.getValue(user.getAbilityLevel(ability)));
    }

    private String getCurrentValue2(Ability ability, User user) {
        return NumberUtil.format1(ability.getSecondaryValue(user.getAbilityLevel(ability)));
    }

    private String getUpgradeValue(Ability ability, User user) {
        String currentValue = getCurrentValue(ability, user);
        String nextValue = NumberUtil.format1(ability.getValue(user.getAbilityLevel(ability) + 1));
        Locale locale = user.getLocale();
        return TextUtil.replace(plugin.getMsg(MenuMessage.DESC_UPGRADE_VALUE, locale),
                "{current}", currentValue,
                "{next}", nextValue);
    }

    private String getUpgradeValue2(Ability ability, User user) {
        String currentValue = getCurrentValue2(ability, user);
        String nextValue = NumberUtil.format1(ability.getSecondaryValue(user.getAbilityLevel(ability) + 1));
        Locale locale = user.getLocale();
        return TextUtil.replace(plugin.getMsg(MenuMessage.DESC_UPGRADE_VALUE, locale),
                "{current}", currentValue,
                "{next}", nextValue);
    }

    private boolean isNotMaxed(User playerData, Ability ability) {
        int maxLevel = ability.getMaxLevel();
        int unlock = ability.getUnlock();
        int levelUp = ability.getLevelUp();
        int maxAllowedBySkill = (ability.getSkill().getMaxLevel() - unlock) / levelUp + 1;
        if (maxLevel == 0 || maxLevel > maxAllowedBySkill) {
            maxLevel = maxAllowedBySkill;
        }
        return playerData.getAbilityLevel(ability) < maxLevel;
    }

    @Override
    public Set<Ability> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        User user = plugin.getUser(player);
        Set<Ability> unlockedAbilities = new HashSet<>();
        // Add abilities that player has not unlocked yet
        for (Ability ability : skill.getAbilities()) {
            if (user.getAbilityLevel(ability) >= 1) {
                unlockedAbilities.add(ability);
            }
        }
        return unlockedAbilities;
    }
}
