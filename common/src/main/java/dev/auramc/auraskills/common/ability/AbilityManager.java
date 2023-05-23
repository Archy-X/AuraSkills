package dev.auramc.auraskills.common.ability;

import com.google.common.collect.ImmutableList;
import dev.auramc.auraskills.api.ability.Ability;
import dev.auramc.auraskills.api.ability.AbstractAbility;
import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.config.OptionValue;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager for storing and retrieving ability configs. Does not handle
 * loading configs from file.
 */
public class AbilityManager {

    private final AuraSkillsPlugin plugin;
    private final Map<Ability, AbilityConfig> configMap;

    public AbilityManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        configMap = new HashMap<>();
    }

    public AbilityConfig getConfig(Ability ability) {
        AbilityConfig config = configMap.get(ability);
        if (config == null) {
            throw new IllegalArgumentException("Ability " + ability + " does not have a config!");
        }
        return config;
    }

    public void addConfig(Ability ability, AbilityConfig config) {
        configMap.put(ability, config);
    }

    public Skill getSkill(Ability ability) {
        return getConfig(ability).skill();
    }

    public boolean isEnabled(Ability ability) {
        return getConfig(ability).enabled();
    }

    public double getBaseValue(Ability ability) {
        return getConfig(ability).baseValue();
    }

    public double getValuePerLevel(Ability ability) {
        return getConfig(ability).valuePerLevel();
    }

    public double getSecondaryBaseValue(Ability ability) {
        return getConfig(ability).secondaryBaseValue();
    }

    public double getSecondaryValuePerLevel(Ability ability) {
        return getConfig(ability).secondaryValuePerLevel();
    }

    public int getUnlock(Ability ability) {
        return getConfig(ability).unlock();
    }

    public int getLevelUp(Ability ability) {
        return getConfig(ability).levelUp();
    }

    public int getMaxLevel(Ability ability) {
        return getConfig(ability).maxLevel();
    }

    public Map<String, OptionValue> getOptions(Ability ability) {
        return getConfig(ability).options();
    }

    /**
     * Gets a list of abilities unlocked or leveled up at a certain level
     *
     * @param skill The skill
     * @param level The skill level
     * @return A list of abilities
     */
    public List<Ability> getAbilities(Skill skill, int level) {
        ImmutableList<Ability> skillAbilities = skill.getAbilities();
        List<Ability> abilities = new ArrayList<>();
        if (skillAbilities.size() == 5) {
            for (Ability ability : skillAbilities) {
                if (level >= getUnlock(ability) && (level - getUnlock(ability)) % getLevelUp(ability) == 0) {
                    abilities.add(ability);
                }
            }
        }
        return abilities;
    }

    @Nullable
    public AbstractAbility getAbstractAbility(NamespacedId id) {
        // Look for the ability in the ability registry, then the mana ability registry
        try {
            return plugin.getAbilityRegistry().get(id);
        } catch (IllegalArgumentException e) {
            try {
                return plugin.getManaAbilityRegistry().get(id);
            } catch (IllegalArgumentException f) {
                return null; // Return null if the ability is not found in either registry
            }
        }
    }

}
