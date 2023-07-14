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

public class UnlockedManaAbilityItem extends AbstractManaAbilityItem implements TemplateItemProvider<ManaAbility> {
    
    public UnlockedManaAbilityItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderData data, ManaAbility manaAbility) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        switch (placeholder) {
            case "name":
                return manaAbility.getDisplayName(locale);
            case "your_ability_level":
                if (isNotMaxed(user, manaAbility)) {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.YOUR_ABILITY_LEVEL, locale),
                            "{level}", String.valueOf(user.getManaAbilityLevel(manaAbility)));
                } else {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.YOUR_ABILITY_LEVEL_MAXED, locale),
                            "{level}", String.valueOf(user.getManaAbilityLevel(manaAbility)));
                }
            case "unlocked_desc":
                if (isNotMaxed(user, manaAbility)) {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.UNLOCKED_DESC, locale),
                            "{skill}", manaAbility.getSkill().getDisplayName(locale),
                            "{level}", RomanNumber.toRoman(getNextUpgradeLevel(manaAbility, user), plugin),
                            "{desc}", TextUtil.replace(manaAbility.getDescription(locale),
                                    "{value}", getUpgradeValue(manaAbility, user),
                                    "{haste_level}", String.valueOf(manaAbility.optionInt("haste_level", 10)),
                                    "{duration}", getUpgradeDuration(manaAbility, user)));
                } else {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.UNLOCKED_DESC_MAXED, locale),
                            "{desc}", TextUtil.replace(manaAbility.getDescription(locale),
                                    "{value}", getUpgradeValue(manaAbility, user),
                                    "{haste_level}", String.valueOf(manaAbility.optionInt("haste_level", 10)),
                                    "{duration}", getUpgradeDuration(manaAbility, user)));
                }
            case "unlocked":
                return plugin.getMsg(MenuMessage.UNLOCKED, locale);
        }
        return placeholder;
    }

    private int getNextUpgradeLevel(ManaAbility manaAbility, User user) {
        int unlock = manaAbility.getUnlock();
        int levelUp = manaAbility.getLevelUp();
        return unlock + levelUp * user.getManaAbilityLevel(manaAbility);
    }

    private String getUpgradeValue(ManaAbility manaAbility, User user) {
        String currentValue = NumberUtil.format1(manaAbility.getDisplayValue(user.getManaAbilityLevel(manaAbility)));
        String nextValue = NumberUtil.format1(manaAbility.getDisplayValue(user.getManaAbilityLevel(manaAbility) + 1));
        return TextUtil.replace(plugin.getMsg(MenuMessage.DESC_UPGRADE_VALUE, user.getLocale()),
                "{current}", currentValue,
                "{next}", nextValue);
    }

    private String getUpgradeDuration(ManaAbility manaAbility, User playerData) {
        String currentDuration = NumberUtil.format1(getDuration(manaAbility, playerData.getManaAbilityLevel(manaAbility)));
        String nextDuration = NumberUtil.format1(getDuration(manaAbility, playerData.getManaAbilityLevel(manaAbility) + 1));
        return TextUtil.replace(plugin.getMsg(MenuMessage.DESC_UPGRADE_VALUE, playerData.getLocale()),
                "{current}", currentDuration,
                "{next}", nextDuration);
    }

    private boolean isNotMaxed(User user, ManaAbility manaAbility) {
        int maxLevel = manaAbility.getMaxLevel();
        int unlock = manaAbility.getUnlock();
        int levelUp = manaAbility.getLevelUp();
        int maxAllowedBySkill = (manaAbility.getSkill().getMaxLevel() - unlock) / levelUp + 1;
        if (maxLevel == 0 || maxLevel > maxAllowedBySkill) {
            maxLevel = maxAllowedBySkill;
        }
        return user.getManaAbilityLevel(manaAbility) < maxLevel;
    }

    @Override
    public Set<ManaAbility> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        User user = plugin.getUser(player);
        Set<ManaAbility> unlockedManaAbilities = new HashSet<>();
        // Add abilities that player has not unlocked yet
        ManaAbility manaAbility = skill.getManaAbility();
        if (manaAbility != null && user.getManaAbilityLevel(manaAbility) >= 1) {
            unlockedManaAbilities.add(manaAbility);
        }
        return unlockedManaAbilities;
    }

    private double getDuration(ManaAbility manaAbility, int level) {
        if (manaAbility == ManaAbilities.LIGHTNING_BLADE) {
            double baseDuration = ManaAbilities.LIGHTNING_BLADE.optionDouble("base_duration");
            double durationPerLevel = ManaAbilities.LIGHTNING_BLADE.optionDouble("duration_per_level");
            return baseDuration + (durationPerLevel * (level - 1));
        } else {
            return manaAbility.getValue(level);
        }
    }

}
