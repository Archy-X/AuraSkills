package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.MessageBuilder;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.storage.StorageProvider;
import dev.aurelium.auraskills.common.storage.backup.BackupProvider;
import dev.aurelium.auraskills.common.util.text.TextUtil;

import java.io.File;
import java.util.Locale;

@CommandAlias("%skills_alias")
@Subcommand("backup")
public class BackupCommand extends BaseCommand {

    private final AuraSkills plugin;

    public BackupCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("save")
    @CommandPermission("auraskills.command.backup.save")
    public void onBackupSave(CommandIssuer issuer) {
        BackupProvider backupProvider = plugin.getBackupProvider();
        if (backupProvider != null) {
            Locale locale = plugin.getLocale(issuer);
            issuer.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.BACKUP_SAVE_SAVING, locale));
            try {
                File file = backupProvider.saveBackup(true);
                if (file != null) {
                    MessageBuilder.create(plugin).locale(locale).prefix().message(CommandMessage.BACKUP_SAVE_SAVED,
                            "type", plugin.getStorageProvider().getClass().getSimpleName(),
                            "file", file.getName()).send(issuer);
                } else {
                    throw new IllegalArgumentException("Did not save backup due to too many entries");
                }
            } catch (Exception e) {
                issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.BACKUP_SAVE_ERROR, locale),
                        "{type}", plugin.getStorageProvider().getClass().getSimpleName()));
            }
        }
    }

    @Subcommand("load")
    @CommandPermission("auraskills.command.backup.load")
    public void onBackupLoad(CommandIssuer issuer, String fileName) {
        StorageProvider storageProvider = plugin.getStorageProvider();
        Locale locale = plugin.getLocale(issuer);
        if (storageProvider == null) {
            return;
        }
        File file = new File(plugin.getDataFolder() + "/backups/" + fileName);
        if (!file.exists()) { // If file does not exist
            issuer.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.BACKUP_LOAD_FILE_NOT_FOUND, locale));
            return;
        }
        if (!file.getName().endsWith(".yml")) {
            issuer.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.BACKUP_LOAD_MUST_BE_YAML, locale));
            return;
        }

        if (plugin.getConfirmManager().requiresConfirmation(issuer, fileName)) {
            issuer.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.BACKUP_LOAD_CONFIRM, locale));
            return;
        }

        loadBackup(file, issuer, locale);

        plugin.getConfirmManager().remove(issuer);
    }

    private void loadBackup(File file, CommandIssuer issuer, Locale locale) {
        issuer.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.BACKUP_LOAD_LOADING, locale));
        try {
            plugin.getBackupProvider().loadBackup(file);
            MessageBuilder.create(plugin).locale(locale).prefix().message(CommandMessage.BACKUP_LOAD_LOADED).send(issuer);
        } catch (Exception e) {
            issuer.sendMessage(plugin.getPrefix(locale) + TextUtil.replace(plugin.getMsg(CommandMessage.BACKUP_LOAD_ERROR, locale),
                    "{error}", e.getMessage()));
            e.printStackTrace();
        }
    }

}
