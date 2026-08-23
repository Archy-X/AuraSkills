package dev.aurelium.auraskills.common.action;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Locale;

public interface UserAction {

    static UserAction parse(ConfigurationNode config) throws SerializationException {
        return switch (config.node("type").getString("").toLowerCase(Locale.ROOT)) {
            case "action_bar" -> ActionBarAction.parse(config);
            case "chat" -> ChatAction.parse(config);
            case "command" -> CommandAction.parse(config);
            case "money" -> MoneyAction.parse(config);
            case "title" -> TitleAction.parse(config);
            case "sound" -> SoundAction.parse(config);
            default -> null;
        };
    }

    void run(AuraSkillsPlugin plugin, User user, ActionContext context);
}
