package dev.aurelium.auraskills.bukkit.util;

import com.archyx.slate.util.TextUtil;
import dev.aurelium.auraskills.common.util.PlatformUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
            EntityType.valueOf(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String convertEntityName(String input) {
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
