package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LockedItem extends SkillLevelItem {

    public LockedItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Integer position) {
        Locale locale = plugin.getUser(player).getLocale();
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int level = getLevel(activeMenu, position);
        switch (placeholder) {
            case "level_locked":
                return TextUtil.replace(plugin.getMsg(MenuMessage.LEVEL_LOCKED, locale),"{level}", RomanNumber.toRoman(level, plugin));
            case "level_number":
                return TextUtil.replace(plugin.getMsg(MenuMessage.LEVEL_NUMBER, locale), "{level}", String.valueOf(level));
            case "rewards":
                return getRewardsLore(skill, level, player, locale);
            case "ability":
                return getAbilityLore(skill, level, locale);
            case "mana_ability":
                return getManaAbilityLore(skill, level, locale);
            case "locked":
                return plugin.getMsg(MenuMessage.LOCKED, locale);
        }
        return placeholder;
    }

    @Override
    public Set<Integer> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        User user = plugin.getUser(player);
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int level = user.getSkillLevel(skill);
        int itemsPerPage = getItemsPerPage(activeMenu);
        int currentPage = activeMenu.getCurrentPage();
        Set<Integer> levels = new HashSet<>();
        for (int i = itemsPerPage - 1; i >= 0; i--) {
            if (1 + currentPage * itemsPerPage + i > level) {
                levels.add(2 + i);
            } else {
                break;
            }
        }
        return levels;
    }

}
