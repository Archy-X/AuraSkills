package dev.aurelium.auraskills.bukkit.loot.parser;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface CustomItemParser {

    boolean shouldUseParser(Map<?, ?> map);

    ItemStack parseCustomItem(Map<?, ?> map);

}
