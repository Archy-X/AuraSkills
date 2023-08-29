package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
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
            case "level":
                return String.valueOf(level);
            case "entries":
                return getRewardEntries(skill, level, player, locale, activeMenu);
        }
        return replaceMenuMessage(placeholder, player, activeMenu);
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
