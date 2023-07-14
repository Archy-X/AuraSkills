package dev.aurelium.auraskills.common.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;

public abstract class EconomyHook extends Hook {

    public EconomyHook(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    public abstract void deposit(User user, double amount);

}
