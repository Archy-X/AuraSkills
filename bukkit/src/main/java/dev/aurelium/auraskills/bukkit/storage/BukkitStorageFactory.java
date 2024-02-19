package dev.aurelium.auraskills.bukkit.storage;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.storage.StorageFactory;

public class BukkitStorageFactory extends StorageFactory {

    public BukkitStorageFactory(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getDataDirectory() {
        return plugin.getPluginFolder().getPath() + "/userdata";
    }
}
