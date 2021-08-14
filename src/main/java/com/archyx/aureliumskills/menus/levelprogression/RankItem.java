package com.archyx.aureliumskills.menus.levelprogression;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class RankItem extends AbstractItem implements TemplateItemProvider<Skill> {

    public RankItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Skill> getContext() {
        return Skill.class;
    }

    @Override
    public Set<Skill> getDefinedContexts() {
        return new HashSet<>();
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderType type, Skill skill) {
        Locale locale = plugin.getLang().getLocale(player);
        switch (placeholder) {
            case "your_ranking":
                return Lang.getMessage(MenuMessage.YOUR_RANKING, locale);
            case "out_of":
                int rank = getRank(skill, player);
                int size = getSize(skill, player);
                return TextUtil.replace(Lang.getMessage(MenuMessage.RANK_OUT_OF, locale),
                        "{rank}", String.valueOf(rank),
                        "{total}", String.valueOf(size));
            case "percent":
                double percent = getPercent(skill, player);
                if (percent > 1) {
                    return TextUtil.replace(Lang.getMessage(MenuMessage.RANK_PERCENT, locale),
                            "{percent}", String.valueOf(Math.round(percent)));
                } else {
                    return TextUtil.replace(Lang.getMessage(MenuMessage.RANK_PERCENT, locale),
                            "{percent}", NumberUtil.format2(percent));
                }
        }
        return placeholder;
    }

    private double getPercent(Skill skill, Player player) {
        int rank = getRank(skill, player);
        int size = getSize(skill, player);
        return (double) rank / (double) size * 100;
    }

    private int getRank(Skill skill, Player player) {
        return plugin.getLeaderboardManager().getSkillRank(skill, player.getUniqueId());
    }

    private int getSize(Skill skill, Player player) {
        return plugin.getLeaderboardManager().getLeaderboard(skill).size();
    }
}
