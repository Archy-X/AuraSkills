package dev.aurelium.auraskills.bukkit.loot.type;

import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.context.LootContext;
import dev.aurelium.auraskills.common.commands.CommandExecutor;

import java.util.Map;
import java.util.Set;

public class CommandLoot extends Loot {

    private final CommandExecutor executor;
    private final String command;

    public CommandLoot(int weight, String message, Map<String, Set<LootContext>> contexts, Map<String, Object> options,
                       CommandExecutor executor, String command) {
        super(weight, message, contexts, options);
        this.executor = executor;
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

}
