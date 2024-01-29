package dev.aurelium.auraskills.api.registry;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import org.jetbrains.annotations.Nullable;

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
     * Gets a stat in the registry with the given id. The stat must have been already
     * registered before calling this method.
     *
     * @param id The {@link NamespacedId} of the stat. Use {@link NamespacedId#fromDefault(String)} to parse default stat names.
     * @return the stat, or null if not found
     */
    @Nullable
    Stat getStat(NamespacedId id);

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
     * Gets an ability in the registry with the given id. The ability must have been already
     * registered before calling this method.
     *
     * @param id The {@link NamespacedId} of the ability. Use {@link NamespacedId#fromDefault(String)} to parse default ability names.
     * @return the ability, or null if not found
     */
    @Nullable
    Ability getAbility(NamespacedId id);

    /**
     * Gets a mana ability in the registry with the given id. This mana ability must have been already
     * registered before calling this method.
     *
     * @param id The {@link NamespacedId} of the mana ability. Use {@link NamespacedId#fromDefault(String)} to parse default mana ability names.
     * @return the stat, or null if not found
     */
    @Nullable
    ManaAbility getManaAbility(NamespacedId id);

}
