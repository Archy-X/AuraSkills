package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.mana.ManaAbilityProvider;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.message.MessageProvider;
import dev.aurelium.auraskills.common.registry.OptionSupplier;
import dev.aurelium.auraskills.common.util.data.OptionProvider;

import java.util.Locale;

public class ManaAbilitySupplier extends OptionSupplier<ManaAbility> implements ManaAbilityProvider {

    private final ManaAbilityManager manaAbilityManager;
    private final MessageProvider messageProvider;

    public ManaAbilitySupplier(AuraSkillsPlugin plugin, ManaAbilityManager manaAbilityManager) {
        this.manaAbilityManager = manaAbilityManager;
        this.messageProvider = plugin.getMessageProvider();
    }

    private LoadedManaAbility get(ManaAbility manaAbility) {
        return manaAbilityManager.getManaAbility(manaAbility);
    }

    @Override
    public Skill getSkill(ManaAbility manaAbility) {
        return get(manaAbility).skill();
    }

    @Override
    public String getDisplayName(ManaAbility manaAbility, Locale locale, boolean formatted) {
        return messageProvider.getManaAbilityDisplayName(manaAbility, locale, formatted);
    }

    @Override
    public String getDescription(ManaAbility manaAbility, Locale locale, boolean formatted) {
        return messageProvider.getManaAbilityDescription(manaAbility, locale, formatted);
    }

    @Override
    public boolean isEnabled(ManaAbility manaAbility) {
        if (!manaAbilityManager.isLoaded(manaAbility)) {
            return false;
        }
        return get(manaAbility).config().enabled();
    }

    @Override
    public double getBaseValue(ManaAbility manaAbility) {
        return get(manaAbility).config().baseValue();
    }

    @Override
    public double getValuePerLevel(ManaAbility manaAbility) {
        return get(manaAbility).config().valuePerLevel();
    }

    @Override
    public double getValue(ManaAbility manaAbility, int level) {
        return getBaseValue(manaAbility) + (getValuePerLevel(manaAbility) * (level - 1));
    }

    @Override
    public double getDisplayValue(ManaAbility manaAbility, int level) {
        if (manaAbility == ManaAbilities.SHARP_HOOK && manaAbility.optionBoolean("display_damage_with_scaling", true)) {
            return getValue(manaAbility, level) * Traits.HP.optionDouble("action_bar_scaling", 1);
        } else {
            return getValue(manaAbility, level);
        }
    }

    @Override
    public double getBaseCooldown(ManaAbility manaAbility) {
        return get(manaAbility).config().baseCooldown();
    }

    @Override
    public double getCooldownPerLevel(ManaAbility manaAbility) {
        return get(manaAbility).config().cooldownPerLevel();
    }

    @Override
    public double getCooldown(ManaAbility manaAbility, int level) {
        double cooldown = getBaseCooldown(manaAbility) + (getCooldownPerLevel(manaAbility) * (level - 1));
        return cooldown > 0 ? cooldown : 0;
    }

    @Override
    public double getBaseManaCost(ManaAbility manaAbility) {
        return get(manaAbility).config().baseManaCost();
    }

    @Override
    public double getManaCostPerLevel(ManaAbility manaAbility) {
        return get(manaAbility).config().manaCostPerLevel();
    }

    @Override
    public double getManaCost(ManaAbility manaAbility, int level) {
        return getBaseManaCost(manaAbility) + (getManaCostPerLevel(manaAbility) * (level - 1));
    }

    @Override
    public int getUnlock(ManaAbility manaAbility) {
        return get(manaAbility).config().unlock();
    }

    @Override
    public int getLevelUp(ManaAbility manaAbility) {
        return get(manaAbility).config().levelUp();
    }

    @Override
    public int getMaxLevel(ManaAbility manaAbility) {
        return get(manaAbility).config().maxLevel();
    }

    @Override
    public OptionProvider getOptions(ManaAbility type) {
        return get(type).config();
    }

    @Override
    public boolean isLoaded(ManaAbility type) {
        return manaAbilityManager.isLoaded(type);
    }
}
