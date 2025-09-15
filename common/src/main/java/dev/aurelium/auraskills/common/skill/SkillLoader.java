package dev.aurelium.auraskills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ability.AbilityLoader;
import dev.aurelium.auraskills.common.ability.LoadedAbility;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.mana.LoadedManaAbility;
import dev.aurelium.auraskills.common.mana.ManaAbilityLoader;
import dev.aurelium.auraskills.common.source.SourceLoader;
import dev.aurelium.auraskills.common.source.SourceTag;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SkillLoader {

    private static final String FILE_NAME = "skills.yml";
    private final AuraSkillsPlugin plugin;
    private final SourceLoader sourceLoader;
    private final ConfigurateLoader configurateLoader;
    private final AbilityLoader abilityLoader;
    private final ManaAbilityLoader manaAbilityLoader;

    public SkillLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.sourceLoader = new SourceLoader(plugin);
        TypeSerializerCollection skillSerializers = TypeSerializerCollection.builder().build();
        this.configurateLoader = new ConfigurateLoader(plugin, skillSerializers);
        this.abilityLoader = new AbilityLoader(plugin);
        this.manaAbilityLoader = new ManaAbilityLoader(plugin);
    }

    /**
     * Loads skills from skills.yml file, including all abilities, mana abilities, and sources for each skill.
     */
    public void loadSkills() {
        try {
            // Unregister existing skills
            plugin.getSkillManager().unregisterAll();
            // Unregister existing abilities
            plugin.getAbilityManager().unregisterAll();
            plugin.getManaAbilityManager().unregisterAll();

            configurateLoader.updateUserFile(FILE_NAME); // Update and save file
            ConfigurationNode main = configurateLoader.loadUserFile(FILE_NAME);
            ConfigurationNode defined = plugin.getSkillRegistry().getDefinedConfig();
            ConfigurationNode root = configurateLoader.loadContentAndMerge(defined, FILE_NAME, main);

            ConfigurationNode skillsNode = root.node("skills");

            abilityLoader.init(); // Load abilities.yml file in memory
            manaAbilityLoader.init(); // Load mana_abilities.yml file in memory

            int skillsLoaded = 0;
            int sourcesLoaded = 0;

            for (Object key : skillsNode.childrenMap().keySet()) {
                String skillName = (String) key;
                // Parse Skill from registry
                Skill skill = plugin.getSkillRegistry().get(NamespacedId.fromString(skillName));

                ConfigurationNode skillNode = skillsNode.node(skillName); // Get the node for the individual skill
                LoadedSkill loadedSkill = loadSkill(skill, skillNode);

                plugin.getSkillManager().register(skill, loadedSkill);

                skillsLoaded++;
                sourcesLoaded += loadedSkill.sources().size();
            }
            plugin.logger().info("Loaded " + skillsLoaded + " skills with " + sourcesLoaded + " total sources");

            // Load source tags
            for (Skill skill : plugin.getSkillManager().getSkillValues()) {
                loadSourceTags(skill);
            }
        } catch (IOException e) {
            plugin.logger().severe("Error loading skills.yml file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public LoadedSkill loadSkill(Skill skill, ConfigurationNode config) throws SerializationException {
        ImmutableList<Ability> abilities = loadAbilities(skill, config);
        ManaAbility manaAbility = loadManaAbility(skill, config);
        SkillOptions options = loadSkillOptions(config.node("options"));

        return new LoadedSkill(skill, abilities, manaAbility, loadSources(skill), options);
    }

    private ImmutableList<Ability> loadAbilities(Skill skill, ConfigurationNode config) throws SerializationException {
        List<String> abilitiesStr = config.node("abilities").getList(String.class, new ArrayList<>());
        // Parse ability names to ability instances in registry
        return abilitiesStr.stream().filter(s -> !s.isEmpty())
                .map(NamespacedId::fromDefault)
                .map(id -> {
                    try {
                        Ability ability = plugin.getAbilityRegistry().get(id);
                        createLoadedAbility(ability, skill);
                        return ability;
                    } catch (IllegalArgumentException e) {
                        plugin.logger().warn("Could not find ability " + id + " while loading " + skill.getId());
                        e.printStackTrace();
                        return null;
                    }
                }).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
    }

    private void createLoadedAbility(Ability ability, Skill skill) {
        try {
            // Load and register ability
            LoadedAbility loadedAbility = abilityLoader.loadAbility(ability, skill);
            plugin.getAbilityManager().register(ability, loadedAbility);
        } catch (SerializationException e) {
            plugin.logger().severe("Error loading ability " + ability.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ManaAbility loadManaAbility(Skill skill, ConfigurationNode config) {
        String manaAbilityStr = config.node("mana_ability").getString();
        ManaAbility manaAbility = null;
        if (manaAbilityStr != null && !manaAbilityStr.isEmpty()) {
            try {
                manaAbility = plugin.getManaAbilityRegistry().get(NamespacedId.fromDefault(manaAbilityStr));
                createLoadedManaAbility(manaAbility, skill);
            } catch (IllegalArgumentException e) {
                plugin.logger().severe("Could not find mana ability " + manaAbilityStr + " while loading " + skill.getId());
            }
        }
        return manaAbility;
    }

    private void createLoadedManaAbility(ManaAbility manaAbility, Skill skill) {
        try {
            // Load and register ability
            LoadedManaAbility loadedManaAbility = manaAbilityLoader.loadManaAbility(manaAbility, skill);
            plugin.getManaAbilityManager().register(manaAbility, loadedManaAbility);
        } catch (SerializationException e) {
            plugin.logger().severe("Error loading ability " + manaAbility.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ImmutableList<XpSource> loadSources(Skill skill) {
        if (skill instanceof Skills auraSkill) {
            // Load sources from file for included skill
            return ImmutableList.copyOf(sourceLoader.loadSources(auraSkill, plugin.getPluginFolder(), true));
        } else if (skill instanceof CustomSkill customSkill) {
            // Load sources of custom skill using the content directory of its registry
            NamespacedRegistry registry = plugin.getApi().getNamespacedRegistry(customSkill.getId().getNamespace());
            if (registry != null) {
                return ImmutableList.copyOf(sourceLoader.loadSources(customSkill, registry.getContentDirectory(), false));
            }
        }
        return ImmutableList.of();
    }

    private SkillOptions loadSkillOptions(ConfigurationNode config) {
        Map<String, Object> optionMap = new HashMap<>();
        for (Object key : config.childrenMap().keySet()) { // Loop through all keys in options node
            if (!(key instanceof String)) continue; // Skip if key is not string

            // Add value to map
            ConfigurationNode node = config.node(key);
            optionMap.put((String) key, node.raw());
        }
        return new SkillOptions(optionMap);
    }

    private void loadSourceTags(Skill skill) {
        if (!skill.isEnabled()) return;

        File contentDir;
        NamespacedRegistry registry = plugin.getApi().getNamespacedRegistry(skill.getId().getNamespace());
        if (skill instanceof CustomSkill && registry != null) {
            contentDir = registry.getContentDirectory();
        } else {
            contentDir = plugin.getPluginFolder();
        }

        Map<SourceTag, List<XpSource>> tagMap = sourceLoader.loadTags(skill, contentDir);

        for (Map.Entry<SourceTag, List<XpSource>> entry : tagMap.entrySet()) {
            plugin.getSkillManager().registerSourceTag(entry.getKey(), entry.getValue());
        }
    }

}
