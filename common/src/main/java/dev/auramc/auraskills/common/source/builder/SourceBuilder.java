package dev.auramc.auraskills.common.source.builder;

import dev.auramc.auraskills.api.registry.NamespacedId;
import dev.auramc.auraskills.common.source.Source;
import dev.auramc.auraskills.common.source.annotation.Required;

import java.lang.reflect.Field;

public abstract class SourceBuilder {

    protected final NamespacedId id;
    protected double xp;

    public SourceBuilder(NamespacedId id) {
        this.id = id;
    }

    public SourceBuilder xp(double xp) {
        this.xp = xp;
        return this;
    }

    public abstract Source build();

    protected <T extends SourceBuilder> void validate(T builder) {
        validateThis(); // Validate current class

        // Validate subclass
        for (Field field : builder.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Required.class)) {
                try {
                    if (field.get(builder) == null) {
                        throw new IllegalStateException("Source field " + field.getName() + " is required");
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Failed to access field " + field.getName(), e);
                }
            }
        }
    }

    // Validates fields in this class
    private void validateThis() {
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Required.class)) {
                try {
                    if (field.get(this) == null) {
                        throw new IllegalStateException("Source field " + field.getName() + " is required");
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Failed to access field " + field.getName(), e);
                }
            }
        }
    }

}
