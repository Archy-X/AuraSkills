package com.archyx.aureliumskills.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageType;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.data.PlayerDataState;
import com.archyx.aureliumskills.lang.CommandMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.text.TextUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

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
    @SuppressWarnings("deprecation")
    public void onProfileSkills(CommandSender sender, String player) {
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
            sendSkillsProfileMessage(sender, player, uuid, playerData.getSkillLevelMap(), playerData.getSkillXpMap());
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
                            sendSkillsProfileMessage(sender, player, uuid, playerDataState.getSkillLevels(), playerDataState.getSkillXp());
                        }
                    }.runTask(plugin);
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    private void sendSkillsProfileMessage(CommandSender sender, String username, UUID uuid, Map<Skill, Integer> skillLevels, Map<Skill, Double> skillXp) {
        Locale locale = plugin.getLang().getLocale(sender);
        String message = Lang.getMessage(CommandMessage.PROFILE_PLAYER_PROFILE, locale);
        message = TextUtil.replace(message, "{name}", username, "{uuid}", uuid.toString());
        StringBuilder skillEntries = new StringBuilder();
        for (Skill skill : Skills.getOrderedValues()) {
            skillEntries.append(TextUtil.replace(Lang.getMessage(CommandMessage.PROFILE_SKILL_ENTRY, locale),
                    "{skill}", StringUtils.capitalize(skill.toString().toLowerCase(Locale.ROOT)),
                    "{level}", String.valueOf(skillLevels.get(skill)),
                    "{xp}", NumberUtil.format1(skillXp.get(skill))));
        }
        message = TextUtil.replace(message, "{skill_entries}", skillEntries.toString());
        sender.sendMessage(message.replaceAll("(\\u005C\\u006E)|(\\n)", "\n"));
    }

}
