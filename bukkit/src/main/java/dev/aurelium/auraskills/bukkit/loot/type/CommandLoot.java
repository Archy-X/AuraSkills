package dev.aurelium.auraskills.bukkit.loot.type;

import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootValues;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import org.jetbrains.annotations.NotNull;

public class CommandLoot extends Loot {

    private final CommandExecutor executor;
    private final String[] commands;

    public CommandLoot(LootValues values, CommandExecutor executor, @NotNull String[] commands) {
        super(values);
        this.executor = executor;
        this.commands = commands;
    }

    public String[] getCommands() {
        return commands;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

}
