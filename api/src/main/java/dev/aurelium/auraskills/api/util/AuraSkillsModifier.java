package dev.aurelium.auraskills.api.util;

import dev.aurelium.auraskills.api.stat.ReloadableIdentifier;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

public abstract class AuraSkillsModifier<T extends ReloadableIdentifier> {

    protected final String name;
    protected final T type;
    protected final double value;
    protected final Operation operation;
    private long expirationTime;
    private boolean pauseOffline;
    private boolean nonPersistent;

    public AuraSkillsModifier(String name, T type, double value, @NotNull Operation operation) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.operation = operation;
    }

    public String name() {
        return name;
    }

    public T type() {
        return type;
    }

    public double value() {
        return value;
    }

    public Operation operation() {
        return operation;
    }

    public boolean isTemporary() {
        return expirationTime != 0;
    }

    public void makeTemporary(long expirationTime, boolean pauseOffline) {
        this.expirationTime = expirationTime;
        this.pauseOffline = pauseOffline;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public boolean isPauseOffline() {
        return pauseOffline;
    }

    public boolean isNonPersistent() {
        return nonPersistent;
    }

    public void setNonPersistent() {
        this.nonPersistent = true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuraSkillsModifier<?> that = (AuraSkillsModifier<?>) o;
        return Double.compare(value, that.value) == 0 && expirationTime == that.expirationTime && pauseOffline == that.pauseOffline && nonPersistent == that.nonPersistent && Objects.equals(name, that.name) && Objects.equals(type, that.type) && operation == that.operation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, value, operation, expirationTime, pauseOffline, nonPersistent);
    }

    @Override
    public String toString() {
        return name + "," + type + "," + value;
    }

    public enum Operation {

        ADD,
        MULTIPLY,
        ADD_PERCENT;

        @NotNull
        public static Operation parse(String name) {
            if (name == null) return Operation.ADD;
            try {
                return Operation.valueOf(name.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                return Operation.ADD;
            }
        }

        public static Operation fromSqlId(byte sqlId) {
            switch (sqlId) {
                case 2:
                    return Operation.MULTIPLY;
                case 3:
                    return Operation.ADD_PERCENT;
                default:
                    return Operation.ADD;
            }
        }

        public byte getSqlId() {
            switch (this) {
                case ADD:
                    return 1;
                case MULTIPLY:
                    return 2;
                case ADD_PERCENT:
                    return 3;
                default:
                    return 0;
            }
        }

        public String getDisplayName() {
            switch (this) {
                case ADD:
                    return "Base";
                case MULTIPLY:
                    return "Multiplicative";
                case ADD_PERCENT:
                    return "Additive";
                default:
                    return this.toString();
            }
        }

    }

}
