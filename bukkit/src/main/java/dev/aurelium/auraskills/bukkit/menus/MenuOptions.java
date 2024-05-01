package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.item.ItemRegistryMenuProvider;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.slate.function.ItemMetaParser;
import dev.aurelium.slate.option.SlateOptions;
import dev.aurelium.slate.option.SlateOptionsBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MenuOptions {

    private final AuraSkills plugin;

    public MenuOptions(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public SlateOptions getBaseOptions() {
        return new SlateOptionsBuilder()
                .keyedItemProvider(new ItemRegistryMenuProvider(plugin.getItemRegistry()))
                .mainDirectory(new File(plugin.getDataFolder(), "menus"))
                .loreWrappingWidth(plugin.configInt(Option.MENUS_LORE_WRAPPING_WIDTH))
                .itemMetaParsers(getMetaParsers())
                .build();
    }

    public Map<String, ItemMetaParser> getMetaParsers() {
        Map<String, ItemMetaParser> parsers = new HashMap<>();

        parsers.put("potion_data", (item, config) -> {
            new ConfigurateItemParser(plugin).parsePotionData(item, config);
            return item;
        });

        parsers.put("nbt", (item, config) -> new ConfigurateItemParser(plugin).parseNBT(item, config));

        parsers.put("hide_tooltip", (item, config) -> {
            new ConfigurateItemParser(plugin).parseHideTooltip(config, item);
            return item;
        });

        return parsers;
    }

}
