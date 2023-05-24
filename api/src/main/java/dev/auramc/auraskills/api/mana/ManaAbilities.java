package dev.auramc.auraskills.api.mana;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.annotation.Inject;
import dev.auramc.auraskills.api.registry.NamespacedId;

import java.util.Locale;

public enum ManaAbilities implements ManaAbility {

    REPLENISH,
    TREECAPITATOR,
    SPEED_MINE,
    SHARP_HOOK,
    TERRAFORM,
    CHARGED_SHOT,
    ABSORPTION,
    LIGHTNING_BLADE;

    @Inject
    private ManaAbilityProvider provider;

    private final NamespacedId id;

    ManaAbilities() {
        this.id = NamespacedId.from(NamespacedId.AURASKILLS, this.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public Skill getSkill() {
        validate();
        return provider.getSkill(this);
    }

    @Override
    public String getDisplayName(Locale locale) {
        validate();
        return provider.getDisplayName(this, locale);
    }

    @Override
    public String getDescription(Locale locale) {
        validate();
        return provider.getDescription(this, locale);
    }

    @Override
    public double getBaseValue() {
        validate();
        return provider.getBaseValue(this);
    }

    @Override
    public double getValuePerLevel() {
        validate();
        return provider.getValuePerLevel(this);
    }

    @Override
    public double getBaseCooldown() {
        validate();
        return provider.getBaseCooldown(this);
    }

    @Override
    public double getCooldownPerLevel() {
        validate();
        return provider.getCooldownPerLevel(this);
    }

    @Override
    public double getBaseManaCost() {
        validate();
        return provider.getBaseManaCost(this);
    }

    @Override
    public double getManaCostPerLevel() {
        validate();
        return provider.getManaCostPerLevel(this);
    }

    @Override
    public int getUnlock() {
        validate();
        return provider.getUnlock(this);
    }

    @Override
    public int getLevelUp() {
        validate();
        return provider.getLevelUp(this);
    }

    @Override
    public int getMaxLevel() {
        validate();
        return provider.getMaxLevel(this);
    }

    private void validate() {
        if (provider == null) {
            throw new IllegalStateException("Attempting to access mana ability provider before it has been injected!");
        }
    }

}
