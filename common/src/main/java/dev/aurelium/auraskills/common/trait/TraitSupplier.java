package dev.aurelium.auraskills.common.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitProvider;
import dev.aurelium.auraskills.common.message.MessageProvider;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TraitSupplier implements TraitProvider {

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
    public String getDisplayName(Trait trait, Locale locale) {
        return messageProvider.getTraitDisplayName(trait, locale);
    }

    @Override
    public boolean optionBoolean(Trait type, String key) {
        return get(type).options().getBoolean(key);
    }

    @Override
    public boolean optionBoolean(Trait type, String key, boolean def) {
        return get(type).options().getBoolean(key, def);
    }

    @Override
    public int optionInt(Trait type, String key) {
        return get(type).options().getInt(key);
    }

    @Override
    public int optionInt(Trait type, String key, int def) {
        return get(type).options().getInt(key, def);
    }

    @Override
    public double optionDouble(Trait type, String key) {
        return get(type).options().getDouble(key);
    }

    @Override
    public double optionDouble(Trait type, String key, double def) {
        return get(type).options().getDouble(key, def);
    }

    @Override
    public String optionString(Trait type, String key) {
        return get(type).options().getString(key);
    }

    @Override
    public String optionString(Trait type, String key, String def) {
        return get(type).options().getString(key, def);
    }

    @Override
    public List<String> optionStringList(Trait type, String key) {
        return get(type).options().getStringList(key);
    }

    @Override
    public Map<String, Object> optionMap(Trait type, String key) {
        return get(type).options().getMap(key);
    }

}
