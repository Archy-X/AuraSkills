package dev.auramc.auraskills.api.source;

import dev.auramc.auraskills.api.util.NamespacedId;

import java.util.Locale;

public interface Source {

    NamespacedId getId();

    String getDisplayName(Locale locale);

}
