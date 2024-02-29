package dev.aurelium.auraskills.api.registry;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface GlobalRegistry {

    /**
     * Gets a skill in the registry with the given id. The skill must have been already
     * registered before calling this method.
     *
     * @param id The {@link NamespacedId} of the skill. Use {@link NamespacedId#fromDefault(String)} to parse default skill names.
     * @return the skill, or null if not found
     */
    @Nullable
    Skill getSkill(NamespacedId id);

    /**
     * Gets a collection of all skills registered. Some skills may be not loaded or disabled, use
     * {@link Skill#isEnabled()} to check.
     *
     * @return all skills
     */
    Collection<Skill> getSkills();

    /**
     * Gets a stat in the registry with the given id. The stat must have been already
     * registered before calling this method.
     *
     * @param id The {@link NamespacedId} of the stat. Use {@link NamespacedId#fromDefault(String)} to parse default stat names.
     * @return the stat, or null if not found
     */
    @Nullable
    Stat getStat(NamespacedId id);

    /**
     * Gets a collection of all stats registered. Some stats may be not loaded or disabled, use
     * {@link Stat#isEnabled()} to check.
     *
     * @return all stats
     */
    Collection<Stat> getStats();

    /**
     * Gets a trait in the registry with the given id. The trait must have been already
     * registered before calling this method.
     *
     * @param id The {@link NamespacedId} of the trait. Use {@link NamespacedId#fromDefault(String)} to parse default trait names.
     * @return the stat, or null if not found
     */
    @Nullable
    Trait getTrait(NamespacedId id);

    /**
     * Gets a collection of all traits registered. Some traits may be not loaded or disabled, use
     * {@link Trait#isEnabled()} to check.
     *
     * @return all traits
     */
    Collection<Trait> getTraits();

    /**
     * Gets an ability in the registry with the given id. The ability must have been already
     * registered before calling this method.
     *
     * @param id The {@link NamespacedId} of the ability. Use {@link NamespacedId#fromDefault(String)} to parse default ability names.
     * @return the ability, or null if not found
     */
    @Nullable
    Ability getAbility(NamespacedId id);

    /**
     * Gets a collection of all abilities registered. Some abilities may be not loaded or disabled, use
     * {@link Ability#isEnabled()} to check.
     *
     * @return all abilities
     */
    Collection<Ability> getAbilities();

    /**
     * Gets a mana ability in the registry with the given id. This mana ability must have been already
     * registered before calling this method.
     *
     * @param id The {@link NamespacedId} of the mana ability. Use {@link NamespacedId#fromDefault(String)} to parse default mana ability names.
     * @return the stat, or null if not found
     */
    @Nullable
    ManaAbility getManaAbility(NamespacedId id);

    /**
     * Gets a collection of all mana abilities registered. Some mana abilities may be not loaded or disabled, use
     * {@link ManaAbilities#isEnabled()} to check.
     *
     * @return all mana abilities
     */
    Collection<ManaAbility> getManaAbilities();

}
