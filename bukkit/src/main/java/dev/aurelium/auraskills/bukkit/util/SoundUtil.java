package dev.aurelium.auraskills.bukkit.util;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class SoundUtil {

    @Nullable
    private static Method soundValueOf = null;

    @SuppressWarnings("deprecation")
    public static Sound getFromEitherName(String name) throws RuntimeException {
        String registryName = name.toLowerCase(Locale.ROOT);
        var key = NamespacedKey.fromString(registryName);
        if (key != null) {
            Sound sound = Registry.SOUNDS.get(key);
            if (sound != null) {
                return sound;
            }
        }

        if (VersionUtils.isAtLeastVersion(21, 3)) {
            return Sound.valueOf(name);
        } else {
            if (soundValueOf == null) {
                try {
                    soundValueOf = Sound.class.getDeclaredMethod("valueOf", String.class);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                Object soundObj = soundValueOf.invoke(null, name.toUpperCase(Locale.ROOT));
                if (soundObj instanceof Sound sound) {
                    return sound;
                } else {
                    throw new IllegalArgumentException("Returned object is not a Sound instance");
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
