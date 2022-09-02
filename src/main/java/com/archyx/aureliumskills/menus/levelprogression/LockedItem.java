package com.archyx.aureliumskills.menus.levelprogression;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.math.RomanNumber;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LockedItem extends SkillLevelItem {

    public LockedItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public @Nullable String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu, PlaceholderType placeholderType, Integer position) {
        Locale locale = plugin.getLang().getLocale(player);
        Object property = activeMenu.getProperty("skill");
        assert (null != property);
        Skill skill = (Skill) property;
        int level = getLevel(activeMenu, position);
        switch (placeholder) {
            case "level_locked":
                return TextUtil.replace(Lang.getMessage(MenuMessage.LEVEL_LOCKED, locale),"{level}", RomanNumber.toRoman(level));
            case "level_number":
                return TextUtil.replace(Lang.getMessage(MenuMessage.LEVEL_NUMBER, locale), "{level}", String.valueOf(level));
            case "rewards":
                return getRewardsLore(skill, level, player, locale);
            case "ability":
                return getAbilityLore(skill, level, locale);
            case "mana_ability":
                return getManaAbilityLore(skill, level, locale);
            case "locked":
                return Lang.getMessage(MenuMessage.LOCKED, locale);
        }
        return placeholder;
    }

    @Override
    public @NotNull Set<Integer> getDefinedContexts(@NotNull Player player, @NotNull ActiveMenu activeMenu) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            Skill skill = (Skill) activeMenu.getProperty("skill");
            int level = playerData.getSkillLevel(skill);
            int itemsPerPage = getItemsPerPage(activeMenu);
            int currentPage = activeMenu.getCurrentPage();
            Set<Integer> levels = new HashSet<>();
            for (int i = itemsPerPage - 1; i >= 0; i--) {
                if (1 + currentPage * itemsPerPage + i > level) {
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
