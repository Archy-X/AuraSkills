package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.preset.ConfigPreset;
import dev.aurelium.auraskills.common.config.preset.PresetEntry;
import dev.aurelium.auraskills.common.config.preset.PresetLoadResult;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.ChatColor;

import java.util.Locale;

@CommandAlias("%skills_alias")
@Subcommand("preset")
public class PresetCommand extends BaseCommand {

    private final AuraSkills plugin;

    public PresetCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("load")
    @CommandPermission("auraskills.command.preset")
    public void onLoad(CommandIssuer issuer, String file) {
        Locale locale = plugin.getLocale(issuer);

        try {
            ConfigPreset configPreset = plugin.getPresetManager().preparePreset(file);
            // Require players to type the command twice
            if (plugin.getConfirmManager().requiresConfirmation(issuer, file, true)) {
                issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.PRESET_LOAD_CONFIRM, locale),
                        "{entries}", getConfirmEntries(configPreset, locale)));
                return;
            }

            PresetLoadResult result = plugin.getPresetManager().loadPreset(configPreset);
            issuer.sendMessage(getResultEntries(result, locale));
            issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.PRESET_LOAD_LOADED, locale),
                    "{name}", file));
        } catch (Exception e) {
            issuer.sendMessage(ChatColor.RED + "Error loading preset: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getConfirmEntries(ConfigPreset preset, Locale locale) {
        var sb = new StringBuilder();
        sb.append("\n");
        for (PresetEntry entry : preset.entries()) {
            String basePath = "commands.preset.load.";
            MessageKey key = null;

            switch (entry.action()) {
                case APPEND -> key = MessageKey.of(basePath + "appended_entry");
                case MERGE -> key = MessageKey.of(basePath + "merged_entry");
                case REPLACE -> key = MessageKey.of(basePath + "replaced_entry");
                case DELETE -> key = MessageKey.of(basePath + "deleted_entry");
            }
            if (key == null) continue;

            sb.append("  ");
            sb.append(TextUtil.replace(plugin.getMsg(key, locale), "{file}", entry.name()));
            sb.append("\n");
        }
        return sb.toString();
    }

    private String getResultEntries(PresetLoadResult result, Locale locale) {
        var sb = new StringBuilder();
        if (!result.created().isEmpty()) {
            sb.append(ChatColor.GREEN);
            sb.append(plugin.getMsg(CommandMessage.PRESET_LOAD_CREATED, locale)).append(":\n");
            sb.append(ChatColor.WHITE);
            result.created().forEach(file -> sb.append("  ").append(file).append("\n"));
        }
        if (!result.modified().isEmpty()) {
            sb.append(ChatColor.YELLOW);
            sb.append(plugin.getMsg(CommandMessage.PRESET_LOAD_MODIFIED, locale)).append(":\n");
            sb.append(ChatColor.WHITE);
            result.created().forEach(file -> sb.append("  ").append(file).append("\n"));
        }
        if (!result.replaced().isEmpty()) {
            sb.append(ChatColor.RED);
            sb.append(plugin.getMsg(CommandMessage.PRESET_LOAD_REPLACED, locale)).append(":\n");
            sb.append(ChatColor.WHITE);
            result.created().forEach(file -> sb.append("  ").append(file).append("\n"));
        }
        if (!result.deleted().isEmpty()) {
            sb.append(ChatColor.DARK_RED);
            sb.append(plugin.getMsg(CommandMessage.PRESET_LOAD_DELETED, locale)).append(":\n");
            sb.append(ChatColor.WHITE);
            result.created().forEach(file -> sb.append("  ").append(file).append("\n"));
        }
        if (!result.skipped().isEmpty()) {
            sb.append(ChatColor.GOLD);
            sb.append(plugin.getMsg(CommandMessage.PRESET_LOAD_SKIPPED, locale)).append(":\n");
            sb.append(ChatColor.WHITE);
            result.created().forEach(file -> sb.append("  ").append(file).append("\n"));
        }
        return sb.toString();
    }

}
