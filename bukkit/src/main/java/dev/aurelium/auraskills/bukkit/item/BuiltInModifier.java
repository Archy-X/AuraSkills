package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;

import java.util.Locale;

public interface BuiltInModifier {

    String name();

    default String getAbilityName(AuraSkills plugin, Locale locale) {
        return plugin.getMsg(MessageKey.of("abilities." + name().toLowerCase(Locale.ROOT) + ".name"), locale);
    }

    default String getDisplayName(AuraSkills plugin, Locale locale) {
        return plugin.getMsg(MenuMessage.ABILITY_MODIFIER_NAME, locale).replace("{name}", getAbilityName(plugin, locale));
    }

    default String getDescriptionMessage(AuraSkills plugin, User user) {
        Locale locale = user.getLocale();
        try {
            Ability ability = Abilities.valueOf(this.name());

            int level = user.getAbilityLevel(ability);
            String desc = plugin.getAbilityManager().getBaseDescription(ability, user, false);
            return TextUtil.replace(desc,
                    "{value}", NumberUtil.format2(ability.getValue(level)),
                    "{value_2}", NumberUtil.format2(ability.getSecondaryValue(level)),
                    "{chance_value}", plugin.getAbilityManager().getChanceValue(ability, level),
                    "{guaranteed_value}", plugin.getAbilityManager().getGuaranteedValue(ability, level));

        } catch (IllegalArgumentException e) {
            return plugin.getMsg(MenuMessage.ABILITY_MODIFIER_DESC, locale).replace("{ability}", getAbilityName(plugin, locale));
        }
    }

}
