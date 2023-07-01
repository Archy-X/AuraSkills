package dev.aurelium.auraskills.common.ability;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbilityProvider;
import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manager for storing and retrieving ability configs. Does not handle
 * loading configs from file.
 */
public class AbilityManager implements AbilityProvider {

    private final AuraSkillsPlugin plugin;
    private final Map<Ability, LoadedAbility> abilityMap;

    public AbilityManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        abilityMap = new HashMap<>();
    }

    public void register(Ability ability, LoadedAbility loadedAbility) {
        abilityMap.put(ability, loadedAbility);
    }

    @NotNull
    public LoadedAbility getAbility(Ability ability) {
        LoadedAbility loadedAbility = abilityMap.get(ability);
        if (loadedAbility == null) {
            throw new IllegalArgumentException("Ability " + ability + " is not loaded!");
        }
        return loadedAbility;
    }

    @Override
    public Skill getSkill(Ability ability) {
        return getAbility(ability).skill();
    }

    @Override
    public String getDisplayName(Ability ability, Locale locale) {
        return plugin.getMessageProvider().getAbilityDisplayName(ability, locale);
    }

    @Override
    public String getDescription(Ability ability, Locale locale) {
        return plugin.getMessageProvider().getAbilityDescription(ability, locale);
    }

    @Override
    public String getInfo(Ability ability, Locale locale) {
        return plugin.getMessageProvider().getAbilityInfo(ability, locale);
    }

    @Override
    public boolean isEnabled(Ability ability) {
        return getAbility(ability).config().enabled();
    }

    @Override
    public double getBaseValue(Ability ability) {
        return getAbility(ability).config().baseValue();
    }

    @Override
    public double getSecondaryBaseValue(Ability ability) {
        return getAbility(ability).config().secondaryBaseValue();
    }

    @Override
    public double getValuePerLevel(Ability ability) {
        return getAbility(ability).config().valuePerLevel();
    }

    @Override
    public double getSecondaryValuePerLevel(Ability ability) {
        return getAbility(ability).config().secondaryValuePerLevel();
    }

    @Override
    public int getUnlock(Ability ability) {
        return getAbility(ability).config().unlock();
    }

    @Override
    public int getLevelUp(Ability ability) {
        return getAbility(ability).config().levelUp();
    }

    @Override
    public int getMaxLevel(Ability ability) {
        return getAbility(ability).config().maxLevel();
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
