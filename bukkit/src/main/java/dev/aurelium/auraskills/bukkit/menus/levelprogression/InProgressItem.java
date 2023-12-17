package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class InProgressItem extends SkillLevelItem {

    public InProgressItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Integer level) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        User user = plugin.getUser(player);

        double currentXp = user.getSkillXp(skill);
        double xpToNext = plugin.getXpRequirements().getXpRequired(skill, level);
        return switch (placeholder) {
            case "level" -> String.valueOf(level);
            case "percent" -> NumberUtil.format2(currentXp / xpToNext * 100);
            case "current_xp" -> NumberUtil.format2(currentXp);
            case "level_xp" -> String.valueOf((int) xpToNext);
            default -> replaceMenuMessage(placeholder, player, activeMenu);
        };
    }

    @Override
    public Set<Integer> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        User user = plugin.getUser(player);
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int currentPage = activeMenu.getCurrentPage();
        int level = user.getSkillLevel(skill);
        int offset = START_LEVEL - 1;
        if (level >= offset + currentPage * ITEMS_PER_PAGE && level < (currentPage + 1) * ITEMS_PER_PAGE + offset) {
            Set<Integer> levels = new HashSet<>();
            int position = level + 1;
            levels.add(position);
            return levels;
        }
        return new HashSet<>();
    }

}
