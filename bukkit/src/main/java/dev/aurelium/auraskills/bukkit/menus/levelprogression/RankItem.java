package dev.aurelium.auraskills.bukkit.menus.levelprogression;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.text.Replacer;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Map;

public class RankItem extends AbstractItem implements SingleItemProvider {

    public RankItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data) {
        Locale locale = plugin.getUser(player).getLocale();
        Skill skill = (Skill) activeMenu.getProperty("skill");
        return replaceMenuMessage(placeholder, player, activeMenu, new Replacer()
                .map("{rank}", () -> String.valueOf(getRank(skill, player)))
                .map("{total}", () -> String.valueOf(getSize(skill)))
                .map("{percent}", () -> {
                    double percent = getPercent(skill, player);
                    if (percent > 1) {
                        return String.valueOf(Math.round(percent));
                    } else {
                        return NumberUtil.format2(percent);
                    }
                })
                .map("{skill}", () -> skill.getDisplayName(locale)));
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, ItemStack item, SlotPos pos, ActiveMenu activeMenu) {
        Map<String, Object> properties = activeMenu.getProperties();
        properties.put("previous_menu", "level_progression");
        plugin.getMenuManager().openMenu(player, "leaderboard", properties);
    }

    private double getPercent(Skill skill, Player player) {
        int rank = getRank(skill, player);
        int size = getSize(skill);
        if (size == 0) {
            size = 1;
        }
        return (double) rank / (double) size * 100;
    }

    private int getRank(Skill skill, Player player) {
        return plugin.getLeaderboardManager().getSkillRank(skill, player.getUniqueId());
    }

    private int getSize(Skill skill) {
        return plugin.getLeaderboardManager().getLeaderboard(skill).size();
    }
}
