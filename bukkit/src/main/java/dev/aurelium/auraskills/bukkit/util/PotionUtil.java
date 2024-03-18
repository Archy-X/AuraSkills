package dev.aurelium.auraskills.bukkit.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class PotionUtil {

    @SuppressWarnings("deprecation")
    public static int getDuration(PotionData potionData) {
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

    public static String formatDuration(int duration) {
        int seconds = duration / 20;
        int minutes = seconds / 60;
        return minutes + ":" + String.format("%02d", seconds % 60);
    }

    @SuppressWarnings("deprecation")
    public static void applyEffect(Player player, PotionEffect effect) {
        if (!effect.getType().isInstant()) {
            if (XMaterial.isNewVersion()) {
                player.addPotionEffect(effect);
            } else {
                PotionEffect currentEffect = player.getPotionEffect(effect.getType());
                // Force apply the effect if effect has greater amplifier or longer duration if same amplifier
                if (currentEffect != null) {
                    if (effect.getDuration() > currentEffect.getDuration() && effect.getAmplifier() == currentEffect.getAmplifier()) {
                        player.addPotionEffect(effect, true);
                    } else if (effect.getAmplifier() > currentEffect.getAmplifier()) {
                        player.addPotionEffect(effect, true);
                    }
                } else {
                    player.addPotionEffect(effect);
                }
            }
        }
    }

    public static boolean isNegativePotion(PotionType potionType) {
        String typeStr = potionType.toString();
        return typeStr.contains("POISON") || typeStr.contains("SLOWNESS") || typeStr.equals("INSTANT_DAMAGE") ||
                typeStr.contains("WEAKNESS") || typeStr.contains("HARMING");
    }

}
