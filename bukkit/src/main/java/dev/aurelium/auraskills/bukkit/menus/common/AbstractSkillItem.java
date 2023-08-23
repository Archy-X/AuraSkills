package dev.aurelium.auraskills.bukkit.menus.common;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.Locale;

public abstract class AbstractSkillItem extends AbstractItem implements TemplateItemProvider<Skill> {

    public AbstractSkillItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Skill> getContext() {
        return Skill.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Skill skill) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        int skillLevel = user.getSkillLevel(skill);

        switch (placeholder) {
            case "skill" -> {
                return skill.getDisplayName(locale);
            }
            case "desc" -> {
                return skill.getDescription(locale);
            }
            case "level" -> {
                if (data.getType() == PlaceholderType.DISPLAY_NAME) {
                    return RomanNumber.toRoman(skillLevel, plugin);
                } else {
                    return TextUtil.replace(plugin.getMsg(MenuMessage.LEVEL, locale), "{level}", RomanNumber.toRoman(skillLevel, plugin));
                }
            }
            case "skill_click" -> {
                return plugin.getMsg(MenuMessage.SKILL_CLICK, locale);
            }
        }
        return replaceMenuMessage(placeholder, player, activeMenu);
    }

}
