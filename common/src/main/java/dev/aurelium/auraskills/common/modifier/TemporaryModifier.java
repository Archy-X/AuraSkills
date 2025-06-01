package dev.aurelium.auraskills.common.modifier;

import dev.aurelium.auraskills.api.util.AuraSkillsModifier;
import org.jetbrains.annotations.NotNull;

public record TemporaryModifier(
        AuraSkillsModifier<?> modifier,
        long expirationTime
) implements Comparable<TemporaryModifier> {

    @Override
    public int compareTo(@NotNull TemporaryModifier other) {
        return Long.compare(this.expirationTime, other.expirationTime);
    }

}
