package dev.aurelium.auraskills.common.stat;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatProvider;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.common.message.MessageProvider;

import java.util.List;
import java.util.Locale;

public class StatSupplier implements StatProvider {

    private final StatManager statManager;
    private final MessageProvider messageProvider;

    public StatSupplier(StatManager statManager, MessageProvider messageProvider) {
        this.statManager = statManager;
        this.messageProvider = messageProvider;
    }

    private LoadedStat get(Stat stat) {
        return statManager.getStat(stat);
    }

    @Override
    public boolean isEnabled(Stat stat) {
        return get(stat).options().enabled();
    }

    @Override
    public ImmutableList<Trait> getTraits(Stat stat) {
        return get(stat).traits();
    }

    @Override
    public double getTraitModifier(Stat stat, Trait trait) {
        return get(stat).traitConfigs().get(trait).modifier();
    }

    @Override
    public String getDisplayName(Stat stat, Locale locale) {
        return messageProvider.getStatDisplayName(stat, locale);
    }

    @Override
    public String getDescription(Stat stat, Locale locale) {
        return messageProvider.getStatDescription(stat, locale);
    }

    @Override
    public String getColor(Stat stat, Locale locale) {
        return messageProvider.getStatColor(stat, locale);
    }

    @Override
    public String getSymbol(Stat stat, Locale locale) {
        return messageProvider.getStatSymbol(stat, locale);
    }

    @Override
    public boolean optionBoolean(Stat type, String key) {
        return get(type).options().getBoolean(key);
    }

    @Override
    public boolean optionBoolean(Stat type, String key, boolean def) {
        return get(type).options().getBoolean(key, def);
    }

    @Override
    public int optionInt(Stat type, String key) {
        return get(type).options().getInt(key);
    }

    @Override
    public int optionInt(Stat type, String key, int def) {
        return get(type).options().getInt(key, def);
    }

    @Override
    public double optionDouble(Stat type, String key) {
        return get(type).options().getDouble(key);
    }

    @Override
    public double optionDouble(Stat type, String key, double def) {
        return get(type).options().getDouble(key, def);
    }

    @Override
    public String optionString(Stat type, String key) {
        return get(type).options().getString(key);
    }

    @Override
    public String optionString(Stat type, String key, String def) {
        return get(type).options().getString(key, def);
    }

    @Override
    public List<String> optionStringList(Stat type, String key) {
        return get(type).options().getStringList(key);
    }
}
