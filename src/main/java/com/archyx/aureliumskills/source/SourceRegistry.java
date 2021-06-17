package com.archyx.aureliumskills.source;

import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class SourceRegistry {

    private final Map<Skill, Class<?>> registry;

    public SourceRegistry() {
        registry = new HashMap<>();
        try {
            for (Skill skill : Skills.values()) {
                String className = StringUtils.capitalize(skill.toString().toLowerCase(Locale.ROOT)) + "Source";
                Class<?> sourceClass = Class.forName("com.archyx.aureliumskills.skills." + skill.toString().toLowerCase(Locale.ROOT) + "." + className);
                registry.put(skill, sourceClass);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * The registered class must be an enum or a class that has a values method that returns an array of Source objects.
     * Any class must implement
     */
    public void register(Skill skill, Class<? extends Source> sourceClass) {
        registry.put(skill, sourceClass);
    }

    public Source[] values(Skill skill) {
        Class<?> sourceClass = registry.get(skill);
        if (sourceClass != null) {
            try {
                Method method = sourceClass.getMethod("values");
                Object object = method.invoke(null);
                if (object instanceof Source[]) {
                    return (Source[]) object;
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return new Source[0];
    }

    public Set<Source> values() {
        Set<Source> sourceSet = new HashSet<>();
        for (Class<?> sourceClass : registry.values()) {
            try {
                Method method = sourceClass.getMethod("values");
                Object object = method.invoke(null);
                if (object instanceof Source[]) {
                    sourceSet.addAll(Arrays.asList((Source[]) object));
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return sourceSet;
    }

    @Nullable
    public Source valueOf(String sourceString) {
        for (Source source : values()) {
            if (source.toString().equals(sourceString.toUpperCase(Locale.ROOT))) {
                return source;
            }
        }
        return null;
    }

}
