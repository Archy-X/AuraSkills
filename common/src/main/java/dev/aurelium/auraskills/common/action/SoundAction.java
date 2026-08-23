package dev.aurelium.auraskills.common.action;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import org.spongepowered.configurate.ConfigurationNode;

public record SoundAction(String sound, String category, float volume, float pitch) implements UserAction {

    public static SoundAction parse(ConfigurationNode config) {
        String sound = config.node("sound").getString();
        String category = config.node("category").getString("master");
        float volume = config.node("volume").getFloat(1.0f);
        float pitch = config.node("pitch").getFloat(1.0f);
        return new SoundAction(sound, category, volume, pitch);
    }

    @Override
    public void run(AuraSkillsPlugin plugin, User user, ActionContext context) {
        user.playSound(sound, category, volume, pitch);
    }
}
