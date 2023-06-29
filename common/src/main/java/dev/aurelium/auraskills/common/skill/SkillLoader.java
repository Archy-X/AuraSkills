package dev.aurelium.auraskills.common.skill;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;
import dev.aurelium.auraskills.common.source.SourceLoader;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Field;
import java.util.*;

public class SkillLoader {

    private final AuraSkillsPlugin plugin;
    private final SourceLoader sourceLoader;

    public SkillLoader(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.sourceLoader = new SourceLoader(plugin);
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
                        createLoadedAbility(ability);
                        return ability;
                    } catch (IllegalArgumentException e) {
                        plugin.logger().severe("Could not find ability " + id + " while loading " + skill.getId());
                        return null;
                    }
                }).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
    }

    private void createLoadedAbility(Ability ability) {

    }

    private ManaAbility loadManaAbility(Skill skill, ConfigurationNode config) {
        String manaAbilityStr = config.node("mana_ability").getString();
        ManaAbility manaAbility = null;
        if (manaAbilityStr != null) {
            try {
                manaAbility = plugin.getManaAbilityRegistry().get(NamespacedId.fromStringOrDefault(manaAbilityStr));
            } catch (IllegalArgumentException e) {
                plugin.logger().severe("Could not find mana ability " + manaAbilityStr + " while loading " + skill.getId());
            }
        }
        return manaAbility;
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

    private void injectProvider(Object obj, Class<?> type, Object provider) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) continue; // Ignore fields without @Inject
            if (field.getType().equals(type)) {
                field.setAccessible(true);
                try {
                    field.set(obj, provider); // Inject instance of this class
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
