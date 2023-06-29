package dev.aurelium.auraskills.api.trait;

import dev.aurelium.auraskills.api.registry.NamespacedId;

import java.util.Locale;

public interface Trait {

    NamespacedId getId();

    String getDisplayName(Locale locale);

    String name();

}
