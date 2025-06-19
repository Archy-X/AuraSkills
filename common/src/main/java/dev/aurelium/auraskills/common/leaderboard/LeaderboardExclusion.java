package dev.aurelium.auraskills.common.leaderboard;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class LeaderboardExclusion {

    private static final String FILE_PATH = ".metadata/leaderboard_metadata.yml";
    protected final AuraSkillsPlugin plugin;
    private final Set<UUID> excludedPlayers = new HashSet<>();

    public LeaderboardExclusion(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isExcludedPlayer(UUID uuid) {
        return excludedPlayers.contains(uuid);
    }

    public void addExcludedPlayer(UUID uuid) {
        excludedPlayers.add(uuid);
    }

    public void removeExcludedPlayer(UUID uuid) {
        excludedPlayers.remove(uuid);
    }

    public void loadFromFile() {
        File file = new File(plugin.getPluginFolder(), FILE_PATH);
        try {
            ConfigurationNode config = FileUtil.loadYamlFile(file);

            List<String> excluded = config.node("excluded_players").getList(String.class, new ArrayList<>());

            this.excludedPlayers.clear();
            this.excludedPlayers.addAll(excluded.stream().map(UUID::fromString).toList());
        } catch (IOException e) {
            plugin.logger().warn("Error loading " + FILE_PATH);
            e.printStackTrace();
        }
    }

    public void saveToFile() {
        File file = new File(plugin.getPluginFolder(), FILE_PATH);
        try {
            ConfigurationNode config = FileUtil.loadYamlFile(file);

            if (excludedPlayers.isEmpty() && config.node("excluded_players").getList(String.class, new ArrayList<>()).isEmpty()) {
                return;
            }

            config.node("excluded_players").set(excludedPlayers.stream().map(UUID::toString).toList());

            FileUtil.saveYamlFile(file, config);
        } catch (IOException e) {
            plugin.logger().warn("Error saving " + FILE_PATH);
            e.printStackTrace();
        }
    }

}
