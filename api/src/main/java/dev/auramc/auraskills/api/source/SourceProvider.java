package dev.auramc.auraskills.api.source;

import java.util.Locale;

public interface SourceProvider {

    String getDisplayName(Source source, Locale locale);

}
