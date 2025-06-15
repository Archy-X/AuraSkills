package dev.aurelium.auraskills.bukkit.loot;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.context.MobContextProvider;
import dev.aurelium.auraskills.bukkit.loot.entity.VanillaEntityParser;
import dev.aurelium.auraskills.bukkit.loot.parser.CustomEntityParser;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.loot.CustomItemParser;
import dev.aurelium.auraskills.common.loot.LootLoader;
import dev.aurelium.auraskills.common.loot.LootManager;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BukkitLootManager extends LootManager {

    private final AuraSkills plugin;
    private final LootLoader lootLoader;
    private final List<CustomItemParser> customItemParsers = new ArrayList<>();
    private final List<CustomEntityParser> customEntityParsers = new ArrayList<>();

    public BukkitLootManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
        this.lootLoader = new BukkitLootLoader(plugin, this);

        registerContextProvider(new MobContextProvider());
        registerCustomItemParser(new ItemKeyParser(plugin));
        registerCustomEntityParser(new VanillaEntityParser(plugin));
    }

    @Override
    public AuraSkills getPlugin() {
        return plugin;
    }

    public void loadLootTables() {
        lootLoader.loadLootTables();
    }

    public boolean toInventory(ItemStack held) {
        if (ItemUtils.hasTelekinesis(held)) {
            return true;
        }
        return plugin.configBoolean(Option.LOOT_DIRECTLY_TO_INVENTORY);
    }

    public List<CustomItemParser> getCustomItemParsers() {
        return customItemParsers;
    }

    public List<CustomEntityParser> getCustomEntityParsers() {
        return customEntityParsers;
    }

    public void registerCustomItemParser(CustomItemParser customItemParser) {
        customItemParsers.add(customItemParser);
    }

    public void registerCustomEntityParser(CustomEntityParser customEntityParser) {
        customEntityParsers.add(customEntityParser);
    }

}
