package dev.aurelium.auraskills.common.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitProvider;
import dev.aurelium.auraskills.common.message.MessageProvider;
import dev.aurelium.auraskills.common.registry.OptionSupplier;
import dev.aurelium.auraskills.common.util.data.OptionProvider;

import java.util.Locale;

public class TraitSupplier extends OptionSupplier<Trait> implements TraitProvider {

    private final TraitManager traitManager;
    private final MessageProvider messageProvider;

    public TraitSupplier(TraitManager traitManager, MessageProvider messageProvider) {
        this.traitManager = traitManager;
        this.messageProvider = messageProvider;
    }

    private LoadedTrait get(Trait trait) {
        return traitManager.getTrait(trait);
    }

    @Override
    public boolean isEnabled(Trait trait) {
        if (!traitManager.isLoaded(trait)) {
            return false;
        }
        return get(trait).options().enabled();
    }

    @Override
    public String getDisplayName(Trait trait, Locale locale, boolean formatted) {
        return messageProvider.getTraitDisplayName(trait, locale, formatted);
    }

    @Override
    public String getMenuDisplay(Trait trait, double value, Locale locale) {
        return traitManager.getMenuDisplay(trait, value, locale);
    }

    @Override
    public OptionProvider getOptions(Trait type) {
        return get(type).options();
    }

    @Override
    public boolean isLoaded(Trait type) {
        return traitManager.isLoaded(type);
    }
}
