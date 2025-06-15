package dev.aurelium.auraskills.common.ref;

/**
 * A wrapper class for platform-dependent player instances
 */
public interface PlayerRef {

    Object get();

    LocationRef getLocation();

}
