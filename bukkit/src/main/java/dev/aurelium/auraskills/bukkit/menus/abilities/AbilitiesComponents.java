package dev.aurelium.auraskills.bukkit.menus.abilities;

import com.archyx.slate.component.ComponentData;
import com.archyx.slate.component.ComponentProvider;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractComponent;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.Replacer;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.Locale;

public class AbilitiesComponents {

    public static class YourLevel extends AbstractComponent implements ComponentProvider {

        public YourLevel(AuraSkills plugin) {
            super(plugin);
        }

        @Override
        public <T> String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ComponentData componentData, T context) {
            if (placeholder.equals("level")) {
                return String.valueOf(plugin.getUser(player).getAbstractAbilityLevel((AbstractAbility) context));
            }
            return replaceMenuMessage(placeholder, player, activeMenu);
        }

        @Override
        public <T> boolean shouldShow(Player player, ActiveMenu activeMenu, T context) {
            return isNotMaxed(plugin.getUser(player), (AbstractAbility) context);
        }

    }

    public static class YourLevelMaxed extends AbstractComponent implements ComponentProvider {

        public YourLevelMaxed(AuraSkills plugin) {
            super(plugin);
        }

        @Override
        public <T> String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ComponentData componentData, T context) {
            if (placeholder.equals("level")) {
                return String.valueOf(plugin.getUser(player).getAbstractAbilityLevel((AbstractAbility) context));
            }
            return replaceMenuMessage(placeholder, player, activeMenu);
        }

        @Override
        public <T> boolean shouldShow(Player player, ActiveMenu activeMenu, T context) {
            return !isNotMaxed(plugin.getUser(player), (AbstractAbility) context);
        }

    }

    public static class UnlockedDesc extends AbstractComponent implements ComponentProvider {

        public UnlockedDesc(AuraSkills plugin) {
            super(plugin);
        }

        @Override
        public <T> String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ComponentData componentData, T context) {
            Locale locale = plugin.getUser(player).getLocale();
            if (placeholder.equals("desc")) {
                User user = plugin.getUser(player);
                if (context instanceof Ability ability) {
                    return TextUtil.replace(ability.getDescription(locale),
                            "{value}", getUpgradeValue(ability, user, activeMenu),
                            "{value_2}", getUpgradeValue2(ability, user, activeMenu));
                } else if (context instanceof ManaAbility manaAbility) {
                    return TextUtil.replace(manaAbility.getDescription(locale),
                            "{value}", getUpgradeValue(manaAbility, user, activeMenu),
                            "{haste_level}", String.valueOf(manaAbility.optionInt("haste_level", 10)),
                            "{duration}", getUpgradeDuration(manaAbility, user, activeMenu));
                }
            }
            return replaceMenuMessage(placeholder, player, activeMenu, new Replacer()
                    .map("{skill}", () -> ((AbstractAbility) context).getSkill().getDisplayName(locale))
                    .map("{level}", () -> RomanNumber.toRoman(getNextUpgradeLevel((AbstractAbility) context, plugin.getUser(player)), plugin)));
        }

        @Override
        public <T> boolean shouldShow(Player player, ActiveMenu activeMenu, T context) {
            return isNotMaxed(plugin.getUser(player), (AbstractAbility) context);
        }

        private int getNextUpgradeLevel(AbstractAbility ability, User user) {
            int unlock = ability.getUnlock();
            int levelUp = ability.getLevelUp();
            return unlock + levelUp * user.getAbstractAbilityLevel(ability);
        }

        private String getCurrentValue(Ability ability, User user) {
            return NumberUtil.format1(ability.getValue(user.getAbilityLevel(ability)));
        }

        private String getCurrentValue2(Ability ability, User user) {
            return NumberUtil.format1(ability.getSecondaryValue(user.getAbilityLevel(ability)));
        }

        private String getUpgradeValue(Ability ability, User user, ActiveMenu activeMenu) {
            String currentValue = getCurrentValue(ability, user);
            String nextValue = NumberUtil.format1(ability.getValue(user.getAbilityLevel(ability) + 1));
            return TextUtil.replace(activeMenu.getFormat("desc_upgrade_value"),
                    "{current}", currentValue,
                    "{next}", nextValue);
        }

        private String getUpgradeValue2(Ability ability, User user, ActiveMenu activeMenu) {
            String currentValue = getCurrentValue2(ability, user);
            String nextValue = NumberUtil.format1(ability.getSecondaryValue(user.getAbilityLevel(ability) + 1));
            return TextUtil.replace(activeMenu.getFormat("desc_upgrade_value"),
                    "{current}", currentValue,
                    "{next}", nextValue);
        }

        private String getUpgradeValue(ManaAbility manaAbility, User user, ActiveMenu activeMenu) {
            String currentValue = NumberUtil.format1(manaAbility.getDisplayValue(user.getManaAbilityLevel(manaAbility)));
            String nextValue = NumberUtil.format1(manaAbility.getDisplayValue(user.getManaAbilityLevel(manaAbility) + 1));
            return TextUtil.replace(activeMenu.getFormat("desc_upgrade_value"),
                    "{current}", currentValue,
                    "{next}", nextValue);
        }

        private String getUpgradeDuration(ManaAbility manaAbility, User user, ActiveMenu activeMenu) {
            String currentDuration = NumberUtil.format1(getDuration(manaAbility, user.getManaAbilityLevel(manaAbility)));
            String nextDuration = NumberUtil.format1(getDuration(manaAbility, user.getManaAbilityLevel(manaAbility) + 1));
            return TextUtil.replace(activeMenu.getFormat("desc_upgrade_value"),
                    "{current}", currentDuration,
                    "{next}", nextDuration);
        }

    }

    public static class UnlockedDescMaxed extends AbstractComponent implements ComponentProvider {

        public UnlockedDescMaxed(AuraSkills plugin) {
            super(plugin);
        }

        @Override
        public <T> String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ComponentData componentData, T context) {
            Locale locale = plugin.getUser(player).getLocale();
            if (placeholder.equals("desc")) {
                User user = plugin.getUser(player);
                if (context instanceof Ability ability) {
                    return TextUtil.replace(ability.getDescription(locale),
                            "{value}", NumberUtil.format1(ability.getValue(user.getAbilityLevel(ability))),
                            "{value_2}", NumberUtil.format1(ability.getSecondaryValue(user.getAbilityLevel(ability))));
                } else if (context instanceof ManaAbility manaAbility) {
                    return TextUtil.replace(manaAbility.getDescription(locale),
                            "{value}", NumberUtil.format1(manaAbility.getDisplayValue(user.getManaAbilityLevel(manaAbility))),
                            "{haste_level}", String.valueOf(manaAbility.optionInt("haste_level", 10)),
                            "{duration}", NumberUtil.format1(getDuration(manaAbility, user.getManaAbilityLevel(manaAbility))));
                }
            }
            return replaceMenuMessage(placeholder, player, activeMenu);
        }

        @Override
        public <T> boolean shouldShow(Player player, ActiveMenu activeMenu, T context) {
            return !isNotMaxed(plugin.getUser(player), (AbstractAbility) context);
        }
    }

    private static boolean isNotMaxed(User user, AbstractAbility ability) {
        int maxLevel = ability.getMaxLevel();
        int unlock = ability.getUnlock();
        int levelUp = ability.getLevelUp();
        int maxAllowedBySkill = (ability.getSkill().getMaxLevel() - unlock) / levelUp + 1;
        if (maxLevel == 0 || maxLevel > maxAllowedBySkill) {
            maxLevel = maxAllowedBySkill;
        }
        return user.getAbstractAbilityLevel(ability) < maxLevel;
    }

    private static double getDuration(ManaAbility manaAbility, int level) {
        if (manaAbility == ManaAbilities.LIGHTNING_BLADE) {
            double baseDuration = ManaAbilities.LIGHTNING_BLADE.optionDouble("base_duration");
            double durationPerLevel = ManaAbilities.LIGHTNING_BLADE.optionDouble("duration_per_level");
            return baseDuration + (durationPerLevel * (level - 1));
        } else {
            return manaAbility.getValue(level);
        }
    }

}
