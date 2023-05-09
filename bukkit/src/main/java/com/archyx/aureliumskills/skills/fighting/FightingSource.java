package com.archyx.aureliumskills.skills.fighting;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.source.SourceManager;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public enum FightingSource implements Source {

    PLAYER,
    BAT,
    CAT,
    CHICKEN,
    COD,
    COW,
    DONKEY,
    FOX,
    GIANT,
    HORSE,
    MUSHROOM_COW("mooshroom"),
    MULE,
    OCELOT,
    PARROT,
    PIG,
    RABBIT,
    SALMON,
    SHEEP,
    SKELETON_HORSE,
    SNOWMAN("snow_golem"),
    SQUID,
    STRIDER,
    TROPICAL_FISH,
    TURTLE,
    VILLAGER,
    WANDERING_TRADER,
    BEE,
    CAVE_SPIDER,
    DOLPHIN,
    ENDERMAN,
    IRON_GOLEM,
    LLAMA,
    PIGLIN,
    PANDA,
    POLAR_BEAR,
    PUFFERFISH,
    SPIDER,
    WOLF,
    ZOMBIFIED_PIGLIN,
    BLAZE,
    CREEPER,
    DROWNED,
    ELDER_GUARDIAN,
    ENDERMITE,
    EVOKER,
    GHAST,
    GUARDIAN,
    HOGLIN,
    HUSK,
    ILLUSIONER,
    MAGMA_CUBE,
    PHANTOM,
    PIGLIN_BRUTE,
    PILLAGER,
    RAVAGER,
    SHULKER,
    SILVERFISH,
    SKELETON,
    SLIME,
    STRAY,
    VEX,
    VINDICATOR,
    WITCH,
    WITHER_SKELETON,
    ZOGLIN,
    ZOMBIE,
    ZOMBIE_VILLAGER,
    ENDER_DRAGON,
    WITHER,
    AXOLOTL,
    GLOW_SQUID,
    GOAT,
    ALLAY,
    FROG,
    TADPOLE,
    WARDEN;

    private String configName;

    FightingSource() {

    }

    FightingSource(String configName) {
        this.configName = configName;
    }

    @Override
    public String toString() {
        return configName != null ? configName.toUpperCase(Locale.ROOT) : name();
    }

    @Override
    public String getPath() {
        if (configName == null) {
            return getSkill().toString().toLowerCase(Locale.ROOT) + "." + toString().toLowerCase(Locale.ROOT);
        } else {
            return getSkill().toString().toLowerCase(Locale.ROOT) + "." + configName.toLowerCase(Locale.ROOT);
        }
    }

    @Override
    public Skill getSkill() {
        return Skills.FIGHTING;
    }

    @Override
    public String getUnitName() {
        if (OptionL.getBoolean(Option.FIGHTING_DAMAGE_BASED)) {
            return "damage";
        }
        return null;
    }

    @Override
    public ItemStack getMenuItem() {
       return SourceManager.getMenuItem(this);
    }

    public static FightingSource getSource(EntityType entityType) {
        FightingSource source;
        try {
            source = FightingSource.valueOf(entityType.toString());
        } catch (IllegalArgumentException ignored) {
            if (entityType.toString().equals("PIG_ZOMBIE")) {
                source = FightingSource.ZOMBIFIED_PIGLIN;
            } else {
                source = FightingSource.COW; // Default backup source, shouldn't be reached
            }
        }
        return source;
    }

}
