package dev.aurelium.auraskills.bukkit.util;

import dev.aurelium.auraskills.bukkit.item.BukkitPotionType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class PotionUtil {

    public static int getDuration(BukkitPotionType potionData) {
        PotionType potionType = potionData.getType();
        if (potionType == null) return 0;

        if (VersionUtils.isAtLeastVersion(20, 2)) {
            int maxDuration = 0;
            for (PotionEffect effect : potionType.getPotionEffects()) {
                maxDuration = Math.max(maxDuration, effect.getDuration());
            }
            return maxDuration;
        } else {
            String type = potionData.getType().toString();
            if (potionData.isUpgraded()) {
                return switch (type) {
                    case "POISON" -> 420;
                    case "REGEN" -> 440;
                    case "SLOWNESS", "TURTLE_MASTER" -> 400;
                    default -> 1800;
                };
            } else if (potionData.isExtended()) {
                return switch (type) {
                    case "POISON", "REGEN" -> 1800;
                    case "SLOWNESS", "SLOW_FALLING", "WEAKNESS" -> 4800;
                    case "TURTLE_MASTER" -> 800;
                    default -> 9600;
                };
            } else {
                return switch (type) {
                    case "POISON", "REGEN" -> 900;
                    case "TURTLE_MASTER" -> 400;
                    case "SLOWNESS", "SLOW_FALLING", "WEAKNESS" -> 1800;
                    default -> 3600;
                };
            }
        }
    }

    public static String formatDuration(int duration) {
        int seconds = duration / 20;
        int minutes = seconds / 60;
        return minutes + ":" + String.format("%02d", seconds % 60);
    }

    public static void applyEffect(Player player, PotionEffect effect) {
        if (!effect.getType().isInstant()) {
            player.addPotionEffect(effect);
        }
    }

    public static boolean isNegativePotion(PotionType potionType) {
        String typeStr = potionType.toString();
        return typeStr.contains("POISON") || typeStr.contains("SLOWNESS") || typeStr.equals("INSTANT_DAMAGE") ||
                typeStr.contains("WEAKNESS") || typeStr.contains("HARMING");
    }

}
