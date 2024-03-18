package dev.aurelium.auraskills.test;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.junit.jupiter.api.Test;

public class NamespacedIdTest {

    @Test
    public void testFromString() {
        assert NamespacedId.of("namespace", "key").toString().equals("namespace/key");
        assert NamespacedId.fromString("namespace/key").toString().equals("namespace/key");
        assert NamespacedId.fromDefault("namespace/key").toString().equals("namespace/key");
        assert NamespacedId.fromDefault("key").toString().equals(NamespacedId.AURASKILLS + "/key");
    }

}
