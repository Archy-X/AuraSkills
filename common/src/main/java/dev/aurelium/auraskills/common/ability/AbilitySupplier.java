package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbilityProvider;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.message.MessageProvider;
import dev.aurelium.auraskills.common.registry.OptionSupplier;
import dev.aurelium.auraskills.common.util.data.OptionProvider;

import java.util.Locale;

public class AbilitySupplier extends OptionSupplier<Ability> implements AbilityProvider {

    private final AbilityManager abilityManager;
    private final MessageProvider messageProvider;

    public AbilitySupplier(AbilityManager abilityManager, MessageProvider messageProvider) {
        this.abilityManager = abilityManager;
        this.messageProvider = messageProvider;
    }

    private LoadedAbility get(Ability ability) {
        return abilityManager.getAbility(ability);
    }

    @Override
    public Skill getSkill(Ability ability) {
        return get(ability).skill();
    }

    @Override
    public String getDisplayName(Ability ability, Locale locale, boolean formatted) {
        return messageProvider.getAbilityDisplayName(ability, locale, formatted);
    }

    @Override
    public String getDescription(Ability ability, Locale locale, boolean formatted) {
        return messageProvider.getAbilityDescription(ability, locale, formatted);
    }

    @Override
    public String getInfo(Ability ability, Locale locale, boolean formatted) {
        return messageProvider.getAbilityInfo(ability, locale, formatted);
    }

    @Override
    public boolean isEnabled(Ability ability) {
        if (!abilityManager.isLoaded(ability)) {
            return false;
        }
        return get(ability).config().enabled();
    }

    @Override
    public double getBaseValue(Ability ability) {
        return get(ability).config().baseValue();
    }

    @Override
    public double getSecondaryBaseValue(Ability ability) {
        return get(ability).config().secondaryBaseValue();
    }

    @Override
    public double getValue(Ability ability, int level) {
        if (level <= 0) {
            return 0.0;
        }
        return getBaseValue(ability) + (getValuePerLevel(ability) * (level - 1));
    }

    @Override
    public double getValuePerLevel(Ability ability) {
        return get(ability).config().valuePerLevel();
    }

    @Override
    public double getSecondaryValuePerLevel(Ability ability) {
        return get(ability).config().secondaryValuePerLevel();
    }

    @Override
    public double getSecondaryValue(Ability ability, int level) {
        return getSecondaryBaseValue(ability) + (getSecondaryValuePerLevel(ability) * (level - 1));
    }

    @Override
    public int getUnlock(Ability ability) {
        return get(ability).config().unlock();
    }

    @Override
    public int getLevelUp(Ability ability) {
        return get(ability).config().levelUp();
    }

    @Override
    public int getMaxLevel(Ability ability) {
        return get(ability).config().maxLevel();
    }

    @Override
    public OptionProvider getOptions(Ability type) {
        return get(type).config();
    }

    @Override
    public boolean isLoaded(Ability type) {
        return abilityManager.isLoaded(type);
    }
}
