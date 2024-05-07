package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for storing and retrieving mana ability configs. Does not handle
 * loading configs from file.
 */
public abstract class ManaAbilityManager {

    private final Map<ManaAbility, LoadedManaAbility> manaAbilityMap;
    private final ManaAbilitySupplier supplier;

    public ManaAbilityManager(AuraSkillsPlugin plugin) {
        this.manaAbilityMap = new HashMap<>();
        this.supplier = new ManaAbilitySupplier(plugin, this);
    }

    // Supplier to be injected into ManaAbility instances
    public ManaAbilitySupplier getSupplier() {
        return supplier;
    }

    public void register(ManaAbility manaAbility, LoadedManaAbility loadedManaAbility) {
        manaAbilityMap.put(manaAbility, loadedManaAbility);
    }

    public void unregisterAll() {
        manaAbilityMap.clear();
    }

    @NotNull
    public LoadedManaAbility getManaAbility(ManaAbility manaAbility) {
        LoadedManaAbility loadedMana = manaAbilityMap.get(manaAbility);
        if (loadedMana == null) {
            throw new IllegalArgumentException("Mana ability " + manaAbility + " is not loaded!");
        }
        return loadedMana;
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
        ManaAbility manaAbility = skill.getManaAbility();
        if (manaAbility != null) {
            int manaAbilityLevel = (level - manaAbility.getUnlock()) / manaAbility.getLevelUp() + 1;
            if (manaAbility.getMaxLevel() > 0 && manaAbilityLevel > manaAbility.getMaxLevel()) {
                return null;
            }
            if (level >= manaAbility.getUnlock() && (level - manaAbility.getUnlock()) % manaAbility.getLevelUp() == 0) {
                return manaAbility;
            }
        }
        return null;
    }

    public boolean isLoaded(ManaAbility manaAbility) {
        return manaAbilityMap.containsKey(manaAbility);
    }

    public abstract void sendNotEnoughManaMessage(User user, double manaCost);

    public abstract String getBaseDescription(ManaAbility manaAbility, User user, boolean formatted);

}
