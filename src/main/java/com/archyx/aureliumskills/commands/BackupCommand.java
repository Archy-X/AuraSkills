package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.data.backup.BackupProvider;
import com.archyx.aureliumskills.data.storage.StorageProvider;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Locale;

@CommandAlias("%skills_alias")
@Subcommand("backup")
public class BackupCommand extends BaseCommand {

    private final AureliumSkills plugin;

    public BackupCommand(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("save")
    @CommandPermission("aureliumskills.backup.save")
    public void onBackupSave(CommandSender sender) {
        BackupProvider backupProvider = plugin.getBackupProvider();
        if (backupProvider != null) {
            Locale locale = plugin.getLang().getLocale(sender);
            sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.BACKUP_SAVE_SAVING, locale));
            backupProvider.saveBackup(sender, true);
        }
    }

    @Subcommand("load")
    @CommandPermission("aureliumskills.backup.load")
    public void onBackupLoad(CommandSender sender, String fileName) {
        StorageProvider storageProvider = plugin.getStorageProvider();
        Locale locale = plugin.getLang().getLocale(sender);
        if (storageProvider != null) {
            File file = new File(plugin.getDataFolder() + "/backups/" + fileName);
            if (file.exists()) {
                if (file.getName().endsWith(".yml")) {
                    // Require player to double type command
                    if (sender instanceof Player) {
                        PlayerData playerData = plugin.getPlayerManager().getPlayerData((Player) sender);
                        if (playerData == null) return;
                        Object typed = playerData.getMetadata().get("backup_command");
                        if (typed != null) {
                            if (typed instanceof String) {
                                String typedFile = (String) typed;
                                if (typedFile.equals(file.getName())) {
                                    sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.BACKUP_LOAD_LOADING, locale));
                                    storageProvider.loadBackup(YamlConfiguration.loadConfiguration(file), sender);
                                    playerData.getMetadata().remove("backup_command");
                                } else {
                                    backupLoadConfirm(playerData, sender, file);
                                }
                            } else {
                                backupLoadConfirm(playerData, sender, file);
                            }
                        } else {
                            backupLoadConfirm(playerData, sender, file);
                        }
                    } else {
                        sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.BACKUP_LOAD_LOADING, locale));
                        storageProvider.loadBackup(YamlConfiguration.loadConfiguration(file), sender);
                    }
                } else {
                    sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.BACKUP_LOAD_MUST_BE_YAML, locale));
                }
            } else { // If file does not exist
                sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.BACKUP_LOAD_FILE_NOT_FOUND, locale));
            }
        }
    }

    private void backupLoadConfirm(PlayerData playerData, CommandSender sender, File file) {
        Locale locale = playerData.getLocale();
        sender.sendMessage(AureliumSkills.getPrefix(locale) + Lang.getMessage(CommandMessage.BACKUP_LOAD_CONFIRM, locale));
        playerData.getMetadata().put("backup_command", file.getName());
        new BukkitRunnable() {
            @Override
            public void run() {
                playerData.getMetadata().remove("backup_command");
            }
        }.runTaskLater(plugin, 20 * 60);
    }
}
