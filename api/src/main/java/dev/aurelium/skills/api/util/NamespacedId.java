package dev.aurelium.skills.api.util;

public class NamespacedId {

    private final String namespace;
    private final String id;

    public NamespacedId(String namespace, String id) {
        this.namespace = namespace;
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return namespace + ":" + id;
    }

    public static NamespacedId fromString(String string) {
        String[] split = string.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid NamespacedId: " + string);
        }
        return new NamespacedId(split[0], split[1]);
    }

}
