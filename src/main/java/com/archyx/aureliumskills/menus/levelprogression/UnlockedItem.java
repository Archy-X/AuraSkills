package com.archyx.aureliumskills.menus.levelprogression;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Set;

public class UnlockedItem extends SkillLevelItem implements TemplateItemProvider<Integer> {

    public UnlockedItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Integer> getContext() {
        return Integer.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderType placeholderType, Integer position) {
        Locale locale = plugin.getLang().getLocale(player);
        Skill skill = (Skill) activeMenu.getProperty("skill");
        int level = getLevel(activeMenu, position);
        switch (placeholder) {
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
        return getCurrentPageLevels(activeMenu);
    }

}
