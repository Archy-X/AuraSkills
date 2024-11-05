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
        /*
        Saved for updating to spigot-api 1.21.3+
        if (VersionUtils.isAtLeastVersion(21, 2)) {
            MAX_HEALTH = Attribute.MAX_HEALTH;
            MOVEMENT_SPEED = Attribute.MOVEMENT_SPEED;
            ATTACK_DAMAGE = Attribute.ATTACK_DAMAGE;
            ATTACK_SPEED = Attribute.ATTACK_SPEED;
            LUCK = Attribute.LUCK;
        } else {
         */
        MAX_HEALTH = fromRegistry("generic.max_health");
        MOVEMENT_SPEED = fromRegistry("generic.movement_speed");
        ATTACK_DAMAGE = fromRegistry("generic.attack_damage");
        ATTACK_SPEED = fromRegistry("generic.attack_speed");
        LUCK = fromRegistry("generic.luck");
    }

    private static Attribute fromRegistry(String key) {
        Attribute attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(key));
        if (attribute == null) {
            throw new IllegalArgumentException("Could not find attribute with key " + key + " in Attribute registry");
        }
        return attribute;
    }

}
