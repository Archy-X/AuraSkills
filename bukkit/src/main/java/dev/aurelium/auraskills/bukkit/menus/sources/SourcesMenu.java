package dev.aurelium.auraskills.bukkit.menus.sources;

import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.menu.MenuProvider;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractMenu;
import dev.aurelium.auraskills.common.util.text.Replacer;
import org.bukkit.entity.Player;

import java.util.Locale;

public class SourcesMenu extends AbstractMenu implements MenuProvider {

    public SourcesMenu(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu) {
        Locale locale = plugin.getUser(player).getLocale();
        if (placeholder.equals("current_page")) {
            return String.valueOf(activeMenu.getCurrentPage() + 1);
        } else if (placeholder.equals("total_pages")) {
            return String.valueOf(activeMenu.getTotalPages());
        }
        return replaceMenuMessage(placeholder, player, activeMenu, new Replacer()
                .map("{skill}", () -> ((Skill) activeMenu.getProperty("skill")).getDisplayName(locale)));
    }

    @Override
    public int getPages(Player player, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int itemsPerPage = (Integer) activeMenu.getProperty("items_per_page");
        int numSources = plugin.getSkillManager().getSkill(skill).sources().size();
        return (numSources - 1) / itemsPerPage + 1;
    }
}
