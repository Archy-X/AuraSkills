package dev.aurelium.auraskills.common.ref;

import java.util.Optional;

/**
 * A wrapper class for platform-dependent location instances
 */
public interface LocationRef {

    Object get();

    Optional<String> getWorldName();

}
