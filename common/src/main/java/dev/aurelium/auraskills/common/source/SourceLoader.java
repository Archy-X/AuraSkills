package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.item.ItemFilter;
import dev.aurelium.auraskills.api.item.ItemFilterMeta;
import dev.aurelium.auraskills.api.item.LootItemFilter;
import dev.aurelium.auraskills.api.item.PotionData;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.*;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.api.implementation.ApiConfigNode;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.source.parser.BlockSourceParser;
import dev.aurelium.auraskills.common.source.parser.ParsingExtension;
import dev.aurelium.auraskills.common.source.parser.util.*;
import dev.aurelium.auraskills.common.util.file.FileUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class SourceLoader {

    private final AuraSkillsPlugin plugin;
    private final ConfigurateLoader configurateLoader;

    public SourceLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        // Register utility serializers
        BaseContext context = new BaseContext(plugin.getApi());
        TypeSerializerCollection sourceSerializers = TypeSerializerCollection.builder()
                .register(ItemFilterMeta.class, new UtilitySerializer<>(new ItemFilterMetaParser(), context))
                .register(ItemFilter.class, new UtilitySerializer<>(new ItemFilterParser(), context))
                .register(LootItemFilter.class, new UtilitySerializer<>(new LootItemFilterParser(), context))
                .register(PotionData.class, new UtilitySerializer<>(new PotionDataParser(), context))
                .register(BlockXpSource.BlockXpSourceState.class, new UtilitySerializer<>(new BlockSourceParser.BlockSourceStateParser(), context))
                .build();
        this.configurateLoader = new ConfigurateLoader(plugin, sourceSerializers);
    }

    public List<XpSource> loadSources(Skill skill, File contentDirectory, boolean loadEmbedded) {
        File sourceFile = new File(contentDirectory, "sources/" + skill.name().toLowerCase(Locale.ROOT) + ".yml");
        String fileName = "sources/" + skill.name().toLowerCase(Locale.ROOT) + ".yml";
        try {
            Map<String, ConfigurationNode> embeddedSources = new HashMap<>();
            ConfigurationNode fileDefault = null;
            // Attempt to load embedded file
            ConfigurationNode embedded = configurateLoader.loadEmbeddedFileOrNull(fileName);
            if (loadEmbedded && embedded != null) {
                // Get the node containing default values in the file
                fileDefault = embedded.node("default");

                // Load each embedded source
                addToMap(embedded, embeddedSources);
            }

            // Load each user source file
            ConfigurationNode user = configurateLoader.loadUserFile(sourceFile);

            ConfigurationNode userDefault = user.node("default");

            Map<String, ConfigurationNode> userSources = new HashMap<>();
            addToMap(user, userSources);

            if (updateUserFile(embeddedSources, userSources, sourceFile, user)) {
                // Reload file if updated
                user = configurateLoader.loadUserFile(sourceFile);
                userDefault = user.node("default");
                userSources = new HashMap<>();
                addToMap(user, userSources);
            }

            // Merge embedded and user sources
            Map<String, ConfigurationNode> sources = new HashMap<>();

            for (String sourceName : userSources.keySet()) {
                ConfigurationNode userNode = userSources.get(sourceName);
                ConfigurationNode merged;
                if (loadEmbedded && fileDefault != null) {
                    ConfigurationNode embeddedNode = embeddedSources.get(sourceName);
                    merged = configurateLoader.mergeNodes(fileDefault, embeddedNode, userDefault, userNode);
                } else {
                    merged = configurateLoader.mergeNodes(userDefault, userNode);
                }
                sources.put(sourceName, merged);
            }

            // Deserialize each source
            List<XpSource> deserializedSources = new ArrayList<>();
            for (Map.Entry<String, ConfigurationNode> entry : sources.entrySet()) {
                String sourceName = entry.getKey();
                ConfigurationNode sourceNode = entry.getValue();

                NamespacedId id = NamespacedId.of(NamespacedId.AURASKILLS, sourceName);

                String type = sourceNode.node("type").getString();
                if (type == null) {
                    throw new IllegalArgumentException("Source " + id + " must specify a type");
                }
                applyNodeReplacements(sourceNode, sourceNode, sourceName);
                XpSource source = parseSourceFromType(type, sourceNode, sourceName);
                if (source != null && source.getXp() > 0.0) {
                    deserializedSources.add(source);
                    registerMenuItem(source, sourceNode);
                }
            }
            return deserializedSources;
        } catch (Exception e) {
            plugin.logger().warn("Error loading source file " + fileName + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean updateUserFile(Map<String, ConfigurationNode> embeddedSources, Map<String, ConfigurationNode> userSources,
                               File userFile, ConfigurationNode user) throws IOException {
        String skillName = userFile.getName();
        if (skillName.contains("alchemy") || skillName.contains("agility") || skillName.contains("enchanting")) {
            return false;
        }

        int added = 0;
        for (Entry<String, ConfigurationNode> embeddedEntry : embeddedSources.entrySet()) {
            String sourceName = embeddedEntry.getKey();
            // Skip if already in user source
            if (userSources.containsKey(sourceName)) {
                continue;
            }
            ConfigurationNode embeddedSource = embeddedEntry.getValue();
            // Set embedded source to user file
            user.node("sources").node(sourceName).set(embeddedSource);
            added++;
        }

        if (added > 0) {
            FileUtil.saveYamlFile(userFile, user);
            String path = plugin.getPluginFolder().toPath().relativize(userFile.toPath()).toString();
            plugin.logger().info("Updated " + path + " with " + added + " new source(s). " +
                    "If you had removed default sources they will be added back. Set the xp of the source to 0 to disable them instead.");
            return true;
        }
        return false;
    }

    public Map<SourceTag, List<XpSource>> loadTags(Skill skill, File contentDirectory) {
        File sourceFile = new File(contentDirectory, "sources/" + skill.name().toLowerCase(Locale.ROOT) + ".yml");
        try {
            Map<SourceTag, List<XpSource>> tagMap = new HashMap<>();

            ConfigurationNode user = configurateLoader.loadUserFile(sourceFile);

            for (SourceTag tag : SourceTag.ofSkill(skill)) {
                ConfigurationNode tagNode = user.node("tags").node(tag.name().toLowerCase(Locale.ROOT));

                // Parse sources from string list
                List<XpSource> sourceList = new ArrayList<>();
                for (String sourceString : tagNode.getList(String.class, new ArrayList<>())) {
                    if (sourceString.equals("*")) { // Add all sources in skill
                        sourceList.addAll(skill.getSources());
                    } else if (sourceString.startsWith("!")) { // Remove source if starts with !
                        NamespacedId id = NamespacedId.fromDefault(sourceString.substring(1));
                        XpSource source = plugin.getSkillManager().getSourceById(id);
                        if (source != null) {
                            sourceList.remove(source);
                        }
                    } else { // Add raw source name
                        XpSource source = plugin.getSkillManager().getSourceById(NamespacedId.fromDefault(sourceString));
                        if (source != null) {
                            sourceList.add(source);
                        }
                    }
                }

                tagMap.put(tag, sourceList);
            }

            return tagMap;
        } catch (Exception e) {
            plugin.logger().warn("Error loading tags in sources file " + sourceFile.getName() + ": " + e.getMessage());
            e.printStackTrace();
            // Return map with empty lists for each tag
            Map<SourceTag, List<XpSource>> fallback = new HashMap<>();
            for (SourceTag tag : SourceTag.ofSkill(skill)) {
                fallback.put(tag, new ArrayList<>());
            }
            return fallback;
        }
    }

    private void addToMap(ConfigurationNode root, Map<String, ConfigurationNode> sourcesMap) {
        root.node("sources").childrenMap().forEach((key, sourceNode) -> {
            String sourceName = key.toString();
            if (!sourceNode.isMap()) { // Replace leaf node to a child node with key _value
                try {
                    sourceNode.node("_value").set(sourceNode.raw());
                } catch (SerializationException e) {
                    throw new RuntimeException(e);
                }
            }
            sourcesMap.put(sourceName, sourceNode);
        });
    }

    @Nullable
    private XpSource parseSourceFromType(String type, ConfigurationNode sourceNode, String sourceName) {
        NamespacedId sourceTypeId = NamespacedId.fromDefault(type.toLowerCase(Locale.ROOT));
        SourceType sourceType = plugin.getSourceTypeRegistry().get(sourceTypeId);

        XpSourceParser<?> parser = sourceType.getParser();

        SourceContext context = new SourceContext(plugin.getApi(), sourceType, sourceName);
        try {
            XpSource source = (XpSource) parser.parse(ApiConfigNode.toApi(sourceNode), context);
            // Apply parsing extensions, if any
            for (ParsingExtension extension : plugin.getSourceTypeRegistry().getParsingExtensions(sourceType)) {
                source = extension.parse(source);
            }
            if (source.isVersionValid()) {
                return source;
            } else {
                return null;
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error deserializing source of type " + type + ": " + e.getMessage());
        }
    }

    private void registerMenuItem(XpSource source, ConfigurationNode sourceNode) {
        // Parse menu item if present
        ConfigurationNode menuNode = sourceNode.node("menu_item");
        if (!menuNode.virtual()) {
            plugin.getItemRegistry().getSourceMenuItems().parseAndRegisterMenuItem(source, menuNode);
        }
        // Parse unit name if present
        ConfigurationNode unitNode = sourceNode.node("unit");
        if (!unitNode.virtual()) {
            plugin.getItemRegistry().getSourceMenuItems().registerSourceUnit(source, unitNode.getString());
        }
    }

    /**
     * Searches baseNode for any String values with placeholders marked between curly braces
     * and replaces them with the corresponding value from parentNode where the key of the parentNode is the placeholder value
     *
     * @param baseNode The baseNode to search for placeholders and modify
     * @param parentNode The parentNode to get the replacement values from
     */
    private void applyNodeReplacements(ConfigurationNode baseNode, ConfigurationNode parentNode, String sourceName) throws SerializationException {
        for (ConfigurationNode child : baseNode.childrenMap().values()) {
            String text = child.getString();
            if (text != null) {
                // Get all placeholders between curly braces in text
                List<String> placeholders = TextUtil.getPlaceholders(text);
                for (String placeholder : placeholders) {
                    if (placeholder.equals("key")) { // Replace key with source name
                        child.set(text.replace("{key}", sourceName));
                        continue;
                    }
                    if (placeholder.equals("value")) {
                        child.set(text.replace("{value}", String.valueOf(parentNode.node("_value").raw())));
                    }
                    // Get replacement value from parentNode
                    String[] path = placeholder.split("\\."); // Split placeholder into path by periods
                    String replacement = parentNode.node((Object[]) path).getString();
                    if (replacement != null) {
                        // Replace placeholder with replacement value
                        child.set(text.replace("{" + placeholder + "}", replacement));
                    }
                }
            } else {
                applyNodeReplacements(child, parentNode, sourceName); // Recursively search for placeholders
            }
        }
    }
}
