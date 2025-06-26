package dev.aurelium.auraskills.bukkit.loot;

import dev.aurelium.auraskills.api.loot.LootParser;
import dev.aurelium.auraskills.bukkit.loot.parser.EntityLootParser;
import dev.aurelium.auraskills.bukkit.loot.parser.ItemLootParser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.loot.CommandLootParser;
import dev.aurelium.auraskills.common.loot.LootLoader;
import dev.aurelium.auraskills.common.loot.LootType;

public class BukkitLootLoader extends LootLoader {

    private final BukkitLootManager manager;

    public BukkitLootLoader(AuraSkillsPlugin plugin, BukkitLootManager manager) {
        super(plugin, manager);
        this.manager = manager;
    }

    @Override
    public LootParser getParser(LootType type) {
        return switch (type) {
            case ITEM -> new ItemLootParser(manager);
            case COMMAND -> new CommandLootParser();
            case ENTITY -> new EntityLootParser(manager);
        };
    }

}
