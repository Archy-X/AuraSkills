package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class LockedItem extends SkillLevelItem {

    public LockedItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Integer level) {
        if (placeholder.equals("level")) {
            return String.valueOf(level);
        }
        return replaceMenuMessage(placeholder, player, activeMenu);
    }

    @Override
    public Set<Integer> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        User user = plugin.getUser(player);
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int level = user.getSkillLevel(skill);
        int currentPage = activeMenu.getCurrentPage();
        Set<Integer> levels = new HashSet<>();
        for (int i = ITEMS_PER_PAGE - 1; i >= 0; i--) {
            if (START_LEVEL - 1 + currentPage * ITEMS_PER_PAGE + i > level) {
                levels.add(START_LEVEL + i + currentPage * ITEMS_PER_PAGE);
            } else {
                break;
            }
        }
        return levels;
    }

}
