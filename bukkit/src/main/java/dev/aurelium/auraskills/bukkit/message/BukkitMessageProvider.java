package dev.aurelium.auraskills.bukkit.message;

import com.archyx.slate.text.TextFormatter;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.message.MessageProvider;
import net.kyori.adventure.text.Component;

public class BukkitMessageProvider extends MessageProvider {

    private final TextFormatter textFormatter = new TextFormatter();

    public BukkitMessageProvider(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String applyFormatting(String message) {
        return textFormatter.toString(textFormatter.toComponent(message));
    }

    @Override
    public String componentToString(Component component) {
        return textFormatter.toString(component);
    }

    @Override
    public Component stringToComponent(String message) {
        return textFormatter.toComponent(message);
    }


}
