package dev.aurelium.auraskills.common.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class EconomyHook extends Hook {

    public EconomyHook(AuraSkillsPlugin plugin, ConfigurationNode config) {
        super(plugin, config);
    }

    public abstract void deposit(User user, double amount);

}
