package dev.aurelium.auraskills.common.skill;

import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.SkillProvider;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for skills.
 */
public class SkillRegistry extends Registry<Skill, SkillProvider> {

    public SkillRegistry(AuraSkillsPlugin plugin) {
        super(plugin, Skill.class, SkillProvider.class);
        registerDefaults();
    }

    public void registerDefaults() {
        for (Skill skill : Skills.values()) {
            this.register(skill.getId(), skill, plugin.getSkillManager().getSupplier());
        }
    }

    @Nullable
    public Skill getFromKey(String key) {
        for (Skill skill : getValues()) {
            if (skill.getId().getKey().equals(key)) {
                return skill;
            }
        }
        return null;
    }

    public ConfigurationNode getDefinedConfig() throws SerializationException {
        ConfigurationNode root = CommentedConfigurationNode.root();
        for (Skill skill : getValues()) {
            // Ignore non-custom skills
            if (!(skill instanceof CustomSkill customSkill)) {
                continue;
            }
            ConfigurationNode skillNode = root.node("skills", skill.getId().toString());

            // Add the list of defined abilities
            List<String> abilityList = new ArrayList<>();
            customSkill.getDefined().getAbilities().forEach(ability -> abilityList.add(ability.getId().toString()));
            if (!abilityList.isEmpty()) {
                skillNode.node("abilities").set(abilityList);
            }

            if (customSkill.getDefined().getManaAbility() != null) {
                skillNode.node("mana_abilities").set(customSkill.getDefined().getManaAbility().getId().toString());
            }
            // Set default options
            ConfigurationNode options = skillNode.node("options");
            options.node("enabled").set(true);
            options.node("max_level").set(97);
            options.node("check_multiplier_permissions").set(true);
        }
        return root;
    }

}
