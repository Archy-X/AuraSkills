package dev.aurelium.auraskills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.ability.AbilityLoader;
import dev.aurelium.auraskills.common.ability.LoadedAbility;
import dev.aurelium.auraskills.common.config.ConfigurateLoader;
import dev.aurelium.auraskills.common.mana.LoadedManaAbility;
import dev.aurelium.auraskills.common.mana.ManaAbilityLoader;
import dev.aurelium.auraskills.common.source.Source;
import dev.aurelium.auraskills.common.source.SourceLoader;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.*;

public class SkillLoader {

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
            ConfigurationNode root = configurateLoader.loadUserFile("skills.yml");

            ConfigurationNode skillsNode = root.node("skills");

            abilityLoader.init(); // Load abilities.yml file in memory
            manaAbilityLoader.init(); // Load mana_abilities.yml file in memory
            for (Object key : skillsNode.childrenMap().keySet()) {
                String skillName = (String) key;
                // Parse Skill from registry
                Skill skill = plugin.getSkillRegistry().get(NamespacedId.fromString(skillName));

                ConfigurationNode skillNode = skillsNode.node(skillName); // Get the node for the individual skill
                LoadedSkill loadedSkill = loadSkill(skill, skillNode);

                plugin.getSkillManager().register(skill, loadedSkill);
            }
        } catch (ConfigurateException e) {
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
        return abilitiesStr.stream().map(NamespacedId::fromStringOrDefault)
                .map(id -> {
                    try {
                        Ability ability = plugin.getAbilityRegistry().get(id);
                        createLoadedAbility(ability, skill);
                        return ability;
                    } catch (IllegalArgumentException e) {
                        plugin.logger().severe("Could not find ability " + id + " while loading " + skill.getId());
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
        if (manaAbilityStr != null) {
            try {
                manaAbility = plugin.getManaAbilityRegistry().get(NamespacedId.fromStringOrDefault(manaAbilityStr));
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

    private ImmutableList<Source> loadSources(Skill skill) {
        if (skill instanceof Skills auraSkill) {
            // Load sources from file for included skill
            return ImmutableList.copyOf(sourceLoader.loadSources(auraSkill));
        } else {
            return ImmutableList.of();
        }
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


}
