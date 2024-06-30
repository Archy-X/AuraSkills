package dev.aurelium.auraskills.bukkit.util;

import dev.aurelium.auraskills.common.util.PlatformUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class BukkitPlatformUtil implements PlatformUtil {

    @Override
    public boolean isValidMaterial(String input) {
        Material material = parseMaterial(input.toUpperCase(Locale.ROOT));
        return material != null;
    }

    @Override
    public boolean isValidEntityType(String input) {
        input = convertEntityName(input).toUpperCase(Locale.ROOT);
        try {
            EntityType type = EntityType.valueOf(input);
            // Check if feature flag locked entity is disabled
            if (VersionUtils.isAtLeastVersion(20)) {
                for (World world : Bukkit.getWorlds()) {
                    if (!type.isEnabledByFeature(world)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String convertEntityName(String input) {
        // Don't need to convert above 1.20.5 since EntityType was renamed to match vanilla
        if (VersionUtils.isAtLeastVersion(20, 5)) {
            return input;
        }
        return switch (input) {
            case "snow_golem" -> "snowman";
            case "mooshroom" -> "mushroom_cow";
            default -> input;
        };
    }

    @Override
    public Component toComponent(String message) {
        message = TextUtil.replace(message, "§", "&"); // Replace section symbols to allow MiniMessage parsing
        MiniMessage mm = MiniMessage.miniMessage();
        try {
            return mm.deserialize(message);
        } catch (ParsingException e) {
            Bukkit.getLogger().info("[Slate] Error applying MiniMessage formatting to input message: " + message);
            e.printStackTrace();
        }
        // MiniMessage parsing
        return Component.text(message);
    }

    @Override
    public String toString(Component component) {
        String message = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build()
                .serialize(component);
        message = TextUtil.replaceNonEscaped(message, "&", "§");
        return message;
    }

    @Nullable
    private Material parseMaterial(String name) {
        return Material.getMaterial(name);
    }
}
