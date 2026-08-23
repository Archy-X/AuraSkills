package dev.aurelium.auraskills.common.action;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.DurationParser;
import org.spongepowered.configurate.ConfigurationNode;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public record ActionBarAction(String text, Duration duration) implements UserAction {

    public static ActionBarAction parse(ConfigurationNode config) {
        String message = Objects.requireNonNull(config.node("text").getString());
        String durationString = config.node("duration").getString("40");
        Duration parsed = DurationParser.parseOrTicks(durationString);
        return new ActionBarAction(message, parsed);
    }

    @Override
    public void run(AuraSkillsPlugin plugin, User user, ActionContext context) {
        String formatted = context.replacePlaceholders(text);

        plugin.getUiProvider().getActionBarManager().setPaused(user, (int) duration.toMillis(), TimeUnit.MILLISECONDS);
        plugin.getUiProvider().sendActionBar(user, plugin.getMessageProvider().applyFormatting(formatted));
    }
}
