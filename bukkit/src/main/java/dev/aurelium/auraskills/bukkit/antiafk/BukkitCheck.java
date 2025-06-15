package dev.aurelium.auraskills.bukkit.antiafk;

import dev.aurelium.auraskills.common.antiafk.AntiAfkManager;
import dev.aurelium.auraskills.common.antiafk.Check;
import dev.aurelium.auraskills.common.antiafk.CheckData;
import dev.aurelium.auraskills.common.antiafk.CheckType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static dev.aurelium.auraskills.bukkit.ref.BukkitPlayerRef.wrap;

public class BukkitCheck extends Check implements Listener {

    public BukkitCheck(CheckType type, AntiAfkManager manager) {
        super(type, manager);
    }

    protected CheckData getCheckData(Player player) {
        return getCheckData(wrap(player));
    }

    protected void logFail(Player player) {
        logFail(wrap(player));
    }

}
