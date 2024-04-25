package dev.aurelium.auraskills.bukkit.item;

import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class exists for compatibility below 1.20.5
 */
public class BukkitPotionType {

    private final PotionType type;
    private boolean extended = false;
    private boolean upgraded = false;

    public BukkitPotionType(PotionType type, boolean extended, boolean upgraded) {
        this.type = type;
        this.extended = extended;
        this.upgraded = upgraded;
    }

    public BukkitPotionType(PotionMeta meta) {
        if (VersionUtils.isAtLeastVersion(20, 2)) {
            type = meta.getBasePotionType();
            if (type != null) {
                extended = type.getKey().getKey().contains("long_");
                upgraded = type.getKey().getKey().contains("strong_");
            }
        } else {
            try {
                Object potionData = PotionMeta.class.getDeclaredMethod("getBasePotionData").invoke(meta);

                Method getType = potionData.getClass().getDeclaredMethod("getType");
                Object potionTypeObj = getType.invoke(potionData);

                type = (PotionType) potionTypeObj;

                Method isExtended = potionData.getClass().getDeclaredMethod("isExtended");
                extended = (boolean) isExtended.invoke(potionData);
                Method isUpgraded = potionData.getClass().getDeclaredMethod("isUpgraded");
                upgraded = (boolean) isUpgraded.invoke(potionData);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nullable
    public PotionType getType() {
        return type;
    }

    public boolean isExtended() {
        return extended;
    }

    public boolean isUpgraded() {
        return upgraded;
    }

    public void applyToMeta(PotionMeta meta) {
        if (VersionUtils.isAtLeastVersion(20, 2)) {
            meta.setBasePotionType(type);
        } else {
            try {
                Class<?> potionDataClass = Class.forName("org.bukkit.potion.PotionData");
                Constructor<?> potionDataConstructor = potionDataClass.getDeclaredConstructor(PotionType.class, boolean.class, boolean.class);

                Object potionData = potionDataConstructor.newInstance(type, extended, upgraded);

                Method setBasePotionData = PotionMeta.class.getDeclaredMethod("setBasePotionData", potionDataClass);
                setBasePotionData.invoke(meta, potionData);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
