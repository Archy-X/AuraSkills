package dev.auramc.auraskills.common.hooks;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;

public abstract class EconomyHook extends Hook {

    public EconomyHook(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    public abstract void deposit(PlayerData playerData, double amount);

}
