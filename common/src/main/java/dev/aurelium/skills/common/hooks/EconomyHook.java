package dev.aurelium.skills.common.hooks;

import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.data.PlayerData;

public abstract class EconomyHook extends Hook {

    public EconomyHook(AureliumSkillsPlugin plugin) {
        super(plugin);
    }

    public abstract void deposit(PlayerData playerData, double amount);

}
