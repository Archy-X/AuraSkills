package dev.aurelium.auraskills.bukkit.loot.parser;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootParser;
import dev.aurelium.auraskills.api.loot.LootParsingContext;
import dev.aurelium.auraskills.bukkit.loot.type.CommandLoot;
import dev.aurelium.auraskills.common.commands.CommandExecutor;
import dev.aurelium.auraskills.common.util.data.Validate;

import java.util.ArrayList;
import java.util.Locale;

public class CommandLootParser implements LootParser {

    @Override
    public Loot parse(LootParsingContext context, ConfigNode config) {
        String executorName = config.node("executor").getString("console");
        CommandExecutor executor = CommandExecutor.valueOf(executorName.toUpperCase(Locale.ROOT));

        String[] commands;
        if (!config.node("commands").empty()) {
            commands = config.node("commands")
                    .getList(String.class, new ArrayList<>())
                    .toArray(new String[0]);
        } else {
            String command = config.node("command").getString("");
            Validate.notNull(command, "Command loot must specify key command");
            commands = new String[] {command};
        }

        return new CommandLoot(context.parseValues(config), executor, commands);
    }
}
