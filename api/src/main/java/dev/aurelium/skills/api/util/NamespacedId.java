package dev.aurelium.skills.api.util;

public class NamespacedId {

    private final String namespace;
    private final String key;

    public NamespacedId(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return namespace + ":" + key;
    }

    public static NamespacedId fromString(String string) {
        String[] split = string.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid NamespacedId: " + string);
        }
        return new NamespacedId(split[0], split[1]);
    }

}
