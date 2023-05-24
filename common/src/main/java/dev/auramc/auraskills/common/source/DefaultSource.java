package dev.auramc.auraskills.common.source;

import dev.auramc.auraskills.api.source.Source;

public record DefaultSource(Source source, String messageSection) implements SourceProperties {
}
