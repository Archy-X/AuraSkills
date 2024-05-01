package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.ACFCoreMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.builder.BuiltMenu;
import dev.aurelium.slate.info.MenuInfo;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@CommandAlias("%skills_alias")
@Subcommand("openmenu")
public class OpenMenuCommand extends BaseCommand {

    private final AuraSkills plugin;
    private final Type mapType;
    private final Gson gson;

    public OpenMenuCommand(AuraSkills plugin) {
        this.plugin = plugin;
        this.mapType = new TypeToken<Map<String, Object>>(){}.getType();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(mapType, getDeserializer())
                .create();
    }

    @Default
    @CommandPermission("auraskills.command.openmenu")
    @CommandCompletion("@menu_names @players")
    public void onOpenMenu(CommandSender sender, String menuName, @Optional @Flags("other") Player target, @Optional JsonArg properties, @Default("0") int page) {
        Locale locale = plugin.getLocale(sender);
        if (target == null) { // Open menu for sender
            if (sender instanceof Player player) {
                openMenuLogError(player, menuName, player, properties, page);
            } else {
                sender.sendMessage(plugin.getMsg(ACFCoreMessage.NOT_ALLOWED_ON_CONSOLE, locale));
            }
        } else {
            openMenuLogError(sender, menuName, target, properties, page);
        }
    }

    private void openMenuLogError(CommandSender sender, String menuName, Player target, @Nullable JsonArg properties, int page) {
        Locale locale = plugin.getLocale(sender);
        try {
            plugin.getSlate().openMenuUnchecked(target, menuName, getProperties(target, menuName, properties), page);
        } catch (Exception e) {
            target.closeInventory(); // Ensure players can't take items out
            var errorMsg = TextUtil.replace(plugin.getMsg(ACFCoreMessage.ERROR_PREFIX, locale),
                    "{message}", e.getMessage() != null ? e.getMessage() : "Check console for error");
            sender.sendMessage(errorMsg);
            e.printStackTrace();
        }
    }

    private Map<String, Object> getProperties(Player player, String menuName, @Nullable JsonArg propString) {
        BuiltMenu builtMenu = plugin.getSlate().getBuiltMenu(menuName);
        MenuInfo menuInfo = new MenuInfo(plugin.getSlate(), player, ActiveMenu.empty(plugin.getSlate(), player));
        // Get menu default properties
        Map<String, Object> properties = new HashMap<>(builtMenu.propertyProvider().get(menuInfo));
        // Parse property string
        if (propString != null) {
            String jsonString = propString.json();
            if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
                Map<String, Object> parsedProps = gson.fromJson(propString.json(), mapType);
                properties.putAll(parsedProps);
            }
        }
        return properties;
    }

    private JsonDeserializer<Map<String, Object>> getDeserializer() {
        return (json, typeOfT, context) -> {
            Map<String, Object> resultMap = new HashMap<>();
            JsonObject jsonObject = json.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();

            for (Map.Entry<String, JsonElement> entry : entrySet) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if (value.isJsonPrimitive() && value.getAsString().startsWith("Skill:")) {
                    // Custom parsing for Skill type
                    String skillName = value.getAsString().substring(6); // Remove "Skill:" prefix
                    Skill skill = plugin.getSkillRegistry().getOrNull(NamespacedId.fromDefault(skillName));

                    resultMap.put(key, skill != null ? skill : Skills.FARMING);
                } else {
                    // Default parsing
                    resultMap.put(key, context.deserialize(value, Object.class));
                }
            }
            return resultMap;
        };
    }

}
