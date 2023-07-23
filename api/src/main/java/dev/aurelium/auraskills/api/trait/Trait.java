package dev.aurelium.auraskills.api.trait;

import dev.aurelium.auraskills.api.option.Optioned;
import dev.aurelium.auraskills.api.registry.NamespacedId;

import java.util.Locale;

public interface Trait extends Optioned {

    NamespacedId getId();

    boolean isEnabled();

    String getDisplayName(Locale locale);

    String name();

}
