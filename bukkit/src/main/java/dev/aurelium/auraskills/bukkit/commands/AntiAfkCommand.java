package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageType;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.AntiAfkLog;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@CommandAlias("%skills_alias")
@Subcommand("antiafk")
public class AntiAfkCommand extends BaseCommand {

    private final AuraSkills plugin;

    public AntiAfkCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("logs")
    @CommandPermission("auraskills.command.antiafk.logs")
    @CommandCompletion("@players")
    @SuppressWarnings("deprecation")
    public void onLogs(CommandSender sender,
                       @Flags("other") String player,
                       @Default("1") @Conditions("limits:min=1") Integer page,
                       @Default("10") @Conditions("limits:min=1") Integer perPage) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        PaperCommandManager manager = plugin.getCommandManager();
        if (!offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(manager.formatMessage(manager.getCommandIssuer(sender), MessageType.ERROR, MinecraftMessageKeys.NO_PLAYER_FOUND, "{search}", player));
            return;
        }
        int offset = (page - 1) * perPage;
        Player bukkitPlayer = offlinePlayer.getPlayer();

        if (offlinePlayer.isOnline() && bukkitPlayer != null) {
            User user = plugin.getUser(bukkitPlayer);

            if (user.getStoredAntiAfkLogs().isEmpty()) {
                // Load if stored logs have not been loaded
                plugin.getScheduler().executeAsync(() -> {
                    List<AntiAfkLog> loaded = plugin.getStorageProvider().loadAntiAfkLogs(offlinePlayer.getUniqueId());
                    user.setStoredAntiAfkLogs(new ArrayList<>(loaded)); // Set loaded into User cache
                    loaded.addAll(user.getSessionAntiAfkLogs()); // Add the logs created this session to the stored logs
                    List<AntiAfkLog> logs = getMostRecent(loaded, offset, perPage);

                    // Send message back on main thread
                    plugin.getScheduler().executeSync(() -> sendLogsMessage(sender, offlinePlayer, logs, page, perPage, loaded.size()));
                });
            } else {
                List<AntiAfkLog> allLogs = user.getStoredAntiAfkLogs().get();
                allLogs.addAll(user.getSessionAntiAfkLogs());

                List<AntiAfkLog> logs = getMostRecent(allLogs, offset, perPage);

                sendLogsMessage(sender, bukkitPlayer, logs, page, perPage, allLogs.size());
            }
        } else {
            // Load logs from storage async for offline users
            plugin.getScheduler().executeAsync(() -> {
                List<AntiAfkLog> loaded = plugin.getStorageProvider().loadAntiAfkLogs(offlinePlayer.getUniqueId());
                List<AntiAfkLog> logs = getMostRecent(loaded, offset, perPage);

                // Send message back on main thread
                plugin.getScheduler().executeSync(() -> sendLogsMessage(sender, offlinePlayer, logs, page, perPage, loaded.size()));
            });
        }
    }

    private void sendLogsMessage(CommandSender recipient, OfflinePlayer target, List<AntiAfkLog> logs, int page, int perPage, int total) {
        Locale locale = plugin.getLocale(recipient);
        int pages = (total - 1) / perPage + 1;

        var sb = new StringBuilder();
        sb.append(TextUtil.replace(plugin.getMsg(CommandMessage.ANTIAFK_LOGS_HEADER, locale),
                "{player}", target.getName(),
                "{count}", String.valueOf(total),
                "{page}", String.valueOf(page),
                "{total_pages}", String.valueOf(pages)));
        sb.append("\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

        for (AntiAfkLog log : logs) {
            String formattedTime = Instant.ofEpochMilli(log.timestamp())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(formatter);

            sb.append(TextUtil.replace(plugin.getMsg(CommandMessage.ANTIAFK_LOGS_ENTRY, locale),
                    "{timestamp}", formattedTime,
                    "{message}", log.message()));
            sb.append("\n");
        }
        if (sb.length() >= 2) {
            sb.delete(sb.length() - 2, sb.length());
        }

        recipient.sendMessage(sb.toString());
    }

    private List<AntiAfkLog> getMostRecent(List<AntiAfkLog> logs, int offset, int limit) {
        List<AntiAfkLog> copied = new ArrayList<>(logs);
        copied.sort(Comparator.comparingLong(AntiAfkLog::timestamp).reversed());

        List<AntiAfkLog> limited = new ArrayList<>();
        for (int i = offset; i < offset + limit && i < copied.size(); i++) {
            limited.add(copied.get(i));
        }
        return limited;
    }

}
