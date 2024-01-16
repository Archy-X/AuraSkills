package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.LootManager;
import dev.aurelium.auraskills.bukkit.loot.builder.CommandLootBuilder;
import dev.aurelium.auraskills.common.commands.CommandExecutor;

import java.util.Locale;
import java.util.Map;

public class CommandLootParser extends LootParser {

    public CommandLootParser(LootManager manager) {
        super(manager);
    }

    @Override
    public Loot parse(Map<?, ?> map) {
        CommandLootBuilder builder = new CommandLootBuilder();

        if (map.containsKey("executor")) {
            builder.executor(CommandExecutor.valueOf(getString(map, "executor").toUpperCase(Locale.ROOT)));
        }

        return builder.command(getString(map, "command"))
                .message(parseMessage(map))
                .weight(parseWeight(map))
                .contexts(parseContexts(map))
                .options(parseOptions(map))
                .build();
    }
}
