package dev.aurelium.auraskills.common.storage.backup;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier.Operation;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

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

    public CompletableFuture<File> saveBackupAsync(boolean savePlayerData) {
        CompletableFuture<File> future = new CompletableFuture<>();

        plugin.getScheduler().executeAsync(() -> {
            try {
                File file = saveBackupSync(savePlayerData);
                future.complete(file);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return future;
    }

    public File saveBackupSync(boolean savePlayerData) throws Exception {
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

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.shouldCopyDefaults(false))
                .path(backupFile.toPath()).build();

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
            for (StatModifier modifier : state.statModifiers().values()) {
                ConfigurationNode modifierNode = userNode.node("stat_modifiers").appendListNode();
                modifierNode.node("name").set(modifier.name());
                modifierNode.node("stat").set(modifier.stat().getId().toString());
                modifierNode.node("operation").set(modifier.operation().toString());
                modifierNode.node("value").set(modifier.value());
            }
            // Save trait modifiers
            for (TraitModifier modifier : state.traitModifiers().values()) {
                ConfigurationNode modifierNode = userNode.node("trait_modifiers").appendListNode();
                modifierNode.node("name").set(modifier.name());
                modifierNode.node("trait").set(modifier.trait().getId().toString());
                modifierNode.node("operation").set(modifier.operation().toString());
                modifierNode.node("value").set(modifier.value());
            }
        }
        // Save the backup file
        loader.save(config);
        return backupFile;
    }

    public void loadBackupAsync(File file, Runnable onComplete, Consumer<Throwable> onError) {
        plugin.getScheduler().executeAsync(() -> {
            try {
                plugin.getBackupProvider().loadBackup(file, onComplete);
            } catch (Exception e) {
                onError.accept(e);
            }
        });
    }

    private void loadBackup(File file, Runnable onComplete) throws Exception {
        ConfigurationNode root = YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.shouldCopyDefaults(false))
                .path(file.toPath()).build().load();

        int version = root.node("backup_version").getInt(0);

        if (version == 2) {
            loadV2(root, onComplete);
        } else if (version == 1) {
            loadV1(root);
        } else {
            throw new IllegalStateException("Invalid backup_version");
        }
    }

    private void loadV2(ConfigurationNode config, Runnable onComplete) throws Exception {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Map<Object, ? extends ConfigurationNode> usersMap = config.node("users").childrenMap();
            int numUsers = usersMap.size();
            AtomicInteger applied = new AtomicInteger();

            var semaphore = new Semaphore(getConcurrencySize());
            for (ConfigurationNode userNode : usersMap.values()) {
                UserState state = getUserState(userNode);
                if (state == null) continue;

                executor.submit(() -> {
                    try {
                        semaphore.acquire();

                        plugin.getStorageProvider().applyState(state); // Save the state

                        int curr = applied.incrementAndGet();

                        if (curr % (numUsers / 10) == 0) {
                            int percent = (int) Math.round(curr / (double) numUsers * 100);
                            plugin.logger().info("Applying backup: " + percent + "%");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        semaphore.release();
                    }
                });
            }

            executor.shutdown();
            if (!executor.awaitTermination(3, TimeUnit.MINUTES)) {
                plugin.logger().severe("Some backup loading tasks did not finish in time");
            }
        }
        onComplete.run();
    }

    private int getConcurrencySize() {
        if (plugin.getStorageProvider() instanceof SqlStorageProvider) {
            return plugin.configInt(Option.SQL_POOL_MAXIMUM_POOL_SIZE);
        } else {
            return 10;
        }
    }

    private @Nullable UserState getUserState(ConfigurationNode userNode) {
        UUID uuid = getFromKey(userNode);
        if (uuid == null) return null;

        Map<Skill, Integer> skillLevels = new ConcurrentHashMap<>();
        Map<Skill, Double> skillXp = new ConcurrentHashMap<>();

        for (ConfigurationNode skillNode : userNode.node("skills").childrenMap().values()) {
            loadSkillNode(skillNode, skillLevels, skillXp);
        }
        // Load modifiers
        Map<String, StatModifier> statModifiers = new ConcurrentHashMap<>();
        for (ConfigurationNode modifierNode : userNode.node("stat_modifiers").childrenList()) {
            String name = modifierNode.node("name").getString();

            String statName = modifierNode.node("stat").getString();
            if (statName == null) continue;
            Stat stat = plugin.getStatRegistry().get(NamespacedId.fromString(statName));
            String operationName = modifierNode.node("operation").getString(Operation.ADD.toString());

            double value = modifierNode.node("value").getDouble();

            StatModifier statModifier = new StatModifier(name, stat, value, Operation.parse(operationName));
            statModifiers.put(name, statModifier);
        }
        Map<String, TraitModifier> traitModifiers = new ConcurrentHashMap<>();
        for (ConfigurationNode modifierNode : userNode.node("trait_modifiers").childrenList()) {
            String name = modifierNode.node("name").getString();

            String traitName = modifierNode.node("trait").getString();
            if (traitName == null) continue;
            Trait trait = plugin.getTraitRegistry().get(NamespacedId.fromString(traitName));
            String operationName = modifierNode.node("operation").getString(Operation.ADD.toString());

            double value = modifierNode.node("value").getDouble();

            TraitModifier traitModifier = new TraitModifier(name, trait, value, Operation.parse(operationName));
            traitModifiers.put(name, traitModifier);
        }
        double mana = userNode.node("mana").getDouble();

        // Create user state
        return new UserState(uuid, skillLevels, skillXp, statModifiers, traitModifiers, mana);
    }

    private void loadV1(ConfigurationNode config) throws Exception {
        for (ConfigurationNode playerNode : config.node("player_data").childrenMap().values()) {
            UUID uuid = getFromKey(playerNode);
            if (uuid == null) continue;

            Map<Skill, Integer> skillLevels = new ConcurrentHashMap<>();
            Map<Skill, Double> skillXp = new ConcurrentHashMap<>();

            for (ConfigurationNode skillNode : playerNode.childrenMap().values()) {
                loadSkillNode(skillNode, skillLevels, skillXp);
            }
            // Create user state from level and xp maps with empty modifiers and mana
            UserState state = new UserState(uuid, skillLevels, skillXp, new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), 0);

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
