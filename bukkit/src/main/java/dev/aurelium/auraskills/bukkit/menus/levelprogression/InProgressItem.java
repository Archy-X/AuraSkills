package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
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

public class InProgressItem extends SkillLevelItem {

    public InProgressItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Integer position) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        int level = getLevel(activeMenu, position);
        switch (placeholder) {
            case "level_in_progress":
                return TextUtil.replace(plugin.getMsg(MenuMessage.LEVEL_IN_PROGRESS, locale),"{level}", RomanNumber.toRoman(level, plugin));
            case "level_number":
                return TextUtil.replace(plugin.getMsg(MenuMessage.LEVEL_NUMBER, locale), "{level}", String.valueOf(level));
            case "rewards":
                return getRewardsLore(skill, level, player, locale);
            case "ability":
                return getAbilityLore(skill, level, locale);
            case "mana_ability":
                return getManaAbilityLore(skill, level, locale);
            case "progress":
                double currentXp = user.getSkillXp(skill);
                double xpToNext = plugin.getXpRequirements().getXpRequired(skill, level);
                return TextUtil.replace(plugin.getMsg(MenuMessage.PROGRESS, locale)
                        ,"{percent}", NumberUtil.format2(currentXp / xpToNext * 100)
                        ,"{current_xp}", NumberUtil.format2(currentXp)
                        ,"{level_xp}", String.valueOf((int) xpToNext));
            case "in_progress":
                return plugin.getMsg(MenuMessage.IN_PROGRESS, locale);
        }
        return placeholder;
    }

    @Override
    public Set<Integer> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        User user = plugin.getUser(player);
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int itemsPerPage = getItemsPerPage(activeMenu);
        int currentPage = activeMenu.getCurrentPage();
        int level = user.getSkillLevel(skill);
        if (level >= 1 + currentPage * itemsPerPage && level < (currentPage + 1) * itemsPerPage + 1) {
            Set<Integer> levels = new HashSet<>();
            int position = (level + 1) % itemsPerPage; // Calculate the first-page equivalent next level
            if (position == 0) { // Account for next skill level 24
                position = itemsPerPage;
            } else if (position == 1) { // Account for next skill level 25
                position = itemsPerPage + 1;
            }
            levels.add(position);
            return levels;
        }
        return new HashSet<>();
    }

}
