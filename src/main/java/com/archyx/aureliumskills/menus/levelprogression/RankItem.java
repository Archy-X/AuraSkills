package com.archyx.aureliumskills.menus.levelprogression;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.SingleItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

public class RankItem extends AbstractItem implements SingleItemProvider {

    public RankItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu, @NotNull PlaceholderType type) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        @Nullable Skill skill = (Skill) activeMenu.getProperty("skill");
        assert (null != skill);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "your_ranking":
                m = Lang.getMessage(MenuMessage.YOUR_RANKING, locale);
                break;
            case "out_of":
                int rank = getRank(skill, player);
                int size = getSize(skill, player);
                m = TextUtil.replace(Lang.getMessage(MenuMessage.RANK_OUT_OF, locale),
                        "{rank}", String.valueOf(rank),
                        "{total}", String.valueOf(size));
                break;
            case "percent":
                double percent = getPercent(skill, player);
                if (percent > 1) {
                    m = TextUtil.replace(Lang.getMessage(MenuMessage.RANK_PERCENT, locale),
                            "{percent}", String.valueOf(Math.round(percent)));
                } else {
                    m = TextUtil.replace(Lang.getMessage(MenuMessage.RANK_PERCENT, locale),
                            "{percent}", NumberUtil.format2(percent));
                }
                break;
            case "leaderboard_click":
                m = TextUtil.replace(Lang.getMessage(MenuMessage.LEADERBOARD_CLICK, locale),
                        "{skill}", skill.getDisplayName(locale));
                break;
        }
        assert (null != m);
        return m;
    }

    @Override
    public void onClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull ItemStack item, @NotNull SlotPos pos, @NotNull ActiveMenu activeMenu) {
        Map<String, Object> properties = activeMenu.getProperties();
        properties.put("previous_menu", "level_progression");
        plugin.getMenuManager().openMenu(player, "leaderboard", properties);
    }

    private double getPercent(@NotNull Skill skill, @NotNull Player player) {
        int rank = getRank(skill, player);
        int size = getSize(skill, player);
        return (double) rank / (double) size * 100;
    }

    private int getRank(@NotNull Skill skill, @NotNull Player player) {
        return plugin.getLeaderboardManager().getSkillRank(skill, player.getUniqueId());
    }

    private int getSize(@NotNull Skill skill, @NotNull Player player) {
        return plugin.getLeaderboardManager().getLeaderboard(skill).size();
    }
}
