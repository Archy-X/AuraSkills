package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.component.ComponentData;
import com.archyx.slate.component.ComponentProvider;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractComponent;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class LevelProgressionComponents {

    public static class AbilityUnlock extends AbstractComponent implements ComponentProvider {

        public AbilityUnlock(AuraSkills plugin) {
            super(plugin);
        }

        @Override
        public <T> String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ComponentData componentData, T context) {
            int level = getLevel(activeMenu, context);
            Skill skill = (Skill) activeMenu.getProperty("skill");

            Ability ability = getUnlocked(skill, level).get(componentData.getInstance());

            Locale locale = plugin.getUser(player).getLocale();
            if (placeholder.equals("name")) {
                return ability.getDisplayName(locale);
            } else if (placeholder.equals("desc")) {
                return TextUtil.replace(ability.getDescription(locale),
                        "{value}", NumberUtil.format1(ability.getValue(1)),
                        "{value_2}", NumberUtil.format1(ability.getSecondaryValue(1)));
            }
            return replaceMenuMessage(placeholder, player, activeMenu);
        }

        @Override
        public <T> boolean shouldShow(Player player, ActiveMenu activeMenu, T context) {
            int level = getLevel(activeMenu, context);
            Skill skill = (Skill) activeMenu.getProperty("skill");
            return !getUnlocked(skill, level).isEmpty();
        }

        @Override
        public <T> int getInstances(Player player, ActiveMenu activeMenu, T context) {
            int level = getLevel(activeMenu, context);
            Skill skill = (Skill) activeMenu.getProperty("skill");
            return getUnlocked(skill, level).size();
        }

        private List<Ability> getUnlocked(Skill skill, int level) {
            return plugin.getAbilityManager().getAbilities(skill, level).stream().filter(a -> a.getUnlock() == level).toList();
        }

    }

    public static class AbilityLevel extends AbstractComponent implements ComponentProvider {

        public AbilityLevel(AuraSkills plugin) {
            super(plugin);
        }

        @Override
        public <T> String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ComponentData componentData, T context) {
            int level = getLevel(activeMenu, context);
            Skill skill = (Skill) activeMenu.getProperty("skill");

            Ability ability = getLeveledUp(skill, level).get(componentData.getInstance());
            Locale locale = plugin.getUser(player).getLocale();
            int abilityLevel = ((level - ability.getUnlock()) / ability.getLevelUp()) + 1;
            return switch (placeholder) {
                case "name" -> ability.getDisplayName(locale);
                case "desc" -> TextUtil.replace(ability.getDescription(locale),
                        "{value}", NumberUtil.format1(ability.getValue(abilityLevel)),
                        "{value_2}", NumberUtil.format1(ability.getSecondaryValue(abilityLevel)));
                case "level" -> RomanNumber.toRoman(abilityLevel, plugin);
                default -> replaceMenuMessage(placeholder, player, activeMenu);
            };
        }

        @Override
        public <T> boolean shouldShow(Player player, ActiveMenu activeMenu, T context) {
            int level = getLevel(activeMenu, context);
            Skill skill = (Skill) activeMenu.getProperty("skill");
            return !getLeveledUp(skill, level).isEmpty();
        }

        @Override
        public <T> int getInstances(Player player, ActiveMenu activeMenu, T context) {
            int level = getLevel(activeMenu, context);
            Skill skill = (Skill) activeMenu.getProperty("skill");
            return getLeveledUp(skill, level).size();
        }

        private List<Ability> getLeveledUp(Skill skill, int level) {
            return plugin.getAbilityManager().getAbilities(skill, level).stream().filter(a -> a.getUnlock() != level).toList();
        }
    }

    public static class ManaAbilityUnlock extends AbstractComponent implements ComponentProvider {

        public ManaAbilityUnlock(AuraSkills plugin) {
            super(plugin);
        }

        @Override
        public <T> String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ComponentData componentData, T context) {
            Skill skill = (Skill) activeMenu.getProperty("skill");

            ManaAbility manaAbility = skill.getManaAbility();
            if (manaAbility == null) return placeholder;

            Locale locale = plugin.getUser(player).getLocale();
            if (placeholder.equals("name")) {
                return manaAbility.getDisplayName(locale);
            } else if (placeholder.equals("desc")) {
                return TextUtil.replace(manaAbility.getDescription(locale),
                        "{value}", NumberUtil.format1(manaAbility.getDisplayValue(1)),
                        "{duration}", NumberUtil.format1(getDuration(manaAbility, 1)),
                        "{haste_level}", String.valueOf(ManaAbilities.SPEED_MINE.optionInt("haste_level", 10)));
            }
            return replaceMenuMessage(placeholder, player, activeMenu);
        }

        @Override
        public <T> boolean shouldShow(Player player, ActiveMenu activeMenu, T context) {
            int level = getLevel(activeMenu, context);
            Skill skill = (Skill) activeMenu.getProperty("skill");

            ManaAbility manaAbility = skill.getManaAbility();
            if (manaAbility != null) {
                return manaAbility.getUnlock() == level;
            } else {
                return false;
            }
        }

    }

    public static class ManaAbilityLevel extends AbstractComponent implements ComponentProvider {

        public ManaAbilityLevel(AuraSkills plugin) {
            super(plugin);
        }

        @Override
        public <T> String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ComponentData componentData, T context) {
            Skill skill = (Skill) activeMenu.getProperty("skill");

            ManaAbility manaAbility = skill.getManaAbility();
            if (manaAbility == null) return placeholder;

            Locale locale = plugin.getUser(player).getLocale();

            int level = getLevel(activeMenu, context);
            int manaAbilityLevel = ((level - manaAbility.getUnlock()) / manaAbility.getLevelUp()) + 1;
            return switch (placeholder) {
                case "name" -> manaAbility.getDisplayName(locale);
                case "desc" -> TextUtil.replace(manaAbility.getDescription(locale),
                        "{value}", NumberUtil.format1(manaAbility.getDisplayValue(manaAbilityLevel)),
                        "{duration}", NumberUtil.format1(getDuration(manaAbility, manaAbilityLevel)),
                        "{haste_level}", String.valueOf(ManaAbilities.SPEED_MINE.optionInt("haste_level", 10)));
                case "level" -> RomanNumber.toRoman(manaAbilityLevel, plugin);
                default -> replaceMenuMessage(placeholder, player, activeMenu);
            };
        }

        @Override
        public <T> boolean shouldShow(Player player, ActiveMenu activeMenu, T context) {
            int level = getLevel(activeMenu, context);
            Skill skill = (Skill) activeMenu.getProperty("skill");

            ManaAbility manaAbility = skill.getManaAbility();
            if (manaAbility != null) {
                return plugin.getManaAbilityManager().getManaAbilityAtLevel(skill, level) != null && manaAbility.getUnlock() != level;
            } else {
                return false;
            }
        }

    }

    private static int getLevel(ActiveMenu activeMenu, Object context) {
        int position = (Integer) context;

        Object property = activeMenu.getProperty("items_per_page");
        int itemsPerPage;
        if (property instanceof Integer) {
            itemsPerPage = (Integer) property;
        } else {
            itemsPerPage = 24;
        }

        int currentPage = activeMenu.getCurrentPage();
        return currentPage * itemsPerPage + position;
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
