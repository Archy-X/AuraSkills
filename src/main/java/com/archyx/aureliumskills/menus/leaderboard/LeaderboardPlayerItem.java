package com.archyx.aureliumskills.menus.leaderboard;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.leaderboard.SkillValue;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class LeaderboardPlayerItem extends AbstractItem implements TemplateItemProvider<Integer> {

    public LeaderboardPlayerItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Integer> getContext() {
        return Integer.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderType placeholderType, Integer place) {
        Locale locale = plugin.getLang().getLocale(player);
        Skill skill = (Skill) activeMenu.getProperty("skill");
        switch (placeholder) {
            case "place":
                return String.valueOf(place);
            case "player":
                SkillValue skillValue = plugin.getLeaderboardManager().getLeaderboard(skill, place, 1).get(0);
                UUID id = skillValue.getId();
                return Bukkit.getOfflinePlayer(id).getName();
            case "skill_level":
                SkillValue value = plugin.getLeaderboardManager().getLeaderboard(skill, place, 1).get(0);
                return TextUtil.replace(Lang.getMessage(MenuMessage.SKILL_LEVEL, locale),
                        "{level}", String.valueOf(value.getLevel()));
        }
        return placeholder;
    }

    @Override
    public Set<Integer> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Set<Integer> places = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            places.add(i);
        }
        return places;
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, Integer place) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        List<SkillValue> values = plugin.getLeaderboardManager().getLeaderboard(skill, place, 1);
        if (values.size() == 0) {
            return null;
        }
        SkillValue skillValue = values.get(0);
        UUID id = skillValue.getId();
        if (baseItem.getItemMeta() instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) baseItem.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
            baseItem.setItemMeta(meta);
        }
        return baseItem;
    }
}
