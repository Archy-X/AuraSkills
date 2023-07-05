package dev.aurelium.auraskills.common.ability;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbilityProvider;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.message.MessageProvider;

import java.util.List;
import java.util.Locale;

public class AbilitySupplier implements AbilityProvider {

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
    public String getDisplayName(Ability ability, Locale locale) {
        return messageProvider.getAbilityDisplayName(ability, locale);
    }

    @Override
    public String getDescription(Ability ability, Locale locale) {
        return messageProvider.getAbilityDescription(ability, locale);
    }

    @Override
    public String getInfo(Ability ability, Locale locale) {
        return messageProvider.getAbilityInfo(ability, locale);
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
    public boolean optionBoolean(Ability type, String key) {
        return get(type).config().getBoolean(key);
    }

    @Override
    public boolean optionBoolean(Ability type, String key, boolean def) {
        return get(type).config().getBoolean(key, def);
    }

    @Override
    public int optionInt(Ability type, String key) {
        return get(type).config().getInt(key);
    }

    @Override
    public int optionInt(Ability type, String key, int def) {
        return get(type).config().getInt(key, def);
    }

    @Override
    public double optionDouble(Ability type, String key) {
        return get(type).config().getDouble(key);
    }

    @Override
    public double optionDouble(Ability type, String key, double def) {
        return get(type).config().getDouble(key, def);
    }

    @Override
    public String optionString(Ability type, String key) {
        return get(type).config().getString(key);
    }

    @Override
    public String optionString(Ability type, String key, String def) {
        return get(type).config().getString(key, def);
    }

    @Override
    public List<String> optionStringList(Ability type, String key) {
        return get(type).config().getStringList(key);
    }
}
