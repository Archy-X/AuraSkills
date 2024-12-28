package dev.aurelium.auraskills.common.storage.backup;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserManager;
import dev.aurelium.auraskills.common.user.UserState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class BackupProvider {

    public final AuraSkillsPlugin plugin;
    public final UserManager playerManager;

    public BackupProvider(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getUserManager();
    }

    public void createBackupFolder() {
        File backupFolder = new File(plugin.getPluginFolder() + "/backups");
        if (!backupFolder.exists()) {
            if (!backupFolder.mkdir()) {
                plugin.logger().warn("Error creating backups folder!");
            }
        }
    }

    public File saveBackup(boolean savePlayerData) throws Exception {
        // Save online players
        if (savePlayerData) {
            for (User user : plugin.getUserManager().getOnlineUsers()) {
                plugin.getStorageProvider().saveSafely(user);
            }
        }

        createBackupFolder();
        LocalTime time = LocalTime.now();
        File backupFile = new File(plugin.getPluginFolder() + "/backups/backup-" + LocalDate.now()
                + "_" + time.getHour() + "-" + time.getMinute() + "-" + time.getSecond() + ".yml");

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(backupFile.toPath()).build();

        ConfigurationNode config = BasicConfigurationNode.root(); // Create empty node

        config.node("backup_version").set(2);

        List<UserState> states = plugin.getStorageProvider().loadStates(false, false);
        if (states.size() > plugin.configInt(Option.AUTOMATIC_BACKUPS_MAX_USERS)) {
            plugin.logger().info("Automatic backup saving was skipped due to too many users (" + states.size() + "), use your own backup system.");
            return null;
        }
        for (UserState state : states) {
            ConfigurationNode userNode = config.node("users", state.uuid().toString());
            // Save skill levels and xp
            for (Skill skill : state.skillLevels().keySet()) {
                int level = state.skillLevels().getOrDefault(skill, plugin.config().getStartLevel());
                double xp = state.skillXp().getOrDefault(skill, 0.0);

                ConfigurationNode skillNode = userNode.node("skills", skill.getId().toString());

                skillNode.node("level").set(level);
                skillNode.node("xp").set(xp);
            }
            // Save mana
            userNode.node("mana").set(state.mana());
            // Save stat modifiers
            for (StatModifier statModifier : state.statModifiers().values()) {
                ConfigurationNode modifierNode = userNode.node("stat_modifiers").appendListNode();
                modifierNode.node("name").set(statModifier.name());
                modifierNode.node("stat").set(statModifier.stat().getId().toString());
                modifierNode.node("value").set(statModifier.value());
            }
            // Save trait modifiers
            for (TraitModifier statModifier : state.traitModifiers().values()) {
                ConfigurationNode modifierNode = userNode.node("trait_modifiers").appendListNode();
                modifierNode.node("name").set(statModifier.name());
                modifierNode.node("trait").set(statModifier.trait().getId().toString());
                modifierNode.node("value").set(statModifier.value());
            }
        }
        // Save the backup file
        loader.save(config);
        return backupFile;
    }

    public void loadBackup(File file) throws Exception {
        ConfigurationNode root = YamlConfigurationLoader.builder().path(file.toPath()).build().load();

        int version = root.node("backup_version").getInt(0);

        if (version == 2) {
            loadV2(root);
        } else if (version == 1) {
            loadV1(root);
        } else {
            throw new IllegalStateException("Invalid backup_version");
        }
    }

    private void loadV2(ConfigurationNode config) throws Exception {
        for (ConfigurationNode userNode : config.node("users").childrenMap().values()) {
            UUID uuid = getFromKey(userNode);
            if (uuid == null) continue;

            Map<Skill, Integer> skillLevels = new HashMap<>();
            Map<Skill, Double> skillXp = new HashMap<>();

            for (ConfigurationNode skillNode : userNode.node("skills").childrenMap().values()) {
                loadSkillNode(skillNode, skillLevels, skillXp);
            }
            // Load modifiers
            Map<String, StatModifier> statModifiers = new HashMap<>();
            for (ConfigurationNode modifierNode : userNode.node("stat_modifiers").childrenList()) {
                String name = modifierNode.node("name").getString();

                String statName = modifierNode.node("stat").getString();
                if (statName == null) continue;
                Stat stat = plugin.getStatRegistry().get(NamespacedId.fromString(statName));

                double value = modifierNode.node("value").getDouble();

                StatModifier statModifier = new StatModifier(name, stat, value);
                statModifiers.put(name, statModifier);
            }
            Map<String, TraitModifier> traitModifiers = new HashMap<>();
            for (ConfigurationNode modifierNode : userNode.node("trait_modifiers").childrenList()) {
                String name = modifierNode.node("name").getString();

                String traitName = modifierNode.node("trait").getString();
                if (traitName == null) continue;
                Trait trait = plugin.getTraitRegistry().get(NamespacedId.fromString(traitName));

                double value = modifierNode.node("value").getDouble();

                TraitModifier traitModifier = new TraitModifier(name, trait, value);
                traitModifiers.put(name, traitModifier);
            }
            double mana = userNode.node("mana").getDouble();

            // Create user state
            UserState state = new UserState(uuid, skillLevels, skillXp, statModifiers, traitModifiers, mana);

            plugin.getStorageProvider().applyState(state); // Save the state
        }
    }

    private void loadV1(ConfigurationNode config) throws Exception {
        for (ConfigurationNode playerNode : config.node("player_data").childrenMap().values()) {
            UUID uuid = getFromKey(playerNode);
            if (uuid == null) continue;

            Map<Skill, Integer> skillLevels = new HashMap<>();
            Map<Skill, Double> skillXp = new HashMap<>();

            for (ConfigurationNode skillNode : playerNode.childrenMap().values()) {
                loadSkillNode(skillNode, skillLevels, skillXp);
            }
            // Create user state from level and xp maps with empty modifiers and mana
            UserState state = new UserState(uuid, skillLevels, skillXp, new HashMap<>(), new HashMap<>(), 0);

            plugin.getStorageProvider().applyState(state); // Save the state
        }
    }

    private void loadSkillNode(ConfigurationNode skillNode, Map<Skill, Integer> skillLevels, Map<Skill, Double> skillXp) {
        String skillName = (String) skillNode.key();
        if (skillName == null) return;

        Skill skill = plugin.getSkillRegistry().get(NamespacedId.fromDefault(skillName.toLowerCase(Locale.ROOT)));
        int level = skillNode.node("level").getInt();
        double xp = skillNode.node("xp").getDouble();

        skillLevels.put(skill, level);
        skillXp.put(skill, xp);
    }

    @Nullable
    private UUID getFromKey(ConfigurationNode node) {
        String key = (String) node.key();
        if (key == null) return null;

        return UUID.fromString(key);
    }

}
