package dev.auramc.auraskills.api.source;

import dev.auramc.auraskills.api.registry.NamespacedId;

import java.util.Locale;

public interface Source {

    NamespacedId getId();

    String getDisplayName(Locale locale);

    String name();

    String getMessageSection();

}
