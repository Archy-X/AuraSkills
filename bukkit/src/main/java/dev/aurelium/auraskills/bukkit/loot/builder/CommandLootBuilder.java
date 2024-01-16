package dev.aurelium.auraskills.bukkit.loot.builder;

import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.type.CommandLoot;
import dev.aurelium.auraskills.common.commands.CommandExecutor;

public class CommandLootBuilder extends LootBuilder {

    private CommandExecutor executor;
    private String command;

    public CommandLootBuilder() {
        this.executor = CommandExecutor.CONSOLE;
    }

    @Override
    public Loot build() {
        return new CommandLoot(weight, message, contexts, options, executor, command);
    }

    public CommandLootBuilder executor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public CommandLootBuilder command(String command) {
        this.command = command;
        return this;
    }

}
