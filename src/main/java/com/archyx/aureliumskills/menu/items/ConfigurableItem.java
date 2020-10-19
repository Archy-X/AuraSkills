package com.archyx.aureliumskills.menu.items;

import fr.minuskube.inv.content.SlotPos;
import org.bukkit.configuration.ConfigurationSection;

public interface ConfigurableItem {

    ItemType getType();

    void load(ConfigurationSection config);

    SlotPos getPos();

}
