package dev.aurelium.auraskills.api.trait;

import java.util.Locale;

public interface TraitProvider {

    String getDisplayName(Trait trait, Locale locale);

}
