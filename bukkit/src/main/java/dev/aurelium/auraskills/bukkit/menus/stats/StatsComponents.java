package dev.aurelium.auraskills.bukkit.menus.stats;

import com.archyx.slate.component.ComponentData;
import com.archyx.slate.component.ComponentProvider;
import com.archyx.slate.item.provider.ListBuilder;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractComponent;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class StatsComponents {

    public static class LeveledBy extends AbstractComponent implements ComponentProvider {

        public LeveledBy(AuraSkills plugin) {
            super(plugin);
        }

        @Override
        public <T> String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ComponentData componentData, T context) {
            if (placeholder.equals("skills")) {
                Locale locale = plugin.getUser(player).getLocale();
                Stat stat = (Stat) context;

                ListBuilder builder = new ListBuilder(data.getListData());

                List<Skill> skillsLeveledBy = plugin.getRewardManager().getSkillsLeveledBy(stat);
                for (Skill skill : skillsLeveledBy) {
                    builder.append(skill.getDisplayName(locale));
                }
                return builder.build();
            }
            return replaceMenuMessage(placeholder, player, activeMenu);
        }

        @Override
        public <T> boolean shouldShow(Player player, ActiveMenu activeMenu, T context) {
            return !plugin.getRewardManager().getSkillsLeveledBy((Stat) context).isEmpty();
        }
    }

}
