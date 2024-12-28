package dev.aurelium.auraskills.common.storage.file;

import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.region.BlockPosition;
import dev.aurelium.auraskills.common.storage.StorageProvider;
import dev.aurelium.auraskills.common.ui.ActionBarType;
import dev.aurelium.auraskills.common.user.AntiAfkLog;
import dev.aurelium.auraskills.common.user.SkillLevelMaps;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserState;
import dev.aurelium.auraskills.common.util.data.KeyIntPair;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileStorageProvider extends StorageProvider {

    private final String dataDirectory;

    public FileStorageProvider(AuraSkillsPlugin plugin, String dataDirectory) {
        super(plugin);
        this.dataDirectory = dataDirectory;
    }

    @Override
    protected User loadRaw(UUID uuid) throws Exception {
        CommentedConfigurationNode root = loadYamlFile(uuid);
        User user = userManager.createNewUser(uuid);
        
        if (root.empty()) {
            return user;
        }

        UUID loadedUuid = UUID.fromString(root.node("uuid").getString(uuid.toString()));
        if (!loadedUuid.equals(uuid)) { // Make sure file name and uuid in file match
            throw new IllegalStateException("UUID mismatch for player " + uuid);
        }

        // Load skill levels and xp
        SkillLevelMaps skillLevelMaps = loadSkills(root.node("skills"));
        for (Map.Entry<Skill, Integer> entry : skillLevelMaps.levels().entrySet()) {
            Skill skill = entry.getKey();
            user.setSkillLevel(skill, entry.getValue());
            user.setSkillXp(skill, skillLevelMaps.xp().get(skill));
        }

        // Load locale
        String localeString = root.node("locale").getString();
        if (localeString != null) {
            Locale locale = new Locale(localeString);
            user.setLocale(locale);
        }

        // Load mana
        double mana = root.node("mana").getDouble();
        user.setMana(mana);

        // Load stat modifiers
        loadStatModifiers(root.node("stat_modifiers")).forEach((name, modifier) -> user.addStatModifier(modifier, false));

        // Load trait modifiers
        loadTraitModifiers(root.node("trait_modifiers")).forEach((name, modifier) -> user.addTraitModifier(modifier, false));

        // Load ability data
        loadAbilityData(root.node("ability_data"), user);

        // Load unclaimed items
        loadUnclaimedItems(root.node("unclaimed_items"), user);

        // Load action bar settings
        loadActionBar(root.node("action_bar"), user);

        // Load jobs
        loadJobs(root.node("jobs"), user);
        // Load last job select time
        long time = root.node("last_job_select_time").getLong(0L);
        user.setLastJobSelectTime(time);

        return user;
    }

    private SkillLevelMaps loadSkills(ConfigurationNode node) {
        Map<Skill, Integer> levelsMap = new HashMap<>();
        Map<Skill, Double> xpMap = new HashMap<>();
        
        // Load each skill section
        node.childrenMap().forEach((skillName, skillNode) -> {
            NamespacedId skillId = NamespacedId.fromString(skillName.toString());
            Skill skill = plugin.getSkillRegistry().getOrNull(skillId);
            if (skill == null) return;

            int level = skillNode.node("level").getInt();
            double xp = skillNode.node("xp").getDouble();

            levelsMap.put(skill, level);
            xpMap.put(skill, xp);
        });
        
        return new SkillLevelMaps(levelsMap, xpMap);
    }

    private Map<String, StatModifier> loadStatModifiers(ConfigurationNode node) {
        Map<String, StatModifier> statModifiers = new HashMap<>();
        node.childrenMap().forEach((index, modifierNode) -> {
            String name = modifierNode.node("name").getString();
            String statName = modifierNode.node("stat").getString();
            double value = modifierNode.node("value").getDouble();

            if (name != null && statName != null) {
                NamespacedId statId = NamespacedId.fromString(statName);
                Stat stat = plugin.getStatRegistry().getOrNull(statId);
                if (stat == null) return;

                StatModifier statModifier = new StatModifier(name, stat, value);
                statModifiers.put(name, statModifier);
            }
        });
        return statModifiers;
    }

    private Map<String, TraitModifier> loadTraitModifiers(ConfigurationNode node) {
        Map<String, TraitModifier> traitModifiers = new HashMap<>();
        node.childrenMap().forEach((index, modifierNode) -> {
            String name = modifierNode.node("name").getString();
            String traitName = modifierNode.node("trait").getString();
            double value = modifierNode.node("value").getDouble();

            if (name != null && traitName != null) {
                NamespacedId traitId = NamespacedId.fromString(traitName);
                Trait trait = plugin.getTraitRegistry().getOrNull(traitId);
                if (trait == null) return;

                TraitModifier traitModifier = new TraitModifier(name, trait, value);
                traitModifiers.put(name, traitModifier);
            }
        });
        return traitModifiers;
    }

    private void loadAbilityData(ConfigurationNode node, User user) {
        node.childrenMap().forEach((abilityName, abilityNode) -> {
            NamespacedId abilityId = NamespacedId.fromString(abilityName.toString());
            AbstractAbility ability = plugin.getAbilityManager().getAbstractAbility(abilityId);

            abilityNode.childrenMap().forEach((key, value) -> {
                String keyStr = String.valueOf(key);
                if (keyStr.equals("cooldown") && ability instanceof ManaAbility manaAbility) {
                    user.getManaAbilityData(manaAbility).setCooldown(value.getInt());
                } else {
                    user.getAbilityData(ability).setData((String) key, value.raw());
                }
            });
        });
    }

    private void loadUnclaimedItems(ConfigurationNode node, User user) {
        List<KeyIntPair> itemList = new ArrayList<>();
        node.childrenList().forEach((itemNode) -> {
            String itemString = itemNode.getString();
            if (itemString != null) {
                String[] split = itemString.split(" ");
                // Load from format 'itemName amount'
                String itemName = split[0];
                int amount = Integer.parseInt(split[1]);

                itemList.add(new KeyIntPair(itemName, amount));
            }
        });
        user.setUnclaimedItems(itemList);
    }

    private void loadActionBar(ConfigurationNode node, User user) {
        for (ActionBarType type : ActionBarType.values()) {
            String typeName = type.toString().toLowerCase(Locale.ROOT);
            if (!node.node(typeName).virtual()) {
                user.setActionBarSetting(type, node.node(typeName).getBoolean());
            }
        }
    }

    private void loadJobs(ConfigurationNode node, User user) {
        user.clearAllJobs();

        for (ConfigurationNode jobNode : node.childrenList()) {
            String skillName = jobNode.getString();
            if (skillName == null) continue;

            Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromString(skillName));

            if (skill == null) continue;

            if (!user.canSelectJob(skill)) continue;

            user.addJob(skill);
        }
    }

    @NotNull
    private CommentedConfigurationNode loadYamlFile(UUID uuid) throws ConfigurateException {
        Path path = Path.of(dataDirectory, uuid.toString() + ".yml");

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(path)
                .build();

        return loader.load();
    }


    @Override
    @NotNull
    public UserState loadState(UUID uuid) throws Exception {
        CommentedConfigurationNode root = loadYamlFile(uuid);

        if (root.empty()) { // Return empty state if player data file doesn't exist
            return UserState.createEmpty(uuid, plugin);
        }

        UUID loadedUuid = UUID.fromString(root.node("uuid").getString(uuid.toString()));
        if (!loadedUuid.equals(uuid)) { // Make sure file name and uuid in file match
            throw new IllegalStateException("UUID mismatch for player " + uuid);
        }

        // Load skill levels and xp
        SkillLevelMaps skillLevelMaps = loadSkills(root.node("skills"));

        // Load mana
        double mana = root.node("mana").getDouble();

        // Load stat modifiers
        Map<String, StatModifier> statModifiers = loadStatModifiers(root.node("stat_modifiers"));

        // Load trait modifiers
        Map<String, TraitModifier> traitModifiers = loadTraitModifiers(root.node("trait_modifiers"));

        return new UserState(uuid, skillLevelMaps.levels(), skillLevelMaps.xp(), statModifiers, traitModifiers, mana);
    }

    @Override
    public void applyState(UserState state) throws Exception {
        CommentedConfigurationNode root = loadYamlFile(state.uuid());

        root.node("uuid").set(state.uuid().toString());

        // Apply skill levels and xp
        ConfigurationNode skillsNode = root.node("skills");
        for (Skill skill : state.skillLevels().keySet()) {
            ConfigurationNode skillNode = skillsNode.node(skill.getId().toString());
            skillNode.node("level").set(state.skillLevels().get(skill));
            skillNode.node("xp").set(state.skillXp().get(skill));
        }

        // Apply mana
        root.node("mana").set(state.mana());

        // Apply stat modifiers
        ConfigurationNode statModifiersNode = root.node("stat_modifiers");
        statModifiersNode.set(null);
        applyStatModifiers(statModifiersNode, state.statModifiers());

        // Apply trait modifiers
        ConfigurationNode traitModifiersNode = root.node("trait_modifiers");
        traitModifiersNode.set(null);
        applyTraitModifiers(traitModifiersNode, state.traitModifiers());

        saveYamlFile(root, state.uuid());
    }

    @Override
    public void save(@NotNull User user) throws Exception {
        CommentedConfigurationNode root = loadYamlFile(user.getUuid());

        root.node("uuid").set(user.getUuid().toString());

        // Apply skill levels and xp
        ConfigurationNode skillsNode = root.node("skills");
        for (Skill skill : user.getSkillLevelMap().keySet()) {
            ConfigurationNode skillNode = skillsNode.node(skill.getId().toString());
            skillNode.node("level").set(user.getSkillLevel(skill));
            skillNode.node("xp").set(user.getSkillXp(skill));
        }

        // Apply locale
        if (user.hasLocale()) {
            root.node("locale").set(user.getLocale().toString());
        }

        // Apply mana
        root.node("mana").set(user.getMana());

        // Apply stat modifiers
        ConfigurationNode statModifiersNode = root.node("stat_modifiers");
        statModifiersNode.set(null);
        applyStatModifiers(statModifiersNode, user.getStatModifiers());

        // Apply trait modifiers
        ConfigurationNode traitModifiersNode = root.node("trait_modifiers");
        traitModifiersNode.set(null);
        applyTraitModifiers(traitModifiersNode, user.getTraitModifiers());

        // Apply ability data
        ConfigurationNode abilityDataNode = root.node("ability_data");
        abilityDataNode.set(null);
        for (AbstractAbility ability : user.getAbilityDataMap().keySet()) {
            ConfigurationNode abilityNode = abilityDataNode.node(ability.getId().toString());
            for (Map.Entry<String, Object> entry : user.getAbilityData(ability).getDataMap().entrySet()) {
                abilityNode.node(entry.getKey()).set(entry.getValue());
            }
        }
        // Save mana ability cooldowns
        for (ManaAbilityData data : user.getManaAbilityDataMap().values()) {
            if (data.getCooldown() <= 0) {
                continue;
            }
            ConfigurationNode abilityNode = abilityDataNode.node(data.getManaAbility().getId().toString());
            abilityNode.node("cooldown").set(data.getCooldown());
        }

        // Apply unclaimed items
        ConfigurationNode unclaimedItemsNode = root.node("unclaimed_items");
        unclaimedItemsNode.set(null);
        for (KeyIntPair item : user.getUnclaimedItems()) {
            unclaimedItemsNode.appendListNode().set(item.getKey() + " " + item.getValue());
        }

        // Save action bar settings
        ConfigurationNode actionBarNode = root.node("action_bar");
        for (ActionBarType type : ActionBarType.values()) {
            if (type != ActionBarType.IDLE) continue; // Only save idle action bar for now
            boolean value = user.isActionBarEnabled(type);
            actionBarNode.node(type.toString().toLowerCase(Locale.ROOT)).set(value);
        }

        // Save jobs
        if (user.getJobs().isEmpty()) {
            root.removeChild("jobs");
        } else {
            List<String> jobNames = new ArrayList<>();
            for (Skill skill : user.getJobs()) {
                jobNames.add(skill.getId().toString());
            }
            root.node("jobs").set(jobNames);
        }
        // Save last job select time
        long selectTime = user.getLastJobSelectTime();
        if (selectTime > 0) {
            root.node("last_job_select_time").set(selectTime);
        }

        // Save anti-AFK logs
        if (!user.getSessionAntiAfkLogs().isEmpty()) {
            // Since logs are not loaded on join, we should append new logs instead of overriding
            ConfigurationNode node = root.node("logs", "anti_afk");
            List<ConfigurationNode> nodeList = node.getList(ConfigurationNode.class, new ArrayList<>());

            for (AntiAfkLog log : user.getSessionAntiAfkLogs()) {
                var logNode = CommentedConfigurationNode.root();
                logNode.node("time").set(log.timestamp());
                logNode.node("message").set(log.message());
                logNode.node("coords").set(log.coords().toString());
                logNode.node("world").set(log.world());

                nodeList.add(logNode);
            }
            node.setList(ConfigurationNode.class, nodeList);
        }

        saveYamlFile(root, user.getUuid());
    }

    private void saveYamlFile(CommentedConfigurationNode root, UUID uuid) throws ConfigurateException {
        // Create a Yaml loader
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(Path.of(dataDirectory, uuid.toString() + ".yml"))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();

        loader.save(root);
    }

    private void applyStatModifiers(ConfigurationNode node, Map<String, StatModifier> modifiers) throws Exception {
        int index = 0;
        for (StatModifier modifier : modifiers.values()) {
            ConfigurationNode modifierNode = node.node(String.valueOf(index));
            modifierNode.node("name").set(modifier.name());
            modifierNode.node("stat").set(modifier.stat().getId().toString());
            modifierNode.node("value").set(modifier.value());
            index++;
        }
    }

    private void applyTraitModifiers(ConfigurationNode node, Map<String, TraitModifier> modifiers) throws Exception {
        int index = 0;
        for (TraitModifier modifier : modifiers.values()) {
            ConfigurationNode modifierNode = node.node(String.valueOf(index));
            modifierNode.node("name").set(modifier.name());
            modifierNode.node("trait").set(modifier.trait().getId().toString());
            modifierNode.node("value").set(modifier.value());
            index++;
        }
    }

    @Override
    public void delete(UUID uuid) throws Exception {
        Path path = Path.of(dataDirectory, uuid.toString() + ".yml");
        Files.deleteIfExists(path);
    }

    @Override
    public List<UserState> loadStates(boolean ignoreOnline, boolean skipKeyValues) {
        List<UserState> states = new ArrayList<>();
        // Get all files in data directory
        File[] files = new File(dataDirectory).listFiles();
        if (files == null) {
            return states;
        }
        // Loop through files and get UUID from file name
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".yml")) {
                String uuidString = fileName.substring(0, fileName.length() - 4);
                try {
                    UUID uuid = UUID.fromString(uuidString);

                    if (ignoreOnline && userManager.hasUser(uuid)) {
                        continue; // Skip if player is online
                    }

                    states.add(loadState(uuid)); // Load state from file
                } catch (Exception e) {
                    plugin.logger().warn("Invalid player data file name: " + fileName);
                }
            }
        }
        return states;
    }

    @Override
    public List<AntiAfkLog> loadAntiAfkLogs(UUID uuid) {
        try {
            ConfigurationNode root = loadYamlFile(uuid);

            if (root.empty()) {
                return new ArrayList<>();
            }

            List<AntiAfkLog> logs = new ArrayList<>();

            for (ConfigurationNode logNode : root.node("logs", "anti_afk").childrenList()) {
                long timestamp = logNode.node("time").getLong();

                String message = logNode.node("message").getString("");
                String coordsStr = logNode.node("coords").getString("");
                var coords = BlockPosition.fromCommaString(coordsStr);
                String world = logNode.node("world").getString("");

                logs.add(new AntiAfkLog(timestamp, message, coords, world));
            }

            return logs;
        } catch (ConfigurateException e) {
            plugin.logger().warn("Failed to load anti-AFK logs from storage for UUID " + uuid);
            e.printStackTrace();
            return new ArrayList<>();
        }

    }
}
