package dev.aurelium.auraskills.common.action;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.DurationParser;
import org.spongepowered.configurate.ConfigurationNode;

import java.time.Duration;

public record TitleAction(
        String title,
        String subtitle,
        Duration fadeIn,
        Duration stay,
        Duration fadeOut) implements UserAction {

    public static TitleAction parse(ConfigurationNode config) {
        String title = config.node("title").getString("");
        String subtitle = config.node("subtitle").getString("");
        Duration fadeIn = DurationParser.parseOrTicks(config.node("fade_in").getString("5"));
        Duration stay = DurationParser.parseOrTicks(config.node("stay").getString("40"));
        Duration fadeOut = DurationParser.parseOrTicks(config.node("fade_out").getString("5"));
        return new TitleAction(title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void run(AuraSkillsPlugin plugin, User user, ActionContext context) {
        plugin.getUiProvider().sendTitle(user, title, subtitle, (int) fadeIn.toMillis() / 50, (int) stay.toMillis() / 50, (int) fadeOut.toMillis() / 50);
    }
}
