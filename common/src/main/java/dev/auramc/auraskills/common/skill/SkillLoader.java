package dev.auramc.auraskills.common.skill;

import dev.auramc.auraskills.api.annotation.Inject;

import java.lang.reflect.Field;

public class SkillLoader {



    private void injectProvider(Object obj, Class<?> type, Object provider) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) continue; // Ignore fields without @Inject
            if (field.getType().equals(type)) {
                field.setAccessible(true);
                try {
                    field.set(obj, provider); // Inject instance of this class
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
