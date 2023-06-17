package dev.aurelium.auraskills.common.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.data.PlayerData;

public abstract class EconomyHook extends Hook {

    public EconomyHook(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    public abstract void deposit(PlayerData playerData, double amount);

}
