package dev.aurelium.auraskills.common.stat;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatProvider;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.common.message.MessageProvider;
import dev.aurelium.auraskills.common.registry.OptionSupplier;
import dev.aurelium.auraskills.common.util.data.OptionProvider;

import java.util.Locale;

public class StatSupplier extends OptionSupplier<Stat> implements StatProvider {

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
        if (!statManager.isLoaded(stat)) {
            return false;
        }
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
    public String getDisplayName(Stat stat, Locale locale, boolean formatted) {
        return messageProvider.getStatDisplayName(stat, locale, formatted);
    }

    @Override
    public String getDescription(Stat stat, Locale locale, boolean formatted) {
        return messageProvider.getStatDescription(stat, locale, formatted);
    }

    @Override
    public String getColor(Stat stat, Locale locale) {
        return messageProvider.getStatColor(stat, locale);
    }

    @Override
    public String getColoredName(Stat stat, Locale locale) {
        return messageProvider.applyFormatting(messageProvider.getStatColor(stat, locale) + stat.getDisplayName(locale, false));
    }

    @Override
    public String getSymbol(Stat stat, Locale locale) {
        return messageProvider.getStatSymbol(stat, locale);
    }

    @Override
    public OptionProvider getOptions(Stat type) {
        return get(type).options();
    }

    @Override
    public boolean isLoaded(Stat type) {
        return statManager.isLoaded(type);
    }
}
