package dev.auramc.auraskills;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.junit.jupiter.api.Test;

public class NamespacedIdTest {

    @Test
    public void testFromString() {
        assert NamespacedId.from("namespace", "key").toString().equals("namespace/key");
        assert NamespacedId.fromString("namespace/key").toString().equals("namespace/key");
        assert NamespacedId.fromStringOrDefault("namespace/key").toString().equals("namespace/key");
        assert NamespacedId.fromStringOrDefault("key").toString().equals(NamespacedId.AURASKILLS + "/key");
    }

}
