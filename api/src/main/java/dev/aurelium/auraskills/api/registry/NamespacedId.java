package dev.aurelium.auraskills.api.registry;

import java.util.Locale;
import java.util.Objects;

public class NamespacedId {

    public static final String AURASKILLS = "auraskills";
    private final String namespace;
    private final String originalKey;
    private final String key;

    private NamespacedId(String namespace, String key) {
        this.namespace = namespace.toLowerCase(Locale.ROOT);
        this.originalKey = key;
        this.key = key.toLowerCase(Locale.ROOT);
    }

    /**
     * Gets the namespace portion of the NamespacedId, usually a plugin name in lowercase.
     *
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Gets the key portion of the NamespacedId, which is the semantic name for the instance of the type,
     * such as the skill name.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the original key portion of the NamespacedId, which is the key in the case it was created with.
     *
     * @return the original key
     */
    public String getOriginalKey() {
        return originalKey;
    }

    /**
     * Returns the full String representation of the NamespacedId, with a / separating the
     * namespace and the key.
     *
     * @return the String namespace/key
     */
    @Override
    public String toString() {
        return namespace + "/" + key;
    }

    /**
     * Creates a new NamespacedId from a given namespace and key. Both should be in
     * lowercase.
     *
     * @param namespace the namespace, usually the name of the plugin calling this method
     * @param key the key that is the actual name of the instance
     * @return the created NamespacedId
     */
    public static NamespacedId of(String namespace, String key) {
        return new NamespacedId(namespace, key);
    }

    /**
     * Creates a new NamespacedId from the full String representation, with the
     * / separating the namespace and the key.
     *
     * @param string the full String, with the slash separator
     * @return the created NamespacedId
     */
    public static NamespacedId fromString(String string) {
        String[] split = string.split("/");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid NamespacedId: " + string);
        }
        return new NamespacedId(split[0], split[1]);
    }

    /**
     * Creates a new NamespacedId from the String representation. Works the
     * same as {@link #fromString(String)}, but will fall back to using the
     * default auraskills namespace if there is no slash in the input.
     *
     * @param string the String input, with or without the slash
     * @return the new NamespacedId
     */
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
