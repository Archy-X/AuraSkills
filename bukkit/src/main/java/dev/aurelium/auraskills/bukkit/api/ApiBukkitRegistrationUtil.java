package dev.aurelium.auraskills.bukkit.api;

import dev.aurelium.auraskills.api.AuraSkillsBukkit;
import dev.aurelium.auraskills.api.AuraSkillsBukkitProvider;

import java.lang.reflect.Method;

public class ApiBukkitRegistrationUtil {

    private static final Method REGISTER_METHOD;
    private static final Method UNREGISTER_METHOD;

    static {
        try {
            REGISTER_METHOD = AuraSkillsBukkitProvider.class.getDeclaredMethod("register", AuraSkillsBukkit.class);
            REGISTER_METHOD.setAccessible(true);

            UNREGISTER_METHOD = AuraSkillsBukkitProvider.class.getDeclaredMethod("unregister");
            UNREGISTER_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void register(AuraSkillsBukkit instance) {
        try {
            REGISTER_METHOD.invoke(null, instance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static void unregister() {
        try {
            UNREGISTER_METHOD.invoke(null);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

}
