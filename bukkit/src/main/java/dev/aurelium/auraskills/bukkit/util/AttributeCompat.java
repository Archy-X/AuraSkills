package dev.aurelium.auraskills.bukkit.util;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;

public class AttributeCompat {

    public static Attribute MAX_HEALTH;
    public static Attribute MOVEMENT_SPEED;
    public static Attribute ATTACK_DAMAGE;
    public static Attribute ATTACK_SPEED;
    public static Attribute LUCK;

    static {

        if (VersionUtils.isAtLeastVersion(21, 2)) {
            MAX_HEALTH = fromRegistry("max_health");
            MOVEMENT_SPEED = fromRegistry("movement_speed");
            ATTACK_DAMAGE = fromRegistry("attack_damage");
            ATTACK_SPEED = fromRegistry("attack_speed");
            LUCK = fromRegistry("luck");
        } else {
            MAX_HEALTH = fromRegistry("generic.max_health");
            MOVEMENT_SPEED = fromRegistry("generic.movement_speed");
            ATTACK_DAMAGE = fromRegistry("generic.attack_damage");
            ATTACK_SPEED = fromRegistry("generic.attack_speed");
            LUCK = fromRegistry("generic.luck");
        }
    }

    private static Attribute fromRegistry(String key) {
        Attribute attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(key));
        if (attribute == null) {
            throw new IllegalArgumentException("Could not find attribute with key " + key + " in Attribute registry");
        }
        return attribute;
    }

}
