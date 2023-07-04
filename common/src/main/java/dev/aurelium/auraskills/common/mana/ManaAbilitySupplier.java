package dev.aurelium.auraskills.common.mana;

import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.mana.ManaAbilityProvider;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.MessageProvider;

import java.util.List;
import java.util.Locale;

public class ManaAbilitySupplier implements ManaAbilityProvider {

    private final AuraSkillsPlugin plugin;
    private final ManaAbilityManager manaAbilityManager;
    private final MessageProvider messageProvider;

    public ManaAbilitySupplier(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.manaAbilityManager = plugin.getManaAbilityManager();
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
    public String getDisplayName(ManaAbility manaAbility, Locale locale) {
        return messageProvider.getManaAbilityDisplayName(manaAbility, locale);
    }

    @Override
    public String getDescription(ManaAbility manaAbility, Locale locale) {
        return messageProvider.getManaAbilityDescription(manaAbility, locale);
    }

    @Override
    public boolean isEnabled(ManaAbility manaAbility) {
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
            return getValue(manaAbility, level) * plugin.configDouble(Option.HEALTH_HP_INDICATOR_SCALING);
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
    public boolean optionBoolean(ManaAbility type, String key) {
        return get(type).config().getBoolean(key);
    }

    @Override
    public boolean optionBoolean(ManaAbility type, String key, boolean def) {
        return get(type).config().getBoolean(key, def);
    }

    @Override
    public int optionInt(ManaAbility type, String key) {
        return get(type).config().getInt(key);
    }

    @Override
    public int optionInt(ManaAbility type, String key, int def) {
        return get(type).config().getInt(key, def);
    }

    @Override
    public double optionDouble(ManaAbility type, String key) {
        return get(type).config().getDouble(key);
    }

    @Override
    public double optionDouble(ManaAbility type, String key, double def) {
        return get(type).config().getDouble(key, def);
    }

    @Override
    public String optionString(ManaAbility type, String key) {
        return get(type).config().getString(key);
    }

    @Override
    public String optionString(ManaAbility type, String key, String def) {
        return get(type).config().getString(key, def);
    }

    @Override
    public List<String> optionStringList(ManaAbility type, String key) {
        return get(type).config().getStringList(key);
    }
}
