package dev.aurelium.auraskills.bukkit.menus.abilities;

import com.archyx.slate.component.ComponentData;
import com.archyx.slate.component.ComponentProvider;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractComponent;
import dev.aurelium.auraskills.common.ability.AbilityUtil;
import dev.aurelium.auraskills.common.user.User;
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
                String format = activeMenu.getFormat("desc_upgrade_value");
                if (context instanceof Ability ability) {
                    int level = user.getAbilityLevel(ability);
                    String desc = plugin.getAbilityManager().getBaseDescription(ability, locale, user);
                    return TextUtil.replace(desc,
                            "{value}", AbilityUtil.getUpgradeValue(ability, level, format),
                            "{value_2}", AbilityUtil.getUpgradeValue2(ability, level, format));
                } else if (context instanceof ManaAbility manaAbility) {
                    int level = user.getManaAbilityLevel(manaAbility);
                    String desc = plugin.getManaAbilityManager().getBaseDescription(manaAbility, locale, user);
                    return TextUtil.replace(desc,
                            "{value}", AbilityUtil.getUpgradeValue(manaAbility, level, format),
                            "{duration}", AbilityUtil.getUpgradeDuration(manaAbility, level, format));
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
                    String desc = plugin.getAbilityManager().getBaseDescription(ability, locale, user);
                    return TextUtil.replace(desc,
                            "{value}", NumberUtil.format1(ability.getValue(user.getAbilityLevel(ability))),
                            "{value_2}", NumberUtil.format1(ability.getSecondaryValue(user.getAbilityLevel(ability))));
                } else if (context instanceof ManaAbility manaAbility) {
                    String desc = plugin.getManaAbilityManager().getBaseDescription(manaAbility, locale, user);
                    return TextUtil.replace(desc,
                            "{value}", NumberUtil.format1(manaAbility.getDisplayValue(user.getManaAbilityLevel(manaAbility))),
                            "{duration}", NumberUtil.format1(AbilityUtil.getDuration(manaAbility, user.getManaAbilityLevel(manaAbility))));
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

}
