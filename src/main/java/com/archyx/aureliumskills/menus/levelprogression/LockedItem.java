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
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu, @NotNull PlaceholderType placeholderType, @NotNull Integer position) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        @Nullable Skill skill = (Skill) activeMenu.getProperty("skill");
        assert (null != skill);
        int level = getLevel(activeMenu, position);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "level_locked":
                m = TextUtil.replace(Lang.getMessage(MenuMessage.LEVEL_LOCKED, locale),"{level}", RomanNumber.toRoman(level));
                break;
            case "level_number":
                m = TextUtil.replace(Lang.getMessage(MenuMessage.LEVEL_NUMBER, locale), "{level}", String.valueOf(level));
                break;
            case "rewards":
                m = getRewardsLore(skill, level, player, locale);
                break;
            case "ability":
                m = getAbilityLore(skill, level, locale);
                break;
            case "mana_ability":
                m = getManaAbilityLore(skill, level, locale);
                break;
            case "locked":
                m = Lang.getMessage(MenuMessage.LOCKED, locale);
                break;
        }
        assert (null != m);
        return m;
    }

    @Override
    public @NotNull Set<@NotNull Integer> getDefinedContexts(@NotNull Player player, @NotNull ActiveMenu activeMenu) {
        Set<@NotNull Integer> levels = new HashSet<>();
        @Nullable PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null)
            return levels;
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int level = playerData.getSkillLevel(skill);
        int itemsPerPage = getItemsPerPage(activeMenu);
        int currentPage = activeMenu.getCurrentPage();
        for (int i = itemsPerPage - 1; i >= 0; i--) {
            if (1 + currentPage * itemsPerPage + i > level) {
                levels.add(2 + i);
            } else {
                break;
            }
        }
        return levels;
    }

}
