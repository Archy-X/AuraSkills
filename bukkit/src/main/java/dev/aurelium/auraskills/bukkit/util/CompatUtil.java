package dev.aurelium.auraskills.bukkit.util;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Set;

@SuppressWarnings("deprecation")
public class CompatUtil {

    public static boolean hasEffect(Player player, Set<String> names) {
        PotionEffect effect = getEffect(player, names);
        return effect != null;
    }

    @Nullable
    public static PotionEffect getEffect(Player player, Set<String> names) {
        if (VersionUtils.isAtLeastVersion(18)) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (names.contains(effect.getType().getKey().getKey())) {
                    return effect;
                }
            }
        } else {
            // PotionEffectType#getName is deprecated since 1.20.5
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (names.contains(effect.getType().getName().toLowerCase(Locale.ROOT))) {
                    return effect;
                }
            }
        }
        return null;
    }

    public static boolean isEffect(PotionEffectType type, Set<String> names) {
        if (VersionUtils.isAtLeastVersion(18)) {
            return names.contains(type.getKey().getKey());
        } else {
            return names.contains(type.getName().toLowerCase(Locale.ROOT));
        }
    }

    public static PotionEffectType haste() {
        if (VersionUtils.isAtLeastVersion(20, 5)) {
            return PotionEffectType.HASTE;
        } else {
            return PotionEffectType.getByName("FAST_DIGGING");
        }
    }

    public static PotionEffectType resistance() {
        if (VersionUtils.isAtLeastVersion(20, 5)) {
            return PotionEffectType.RESISTANCE;
        } else {
            return PotionEffectType.getByName("DAMAGE_RESISTANCE");
        }
    }

    public static PotionEffectType slowness() {
        if (VersionUtils.isAtLeastVersion(20, 5)) {
            return PotionEffectType.SLOWNESS;
        } else {
            return PotionEffectType.getByName("SLOW");
        }
    }

    public static PotionEffectType jumpBoost() {
        if (VersionUtils.isAtLeastVersion(20, 5)) {
            return PotionEffectType.JUMP_BOOST;
        } else {
            return PotionEffectType.getByName("JUMP");
        }
    }

    public static Material getTurtleScute() {
        if (VersionUtils.isAtLeastVersion(20, 5)) {
            return Material.TURTLE_SCUTE;
        } else {
            return Material.valueOf("SCUTE");
        }
    }

    public static Particle villagerParticle() {
        if (VersionUtils.isAtLeastVersion(20, 5)) {
            return Particle.HAPPY_VILLAGER;
        } else {
            return Particle.valueOf("VILLAGER_HAPPY");
        }
    }

    public static Particle dustParticle() {
        if (VersionUtils.isAtLeastVersion(20, 5)) {
            return Particle.BLOCK;
        } else {
            return Particle.valueOf("BLOCK_DUST");
        }
    }

    public static Particle blockParticle() {
        if (VersionUtils.isAtLeastVersion(20, 5)) {
            return Particle.DUST;
        } else {
            return Particle.valueOf("REDSTONE");
        }
    }

    public static Particle witchParticle() {
        if (VersionUtils.isAtLeastVersion(20, 5)) {
            return Particle.WITCH;
        } else {
            return Particle.valueOf("SPELL_WITCH");
        }
    }

}
