package dev.aurelium.auraskills.bukkit.stat;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.player.User;
import dev.aurelium.auraskills.common.stat.StatManager;

public class BukkitStatManager extends StatManager {

    public BukkitStatManager(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void reloadPlayer(User user) {
        // TODO Implement
    }

    @Override
    public <T> void reload(User user, T type) {

    }

    @Override
    public void reloadStat(User user, Stat stat) {

    }
}
