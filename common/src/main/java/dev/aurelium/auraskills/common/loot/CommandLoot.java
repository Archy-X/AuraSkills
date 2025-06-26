package dev.aurelium.auraskills.common.loot;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootValues;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandLoot extends Loot {

    private final CommandExecutor executor;
    private final String[] commands;

    public CommandLoot(LootValues values, CommandExecutor executor, @NotNull String[] commands, List<ConfigNode> requirements) {
        super(values, requirements);
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
