package com.archyx.aureliumskills.menus.sources;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Set;

public class SourceItem extends AbstractItem implements TemplateItemProvider<Source> {

    public SourceItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Source> getContext() {
        return Source.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderType placeholderType, Source source) {
        Locale locale = plugin.getLang().getLocale(player);
        switch (placeholder) {
            case "source_name":
                return TextUtil.replace(Lang.getMessage(MenuMessage.SOURCE_NAME, locale),
                        "{name}", StringUtils.substringAfterLast(source.getPath(), "."));
            case "source_xp":
                return TextUtil.replace(Lang.getMessage(MenuMessage.SOURCE_XP, locale),
                        "{xp}", String.valueOf(plugin.getSourceManager().getXp(source)));
            case "multiplied_xp":
                Skill skill = (Skill) activeMenu.getProperty("skill");
                double multiplier = plugin.getLeveler().getMultiplier(player, skill);
                if (multiplier > 1.0) {
                    return TextUtil.replace(Lang.getMessage(MenuMessage.MULTIPLIED_XP, locale),
                            "{xp}", NumberUtil.format1(plugin.getSourceManager().getXp(source) * multiplier));
                } else {
                    return "";
                }
            case "multiplied_desc":
                return Lang.getMessage(MenuMessage.MULTIPLIED_DESC, locale);
        }
        return placeholder;
    }

    @Override
    public Set<Source> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        return null;
    }
}
