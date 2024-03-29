package dev.aurelium.auraskills.bukkit.menus.common;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.message.MessageProvider;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.serialize.SerializationException;

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
        MessageProvider msg = plugin.getMessageProvider();

        switch (placeholder) {
            case "skill" -> {
                return msg.getRaw(msg.getSkillDisplayNameKey(skill), locale);
            }
            case "desc" -> {
                return msg.getRaw(msg.getSkillDescriptionKey(skill), locale);
            }
            case "level" -> {
                return RomanNumber.toRoman(skillLevel, plugin);
            }
            case "skill_click" -> {
                return plugin.getMsg(MenuMessage.SKILL_CLICK, locale);
            }
        }
        return replaceMenuMessage(placeholder, player, activeMenu);
    }

    protected ItemStack modifyItem(Skill skill, ItemStack baseItem) {
        if (!skill.isEnabled()) {
            return null;
        }
        if (skill instanceof CustomSkill customSkill) {
            try {
                ConfigurateItemParser parser = new ConfigurateItemParser(plugin);

                return parser.parseBaseItem(parser.parseItemContext(customSkill.getDefined().getItem()));
            } catch (SerializationException | IllegalArgumentException e) {
                plugin.logger().warn("Error parsing ItemContext of CustomSkill " + customSkill.getId());
                e.printStackTrace();
            }
        }
        return baseItem;
    }
}
