package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.mana.ManaAbilityProvider;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Manager for storing and retrieving mana ability configs. Does not handle
 * loading configs from file.
 */
public class ManaAbilityManager implements ManaAbilityProvider {

    private final AuraSkillsPlugin plugin;
    private final Map<ManaAbility, LoadedManaAbility> manaAbilityMap;

    public ManaAbilityManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        manaAbilityMap = new HashMap<>();
    }

    public void register(ManaAbility manaAbility, LoadedManaAbility loadedManaAbility) {
        manaAbilityMap.put(manaAbility, loadedManaAbility);
    }

    public LoadedManaAbility getManaAbility(ManaAbility manaAbility) {
        LoadedManaAbility loadedMana = manaAbilityMap.get(manaAbility);
        if (loadedMana == null) {
            throw new IllegalArgumentException("Mana ability " + manaAbility + " is not loaded!");
        }
        return loadedMana;
    }

    @Override
    public Skill getSkill(ManaAbility manaAbility) {
        return getManaAbility(manaAbility).skill();
    }

    @Override
    public String getDisplayName(ManaAbility manaAbility, Locale locale) {
        return plugin.getMessageProvider().getManaAbilityDisplayName(manaAbility, locale);
    }

    @Override
    public String getDescription(ManaAbility manaAbility, Locale locale) {
        return plugin.getMessageProvider().getManaAbilityDescription(manaAbility, locale);
    }

    @Override
    public boolean isEnabled(ManaAbility manaAbility) {
        return getManaAbility(manaAbility).config().enabled();
    }

    @Override
    public double getBaseValue(ManaAbility manaAbility) {
        return getManaAbility(manaAbility).config().baseValue();
    }

    @Override
    public double getValuePerLevel(ManaAbility manaAbility) {
        return getManaAbility(manaAbility).config().valuePerLevel();
    }

    @Override
    public double getBaseCooldown(ManaAbility manaAbility) {
        return getManaAbility(manaAbility).config().baseCooldown();
    }

    @Override
    public double getCooldownPerLevel(ManaAbility manaAbility) {
        return getManaAbility(manaAbility).config().cooldownPerLevel();
    }

    @Override
    public double getBaseManaCost(ManaAbility manaAbility) {
        return getManaAbility(manaAbility).config().baseManaCost();
    }

    @Override
    public double getManaCostPerLevel(ManaAbility manaAbility) {
        return getManaAbility(manaAbility).config().manaCostPerLevel();
    }

    @Override
    public int getUnlock(ManaAbility manaAbility) {
        return getManaAbility(manaAbility).config().unlock();
    }

    @Override
    public int getLevelUp(ManaAbility manaAbility) {
        return getManaAbility(manaAbility).config().levelUp();
    }

    @Override
    public int getMaxLevel(ManaAbility manaAbility) {
        return getManaAbility(manaAbility).config().maxLevel();
    }

    /**
     * Gets the mana ability unlocked or leveled up at a certain level
     *
     * @param skill The skill
     * @param level The skill level
     * @return The mana ability unlocked or leveled up, or null
     */
    @Nullable
    public ManaAbility getManaAbilityAtLevel(Skill skill, int level) {
        ManaAbility mAbility = skill.getManaAbility();
        if (mAbility != null) {
            if (level >= getUnlock(mAbility) && (level - getUnlock(mAbility)) % getLevelUp(mAbility) == 0) {
                return mAbility;
            }
        }
        return null;
    }

}
