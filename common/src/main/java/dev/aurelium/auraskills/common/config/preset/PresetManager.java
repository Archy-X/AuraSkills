package dev.aurelium.auraskills.common.config.preset;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PresetManager {

    private final AuraSkillsPlugin plugin;

    public PresetManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public ConfigPreset preparePreset(String name) throws PresetLoadException {
        if (!name.endsWith(".zip")) {
            throw new PresetLoadException("File name must end in .zip");
        }
        File zipFile = new File(plugin.getPluginFolder(), "presets/" + name);
        if (!zipFile.exists()) {
            throw new PresetLoadException("Preset file does not exist in the presets folder");
        }
        try {
            ConfigurationNode presetConfig = getPresetConfig(zipFile);
            List<PresetEntry> entries = parsePresetEntries(presetConfig);

            return new ConfigPreset(name, zipFile, entries);
        } catch (IOException e) {
            throw new PresetLoadException(e.getMessage());
        }
    }

    public PresetLoadResult loadPreset(ConfigPreset preset) throws IOException {
        // Logging the actions and names of file changes
        List<String> created = new ArrayList<>();
        List<String> modified = new ArrayList<>();
        List<String> replaced = new ArrayList<>();
        List<String> deleted = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(preset.zipFile(), ZipFile.OPEN_READ)) {
            for (PresetEntry presetEntry : preset.entries()) {
                int result = loadPresetEntry(presetEntry, zipFile);
                // Log result with file name
                switch (result) {
                    case 0 -> created.add(presetEntry.name());
                    case 1 -> modified.add(presetEntry.name());
                    case 2 -> replaced.add(presetEntry.name());
                    case 3 -> skipped.add(presetEntry.name());
                }
            }
        }
        return new PresetLoadResult(created, modified, replaced, deleted, skipped);
    }

    private int loadPresetEntry(PresetEntry presetEntry, ZipFile zipFile) throws IOException {
        String path = "files/" + presetEntry.name();
        ZipEntry zipEntry = zipFile.getEntry(path);

        if (zipEntry == null) {
            return 3; // Skipped
        }

        InputStream inputStream = zipFile.getInputStream(zipEntry);
        // Get the equivalent file of the currently active config
        File existingFile = new File(plugin.getPluginFolder(), presetEntry.name());
        // File does not existing, we can just save and ignore the PresetAction
        if (!existingFile.exists()) {
            // Create the new file, creating new directories if needed
            Files.createDirectories(existingFile.toPath().getParent());
            Files.copy(inputStream, existingFile.toPath());
            return 0; // Added
        } else if (presetEntry.action() == PresetAction.REPLACE) {
            Files.copy(inputStream, existingFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return 2; // Replaced
        } else {
            ConfigurationNode presetConfig = loadInputStream(inputStream);
            ConfigurationNode existingConfig = FileUtil.loadYamlFile(existingFile);

            ConfigurateLoader loader = new ConfigurateLoader(plugin, TypeSerializerCollection.builder().build());
            ConfigurationNode merged;
            if (presetEntry.action() == PresetAction.MERGE) {
                // Preset overrides existing
                merged = loader.mergeNodes(existingConfig, presetConfig);
            } else if (presetEntry.action() == PresetAction.APPEND) {
                // Preset does not override
                merged = loader.mergeNodes(presetConfig, existingConfig);
            } else {
                return 3; // Skipped
            }

            FileUtil.saveYamlFile(existingFile, merged);
            return 1; // Modified
        }
    }

    private ConfigurationNode getPresetConfig(File file) throws IOException {
        try (ZipFile zipFile = new ZipFile(file, ZipFile.OPEN_READ)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                // Search until the preset.yml file is found in the root level of the zip
                if (zipEntry.isDirectory() || !zipEntry.getName().equals("preset.yml")) {
                    continue;
                }
                try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
                    return loadInputStream(inputStream);
                }
            }
            // If this is reached, no preset.yml file was found
            throw new PresetLoadException("No preset.yml configuration file was found in the zip");
        }
    }

    public List<PresetEntry> parsePresetEntries(ConfigurationNode root) {
        int fileVersion = root.node("file_version").getInt();
        if (fileVersion != 1) {
            throw new PresetLoadException("Preset has an incompatible file_version");
        }
        List<PresetEntry> entries = new ArrayList<>();
        for (ConfigurationNode fileConfig : root.node("files").childrenList()) {
            String path = fileConfig.node("path").getString();
            if (path == null) continue;

            PresetAction action = PresetAction.valueOf(fileConfig.node("action").getString("replace").toUpperCase(Locale.ROOT));
            entries.add(new PresetEntry(path, action));
        }
        return entries;
    }

    private ConfigurationNode loadInputStream(InputStream inputStream) throws ConfigurateException {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .source(() -> new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
                .nodeStyle(NodeStyle.BLOCK)
                .build();

        return loader.load();
    }

}
