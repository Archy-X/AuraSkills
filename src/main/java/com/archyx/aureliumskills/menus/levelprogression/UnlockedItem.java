package com.archyx.aureliumskills.menus.levelprogression;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.math.RomanNumber;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class UnlockedItem extends SkillLevelItem {

    public UnlockedItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Integer position) {
        Locale locale = plugin.getLang().getLocale(player);
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int level = getLevel(activeMenu, position);
        switch (placeholder) {
            case "level_unlocked":
                return TextUtil.replace(Lang.getMessage(MenuMessage.LEVEL_UNLOCKED, locale),"{level}", RomanNumber.toRoman(level));
            case "level_number":
                return TextUtil.replace(Lang.getMessage(MenuMessage.LEVEL_NUMBER, locale), "{level}", String.valueOf(level));
            case "rewards":
                return getRewardsLore(skill, level, player, locale);
            case "ability":
                return getAbilityLore(skill, level, locale);
            case "mana_ability":
                return getManaAbilityLore(skill, level, locale);
            case "unlocked":
                return Lang.getMessage(MenuMessage.UNLOCKED, locale);
        }
        return placeholder;
    }

    @Override
    public Set<Integer> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            Skill skill = (Skill) activeMenu.getProperty("skill");
            int level = playerData.getSkillLevel(skill);
            int itemsPerPage = getItemsPerPage(activeMenu);
            int currentPage = activeMenu.getCurrentPage();
            Set<Integer> levels = new HashSet<>();
            for (int i = 0; i < itemsPerPage; i++) {
                if (2 + currentPage * itemsPerPage + i <= level) {
                    levels.add(2 + i);
                } else {
                    break;
                }
            }
            return levels;
        }
        return new HashSet<>();
    }

}
