package dev.aurelium.auraskills.common.migration;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUserMigrator {

    private final AuraSkillsPlugin plugin;

    FileUserMigrator(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public void migrate() {
        File playerDataDir = new File(plugin.getPluginFolder().getParentFile(), "AureliumSkills/playerdata");
        if (!playerDataDir.exists() || !playerDataDir.isDirectory()) return;

        File userDataDir = new File(plugin.getPluginFolder(), "userdata");

        File[] files = playerDataDir.listFiles();
        if (files == null || files.length == 0) return;

        plugin.logger().warn("[Migrator] Found AureliumSkills/playerdata files, attempting to migrate to AuraSkills/userdata");
        try {
            int migrated = 0;
            for (File playerDataFile : files) {
                if (!playerDataFile.getName().endsWith(".yml")) continue;

                // File in new userdata dir with same name as player data file
                File userFile = new File(userDataDir, playerDataFile.getName());

                // Copy old file to new file
                Files.createDirectories(userFile.getParentFile().toPath());
                Files.copy(playerDataFile.toPath(), userFile.toPath());

                if (!userFile.exists()) continue;

                ConfigurationNode config = FileUtil.loadYamlFile(userFile);

                applyFormatChanges(config);

                FileUtil.saveYamlFile(userFile, config);
                migrated++;
            }
            plugin.logger().warn("[Migrator] Migrated " + migrated + " files from AureliumSkills/playerdata to AuraSkills/userdata");
        } catch (IOException e) {
            plugin.logger().severe("[Migrator] Failed to migrate files from AureliumSkills/playerdata to AuraSkills/userdata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyFormatChanges(ConfigurationNode config) throws SerializationException {
        final String PLUGIN_NAME = "auraskills";
        // Add namespace to skills
        ConfigurationNode skillsNode = config.node("skills");
        for (ConfigurationNode oldNode : skillsNode.childrenMap().values()) {
            String oldKey = (String) oldNode.key();
            if (oldKey != null) {
                skillsNode.node(PLUGIN_NAME + "/" + oldKey).set(oldNode.raw());
                skillsNode.removeChild(oldKey);
            }
        }
        // Add namespaces to stat modifiers
        ConfigurationNode statModifiersNode = config.node("stat_modifiers");
        for (ConfigurationNode modifierNode : statModifiersNode.childrenMap().values()) {
            String bareStatName = modifierNode.node("stat").getString();
            modifierNode.node("stat").set(PLUGIN_NAME + "/" + bareStatName);
        }
        // Add namespace to ability data
        ConfigurationNode abilityDataNode = config.node("ability_data");
        for (ConfigurationNode oldNode : abilityDataNode.childrenMap().values()) {
            String oldKey = (String) oldNode.key();
            if (oldKey != null) {
                abilityDataNode.node(PLUGIN_NAME + "/" + oldKey).set(oldNode.raw());
                abilityDataNode.removeChild(oldKey);
            }
        }
    }

}
