package dev.aurelium.auraskills.common.action;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Objects;

public record ChatAction(String text) implements UserAction {

    public static ChatAction parse(ConfigurationNode config) {
        String message = Objects.requireNonNull(config.node("text").getString());
        return new ChatAction(message);
    }

    @Override
    public void run(AuraSkillsPlugin plugin, User user, ActionContext context) {
        String formatted = context.replacePlaceholders(text);
        Component component = plugin.getPlatformUtil().toComponent(formatted);
        user.sendMessage(component);
    }
}
