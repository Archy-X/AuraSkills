package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageType;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.data.PlayerDataState;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.modifier.StatModifier;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@CommandAlias("%skills_alias")
@Subcommand("profile")
public class ProfileCommand extends BaseCommand {

    private final AureliumSkills plugin;

    public ProfileCommand(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("skills")
    @CommandPermission("aureliumskills.profile")
    @CommandCompletion("@players")
    @SuppressWarnings("deprecation")
    public void onSkills(CommandSender sender, String player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        PaperCommandManager manager = plugin.getCommandManager();
        if (!offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(manager.formatMessage(manager.getCommandIssuer(sender), MessageType.ERROR, MinecraftMessageKeys.NO_PLAYER_FOUND, "{search}", player));
            return;
        }
        UUID uuid = offlinePlayer.getUniqueId();
        if (offlinePlayer.isOnline()) { // Online players
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(uuid);
            if (playerData == null) {
                sender.sendMessage(manager.formatMessage(manager.getCommandIssuer(sender), MessageType.ERROR, MinecraftMessageKeys.NO_PLAYER_FOUND, "{search}", player));
                return;
            }
            sendSkillsMessage(sender, player, uuid, playerData.getSkillLevelMap(), playerData.getSkillXpMap());
        } else { // Offline players
            new BukkitRunnable() {
                @Override
                public void run() {
                    PlayerDataState playerDataState = plugin.getStorageProvider().loadState(uuid);
                    if (playerDataState == null) {
                        sender.sendMessage(manager.formatMessage(manager.getCommandIssuer(sender), MessageType.ERROR, MinecraftMessageKeys.NO_PLAYER_FOUND, "{search}", player));
                        return;
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            sendSkillsMessage(sender, player, uuid, playerDataState.getSkillLevels(), playerDataState.getSkillXp());
                        }
                    }.runTask(plugin);
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    @Subcommand("stats")
    @CommandPermission("aureliumskills.profile")
    @CommandCompletion("@players")
    @SuppressWarnings("deprecation")
    public void onStats(CommandSender sender, String player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        PaperCommandManager manager = plugin.getCommandManager();
        if (!offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(manager.formatMessage(manager.getCommandIssuer(sender), MessageType.ERROR, MinecraftMessageKeys.NO_PLAYER_FOUND, "{search}", player));
            return;
        }
        UUID uuid = offlinePlayer.getUniqueId();
        if (offlinePlayer.isOnline()) { // Online players
            PlayerData playerData = plugin.getPlayerManager().getPlayerData(uuid);
            if (playerData == null) {
                sender.sendMessage(manager.formatMessage(manager.getCommandIssuer(sender), MessageType.ERROR, MinecraftMessageKeys.NO_PLAYER_FOUND, "{search}", player));
                return;
            }
            sendStatsMessage(sender, player, uuid, playerData.getSkillLevelMap(), playerData.getStatModifiers());
        } else { // Offline players
            new BukkitRunnable() {
                @Override
                public void run() {
                    PlayerDataState playerDataState = plugin.getStorageProvider().loadState(uuid);
                    if (playerDataState == null) {
                        sender.sendMessage(manager.formatMessage(manager.getCommandIssuer(sender), MessageType.ERROR, MinecraftMessageKeys.NO_PLAYER_FOUND, "{search}", player));
                        return;
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            sendStatsMessage(sender, player, uuid, playerDataState.getSkillLevels(), playerDataState.getStatModifiers());
                        }
                    }.runTask(plugin);
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    private void sendSkillsMessage(CommandSender sender, String username, UUID uuid, Map<Skill, Integer> skillLevels, Map<Skill, Double> skillXp) {
        Locale locale = plugin.getLang().getLocale(sender);
        String message = Lang.getMessage(CommandMessage.PROFILE_SKILLS, locale);
        message = TextUtil.replace(message, "{name}", username, "{uuid}", uuid.toString());
        StringBuilder skillEntries = new StringBuilder();
        for (Skill skill : Skills.getOrderedValues()) {
            skillEntries.append(TextUtil.replace(Lang.getMessage(CommandMessage.PROFILE_SKILL_ENTRY, locale),
                    "{skill}", TextUtil.capitalize(skill.toString().toLowerCase(Locale.ROOT)),
                    "{level}", String.valueOf(skillLevels.get(skill)),
                    "{xp}", NumberUtil.format1(skillXp.get(skill))));
        }
        message = TextUtil.replace(message, "{skill_entries}", skillEntries.toString());
        sender.sendMessage(message.replaceAll("(\\u005C\\u006E)|(\\n)", "\n"));
    }

    private void sendStatsMessage(CommandSender sender, String username, UUID uuid, Map<Skill, Integer> skillLevels, Map<String, StatModifier> statModifiers) {
        Locale locale = plugin.getLang().getLocale(sender);
        String message = Lang.getMessage(CommandMessage.PROFILE_STATS, locale);
        message = TextUtil.replace(message, "{name}", username, "{uuid}", uuid.toString());

        Map<Stat, Double> baseStats = new HashMap<>();
        for (Skill skill : plugin.getSkillRegistry().getSkills()) {
            Map<Stat, Double> skillRewardedStats = plugin.getRewardManager().getRewardTable(skill).applyStats(skillLevels.getOrDefault(skill, 1));
            for (Map.Entry<Stat, Double> entry : skillRewardedStats.entrySet()) {
                double existing = baseStats.getOrDefault(entry.getKey(), 0.0);
                baseStats.put(entry.getKey(), existing + entry.getValue());
            }
        }

        Map<Stat, Double> modifiedStats = new HashMap<>();
        for (StatModifier modifier : statModifiers.values()) {
            double existing = modifiedStats.getOrDefault(modifier.getStat(), 0.0);
            modifiedStats.put(modifier.getStat(), existing + modifier.getValue());
        }

        Map<Stat, Double> totalStats = new HashMap<>();
        for (Stat stat : plugin.getStatRegistry().getStats()) {
            double base = baseStats.getOrDefault(stat, 0.0);
            double modified = modifiedStats.getOrDefault(stat, 0.0);
            totalStats.put(stat, base + modified);
        }

        StringBuilder statEntries = new StringBuilder();
        for (Stat stat : plugin.getStatRegistry().getStats()) {
            statEntries.append(TextUtil.replace(Lang.getMessage(CommandMessage.PROFILE_STAT_ENTRY, locale),
                    "{stat}", TextUtil.capitalize(stat.toString().toLowerCase(Locale.ROOT)),
                    "{total_level}", NumberUtil.format1(totalStats.getOrDefault(stat, 0.0)),
                    "{base_level}", NumberUtil.format1(baseStats.getOrDefault(stat, 0.0)),
                    "{modified_level}", NumberUtil.format1(modifiedStats.getOrDefault(stat, 0.0))));
        }
        message = TextUtil.replace(message, "{stat_entries}", statEntries.toString());
        sender.sendMessage(message.replaceAll("(\\u005C\\u006E)|(\\n)", "\n"));
    }

}
