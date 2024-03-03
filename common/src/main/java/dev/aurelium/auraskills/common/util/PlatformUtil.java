package dev.aurelium.auraskills.common.util;

import net.kyori.adventure.text.Component;

public interface PlatformUtil {

    boolean isValidMaterial(String input);

    boolean isValidEntityType(String input);

    String convertEntityName(String input);

    Component toComponent(String message);

    String toString(Component component);

}
