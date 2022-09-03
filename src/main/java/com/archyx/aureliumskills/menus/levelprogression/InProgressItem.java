package com.archyx.aureliumskills.menus.levelprogression;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.math.NumberUtil;
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

public class InProgressItem extends SkillLevelItem {

    public InProgressItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu activeMenu, @NotNull PlaceholderType placeholderType, @NotNull Integer position) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        @Nullable Skill skill = (Skill) activeMenu.getProperty("skill");
        assert (null != skill);
        @Nullable PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null)
            return placeholder;
        int level = getLevel(activeMenu, position);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "level_in_progress":
                m = TextUtil.replace(Lang.getMessage(MenuMessage.LEVEL_IN_PROGRESS, locale),"{level}", RomanNumber.toRoman(level));
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
            case "progress":
                double currentXp = playerData.getSkillXp(skill);
                double xpToNext = plugin.getLeveler().getXpRequirements().getXpRequired(skill, level);
                m = TextUtil.replace(Lang.getMessage(MenuMessage.PROGRESS, locale)
                        ,"{percent}", NumberUtil.format2(currentXp / xpToNext * 100)
                        ,"{current_xp}", NumberUtil.format2(currentXp)
                        ,"{level_xp}", String.valueOf((int) xpToNext));
                break;
            case "in_progress":
                m = Lang.getMessage(MenuMessage.IN_PROGRESS, locale);
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
        @Nullable Skill skill = (Skill) activeMenu.getProperty("skill");
        assert (null != skill);
        int itemsPerPage = getItemsPerPage(activeMenu);
        int currentPage = activeMenu.getCurrentPage();
        int level = playerData.getSkillLevel(skill);
        if (level >= 1 + currentPage * itemsPerPage && level < (currentPage + 1) * itemsPerPage + 2) {
            int position = (level + 1) % itemsPerPage; // Calculate the first-page equivalent next level
            if (position == 0) { // Account for next skill level 24
                position = 24;
            } else if (position == 1) { // Account for next skill level 25
                position = 25;
            }
            levels.add(position);
        }
        return levels;
    }

}
