package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.LootManager;
import dev.aurelium.auraskills.bukkit.loot.builder.CommandLootBuilder;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import dev.aurelium.auraskills.common.util.data.Validate;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Locale;

public class CommandLootParser extends LootParser {

    public CommandLootParser(LootManager manager) {
        super(manager);
    }

    @Override
    public Loot parse(ConfigurationNode config) throws SerializationException {
        CommandLootBuilder builder = new CommandLootBuilder();

        String executor = config.node("executor").getString("console");
        builder.executor(CommandExecutor.valueOf(executor.toUpperCase(Locale.ROOT)));

        String command = config.node("command").getString("");
        Validate.notNull(command, "Command loot must specify key command");

        return builder.command(command)
                .message(parseMessage(config))
                .weight(parseWeight(config))
                .contexts(parseContexts(config))
                .options(parseOptions(config))
                .build();
    }
}
