package dev.aurelium.auraskills.bukkit.util;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;

public class AttributeCompat {

    public static Attribute maxHealth;
    public static Attribute movementSpeed;
    public static Attribute attackDamage;
    public static Attribute attackSpeed;
    public static Attribute luck;

    static {
        if (VersionUtils.isAtLeastVersion(21, 2)) {
            maxHealth = fromRegistry("max_health");
            movementSpeed = fromRegistry("movement_speed");
            attackDamage = fromRegistry("attack_damage");
            attackSpeed = fromRegistry("attack_speed");
            luck = fromRegistry("luck");
        } else {
            maxHealth = fromRegistry("generic.max_health");
            movementSpeed = fromRegistry("generic.movement_speed");
            attackDamage = fromRegistry("generic.attack_damage");
            attackSpeed = fromRegistry("generic.attack_speed");
            luck = fromRegistry("generic.luck");
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
