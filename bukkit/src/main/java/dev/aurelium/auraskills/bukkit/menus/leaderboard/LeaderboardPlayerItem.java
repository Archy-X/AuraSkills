package dev.aurelium.auraskills.bukkit.menus.leaderboard;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.common.leaderboard.SkillValue;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class LeaderboardPlayerItem extends AbstractItem implements TemplateItemProvider<Integer> {

    public LeaderboardPlayerItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Integer> getContext() {
        return Integer.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Integer place) {
        Locale locale = plugin.getUser(player).getLocale();
        Skill skill = (Skill) activeMenu.getProperty("skill");
        SkillValue value = plugin.getLeaderboardManager().getLeaderboard(skill, place, 1).get(0);
        switch (placeholder) {
            case "player_entry":
                UUID id = value.id();
                String name = Bukkit.getOfflinePlayer(id).getName();
                return TextUtil.replace(plugin.getMsg(MenuMessage.PLAYER_ENTRY, locale),
                        "{place}", String.valueOf(place),
                        "{player}", name != null ? name : "?");
            case "skill_level":
                return TextUtil.replace(plugin.getMsg(MenuMessage.SKILL_LEVEL, locale),
                        "{level}", String.valueOf(value.level()));
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
        UUID id = skillValue.id();
        if (baseItem.getItemMeta() instanceof SkullMeta meta) {
            // Set the player skin on the head
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(id));
            baseItem.setItemMeta(meta);
        }
        return baseItem;
    }
}
