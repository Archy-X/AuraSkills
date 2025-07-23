package dev.aurelium.auraskills.common.loot;

import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootValues;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import org.jetbrains.annotations.NotNull;

public class CommandLoot extends Loot {

    private final CommandExecutor executor;
    private final String[] commands;

    public CommandLoot(NamespacedId id, LootValues values, CommandExecutor executor, @NotNull String[] commands) {
        super(id, values);
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
