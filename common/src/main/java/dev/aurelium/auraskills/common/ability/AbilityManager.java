package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager for storing and retrieving ability configs. Does not handle
 * loading configs from file.
 */
public abstract class AbilityManager {

    private final AuraSkillsPlugin plugin;
    private final Map<Ability, LoadedAbility> abilityMap;
    private final AbilitySupplier supplier;
    protected final Map<Class<?>, AbilityImpl> abilityImpls = new HashMap<>();

    public AbilityManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.abilityMap = new HashMap<>();
        this.supplier = new AbilitySupplier(this, plugin.getMessageProvider());
    }

    public abstract void registerAbilityImplementations();

    protected void addImplToMap(AbilityImpl abilityImpl) {
        abilityImpls.put(abilityImpl.getClass(), abilityImpl);
    }

    public <T extends AbilityImpl> T getAbilityImpl(Class<T> clazz) {
        AbilityImpl abilityImpl = abilityImpls.get(clazz);
        if (abilityImpl != null) {
            return clazz.cast(abilityImpl);
        }
        throw new IllegalArgumentException("Ability implementation of type " + clazz.getSimpleName() + " not found!");
    }

    @Nullable
    public AbilityImpl getAbilityImpl(Ability ability) {
        for (AbilityImpl impl : abilityImpls.values()) {
            if (impl.getAbilities().contains(ability)) {
                return impl;
            }
        }
        return null;
    }

    public String getBaseDescription(Ability ability, User user, boolean formatted) {
        String desc = ability.getDescription(user.getLocale(), formatted);
        AbilityImpl impl = plugin.getAbilityManager().getAbilityImpl(ability);
        if (impl != null) {
            desc = impl.replaceDescPlaceholders(desc, ability, user);
        }
        return desc;
    }

    // Supplier to be injected into Ability instances
    public AbilitySupplier getSupplier() {
        return supplier;
    }

    public void register(Ability ability, LoadedAbility loadedAbility) {
        abilityMap.put(ability, loadedAbility);
    }

    public void unregisterAll() {
        abilityMap.clear();
    }

    @NotNull
    public LoadedAbility getAbility(Ability ability) {
        LoadedAbility loadedAbility = abilityMap.get(ability);
        if (loadedAbility == null) {
            throw new IllegalArgumentException("Ability " + ability + " is not loaded!");
        }
        return loadedAbility;
    }

    /**
     * Gets a list of abilities unlocked or leveled up at a certain level
     *
     * @param skill The skill
     * @param level The skill level
     * @return A list of abilities
     */
    public List<Ability> getAbilities(Skill skill, int level) {
        List<Ability> skillAbilities = skill.getAbilities();
        List<Ability> abilities = new ArrayList<>();
        for (Ability ability : skillAbilities) {
            int abilityLevel = (level - ability.getUnlock()) / ability.getLevelUp() + 1;
            if (ability.getMaxLevel() > 0 && abilityLevel > ability.getMaxLevel()) {
                continue;
            }
            if (level >= ability.getUnlock() && (level - ability.getUnlock()) % ability.getLevelUp() == 0) {
                abilities.add(ability);
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

    public boolean isLoaded(Ability ability) {
        return abilityMap.containsKey(ability);
    }

    public String getChanceValue(Ability ability, int level) {
        return NumberUtil.format1(ability.getValue(level) - (Math.floor(ability.getValue(level) / 100) * 100));
    }

    public String getGuaranteedValue(Ability ability, int level) {
        return String.valueOf((int) ability.getValue(level) / 100);
    }

}
