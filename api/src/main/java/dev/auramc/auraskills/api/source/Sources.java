package dev.auramc.auraskills.api.source;

import dev.auramc.auraskills.api.annotation.Inject;
import dev.auramc.auraskills.api.util.NamespacedId;

import java.util.Locale;

public enum Sources implements Source {

    ;

    @Inject
    private SourceProvider provider;

    private final NamespacedId id;

    Sources() {
        this.id = NamespacedId.from(NamespacedId.AURASKILLS, this.name().toLowerCase());
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public String getDisplayName(Locale locale) {
        validate();
        return provider.getDisplayName(this, locale);
    }

    private void validate() {
        if (provider == null) {
            throw new IllegalStateException("Attempting to access source provider before it has been injected!");
        }
    }

}
