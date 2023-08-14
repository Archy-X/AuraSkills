package dev.aurelium.auraskills.api.registry;

import java.util.Locale;
import java.util.Objects;

public class NamespacedId {

    public static final String AURASKILLS = "auraskills";
    private final String namespace;
    private final String key;

    private NamespacedId(String namespace, String key) {
        this.namespace = namespace.toLowerCase(Locale.ROOT);
        this.key = key.toLowerCase(Locale.ROOT);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return namespace + "/" + key;
    }

    public static NamespacedId from(String namespace, String key) {
        return new NamespacedId(namespace, key);
    }

    public static NamespacedId fromString(String string) {
        String[] split = string.split("/");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid NamespacedId: " + string);
        }
        return new NamespacedId(split[0], split[1]);
    }

    public static NamespacedId fromDefault(String string) {
        String[] split = string.split("/");
        if (split.length == 1) {
            return new NamespacedId(NamespacedId.AURASKILLS, split[0]); // Use default namespace if not specified
        } else if (split.length != 2) {
            throw new IllegalArgumentException("Invalid NamespacedId: " + string);
        }
        return new NamespacedId(split[0], split[1]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamespacedId that = (NamespacedId) o;
        return Objects.equals(namespace, that.namespace) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, key);
    }
}
