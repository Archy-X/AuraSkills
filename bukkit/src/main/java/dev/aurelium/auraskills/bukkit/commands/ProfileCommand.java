package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageType;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserState;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@CommandAlias("%skills_alias")
@Subcommand("profile")
public class ProfileCommand extends BaseCommand {

    private final AuraSkills plugin;

    public ProfileCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("skills")
    @CommandPermission("auraskills.command.profile")
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
            User user = plugin.getUser(Bukkit.getPlayer(uuid));
            sendSkillsMessage(sender, player, uuid, user.getSkillLevelMap(), user.getSkillXpMap());
        } else { // Offline players
            plugin.getScheduler().executeAsync(() -> {
                try {
                    UserState userState = plugin.getStorageProvider().loadState(uuid);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            sendSkillsMessage(sender, player, uuid, userState.skillLevels(), userState.skillXp());
                        }
                    }.runTask(plugin);
                } catch (Exception ignored) {
                    sender.sendMessage(manager.formatMessage(manager.getCommandIssuer(sender), MessageType.ERROR, MinecraftMessageKeys.NO_PLAYER_FOUND, "{search}", player));
                }
            });
        }
    }

    @Subcommand("stats")
    @CommandPermission("auraskills.command.profile")
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
            User playerData = plugin.getUser(Bukkit.getPlayer(uuid));
            sendStatsMessage(sender, player, uuid, playerData.getSkillLevelMap(), playerData.getStatModifiers());
        } else { // Offline players
            plugin.getScheduler().executeAsync(() -> {
                try {
                    UserState userState = plugin.getStorageProvider().loadState(uuid);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            sendStatsMessage(sender, player, uuid, userState.skillLevels(), userState.statModifiers());
                        }
                    }.runTask(plugin);
                } catch (Exception ignored) {
                    sender.sendMessage(manager.formatMessage(manager.getCommandIssuer(sender), MessageType.ERROR, MinecraftMessageKeys.NO_PLAYER_FOUND, "{search}", player));
                }
            });
        }
    }

    private void sendSkillsMessage(CommandSender sender, String username, UUID uuid, Map<Skill, Integer> skillLevels, Map<Skill, Double> skillXp) {
        Locale locale = plugin.getLocale(sender);
        String message = plugin.getMsg(CommandMessage.PROFILE_SKILLS, locale);
        message = TextUtil.replace(message, "{name}", username, "{uuid}", uuid.toString());
        StringBuilder skillEntries = new StringBuilder();
        // Sort skills alphabetically
        List<Skill> skillList = new ArrayList<>(List.copyOf(skillLevels.keySet()));
        skillList.sort(Comparator.comparing(Skill::name));

        for (Skill skill : skillList) {
            if (!plugin.getSkillManager().isLoaded(skill)) continue;

            skillEntries.append(TextUtil.replace(plugin.getMsg(CommandMessage.PROFILE_SKILL_ENTRY, locale),
                    "{skill}", TextUtil.capitalize(skill.name().toLowerCase(Locale.ROOT)),
                    "{level}", String.valueOf(skillLevels.get(skill)),
                    "{xp}", NumberUtil.format1(skillXp.get(skill))));
        }
        message = TextUtil.replace(message, "{skill_entries}", skillEntries.toString());
        sender.sendMessage(message.replaceAll("(\\u005C\\u006E)|(\\n)", "\n"));
    }

    private void sendStatsMessage(CommandSender sender, String username, UUID uuid, Map<Skill, Integer> skillLevels, Map<String, StatModifier> statModifiers) {
        Locale locale = plugin.getLocale(sender);
        String message = plugin.getMsg(CommandMessage.PROFILE_STATS, locale);
        message = TextUtil.replace(message, "{name}", username, "{uuid}", uuid.toString());

        Map<Stat, Double> baseStats = new HashMap<>();
        for (Skill skill : plugin.getSkillManager().getEnabledSkills()) {
            Map<Stat, Double> skillRewardedStats = plugin.getRewardManager().getRewardTable(skill).applyStats(skillLevels.getOrDefault(skill, 1));
            for (Map.Entry<Stat, Double> entry : skillRewardedStats.entrySet()) {
                double existing = baseStats.getOrDefault(entry.getKey(), 0.0);
                baseStats.put(entry.getKey(), existing + entry.getValue());
            }
        }

        Map<Stat, Double> modifiedStats = new HashMap<>();
        for (StatModifier modifier : statModifiers.values()) {
            double existing = modifiedStats.getOrDefault(modifier.stat(), 0.0);
            modifiedStats.put(modifier.stat(), existing + modifier.value());
        }

        Map<Stat, Double> totalStats = new HashMap<>();
        for (Stat stat : plugin.getStatManager().getEnabledStats()) {
            double base = baseStats.getOrDefault(stat, 0.0);
            double modified = modifiedStats.getOrDefault(stat, 0.0);
            totalStats.put(stat, base + modified);
        }

        StringBuilder statEntries = new StringBuilder();
        // Sort stats alphabetically
        List<Stat> statList = new ArrayList<>(List.copyOf(totalStats.keySet()));
        statList.sort(Comparator.comparing(Stat::name));

        for (Stat stat : statList) {
            if (!plugin.getStatManager().isLoaded(stat)) continue;

            statEntries.append(TextUtil.replace(plugin.getMsg(CommandMessage.PROFILE_STAT_ENTRY, locale),
                    "{stat}", TextUtil.capitalize(stat.name().toLowerCase(Locale.ROOT)),
                    "{total_level}", NumberUtil.format1(totalStats.getOrDefault(stat, 0.0)),
                    "{base_level}", NumberUtil.format1(baseStats.getOrDefault(stat, 0.0)),
                    "{modified_level}", NumberUtil.format1(modifiedStats.getOrDefault(stat, 0.0))));
        }
        message = TextUtil.replace(message, "{stat_entries}", statEntries.toString());
        sender.sendMessage(message.replaceAll("(\\u005C\\u006E)|(\\n)", "\n"));
    }

}
