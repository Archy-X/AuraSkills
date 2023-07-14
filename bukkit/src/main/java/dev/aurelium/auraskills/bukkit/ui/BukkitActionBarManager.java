package dev.aurelium.auraskills.bukkit.ui;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ui.ActionBarManager;
import dev.aurelium.auraskills.common.user.User;

public class BukkitActionBarManager extends ActionBarManager {

    public BukkitActionBarManager(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getHp(User user) {
        return null;
    }

    @Override
    public String getMaxHp(User user) {
        return null;
    }
}
