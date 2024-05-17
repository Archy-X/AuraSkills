package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.MessageBuilder;
import dev.aurelium.auraskills.common.message.type.ACFCoreMessage;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Locale;

@CommandAlias("%skills_alias")
@Subcommand("jobs")
public class JobsCommand extends BaseCommand {

    private final AuraSkills plugin;

    public JobsCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("add")
    @CommandCompletion("@skills @players")
    @CommandPermission("auraskills.command.jobs")
    public void onAddOther(CommandIssuer issuer, Skill job, @Flags("other") @CommandPermission("auraskills.command.jobs.other") @Optional User user) {
        Locale locale = plugin.getLocale(issuer);
        if (user == null) {
            if (issuer.isPlayer()) {
                Player player = issuer.getIssuer();
                addJobToPlayer(plugin.getUser(player), job, locale, issuer);
            } else {
                issuer.sendMessage(plugin.getMsg(ACFCoreMessage.NOT_ALLOWED_ON_CONSOLE, locale));
            }
        } else {
            addJobToPlayer(user, job, locale, issuer);
        }
    }

    private void addJobToPlayer(User user, Skill job, Locale locale, CommandIssuer issuer) {
        if (user.getJobs().size() < user.getJobLimit()) {
            if (!user.getJobs().contains(job)) {
                user.addJob(job);
                MessageBuilder.create(plugin).locale(locale).prefix()
                        .message(CommandMessage.JOBS_ADD_ADDED, "player", user.getUsername(),
                                "job", job.getDisplayName(locale) + ChatColor.RESET)
                        .send(issuer);
            } else {
                MessageBuilder.create(plugin).locale(locale).prefix()
                        .message(CommandMessage.JOBS_ADD_EXISTING, "player", user.getUsername(),
                                "job", job.getDisplayName(locale) + ChatColor.RESET)
                        .send(issuer);
            }
        } else {
            MessageBuilder.create(plugin).locale(locale).prefix()
                    .message(CommandMessage.JOBS_ADD_LIMITED, "player", user.getUsername())
                    .send(issuer);
        }
    }

    @Subcommand("remove")
    @CommandCompletion("@skills @players")
    @CommandPermission("auraskills.command.jobs")
    public void onRemove(CommandIssuer issuer, Skill job, @Flags("other") @CommandPermission("auraskills.command.jobs.other") @Optional User user) {
        Locale locale = plugin.getLocale(issuer);
        if (user == null) {
            if (issuer.isPlayer()) {
                Player player = issuer.getIssuer();
                removeJobFromPlayer(plugin.getUser(player), job, locale, issuer);
            } else {
                issuer.sendMessage(plugin.getMsg(ACFCoreMessage.NOT_ALLOWED_ON_CONSOLE, locale));
            }
        } else {
            removeJobFromPlayer(user, job, locale, issuer);
        }
    }

    private void removeJobFromPlayer(User user, Skill job, Locale locale, CommandIssuer issuer) {
        if (user.getJobs().contains(job)) {
            user.removeJob(job);
            MessageBuilder.create(plugin).locale(locale).prefix()
                    .message(CommandMessage.JOBS_REMOVE_REMOVED, "player", user.getUsername(),
                            "job", job.getDisplayName(locale) + ChatColor.RESET)
                    .send(issuer);
        } else {
            MessageBuilder.create(plugin).locale(locale).prefix()
                    .message(CommandMessage.JOBS_REMOVE_UNCHANGED, "player", user.getUsername(),
                            "job", job.getDisplayName(locale) + ChatColor.RESET)
                    .send(issuer);
        }
    }

    @Subcommand("removeall")
    @CommandCompletion("@players")
    @CommandPermission("auraskills.command.jobs")
    public void onRemoveAll(CommandIssuer issuer, @Flags("other") @CommandPermission("auraskills.command.jobs.other") @Optional User user) {
        Locale locale = plugin.getLocale(issuer);
        if (user == null) {
            if (issuer.isPlayer()) {
                Player player = issuer.getIssuer();
                removeAllJobs(plugin.getUser(player), locale, issuer);
            }
        } else {
            removeAllJobs(user, locale, issuer);
        }
    }

    private void removeAllJobs(User user, Locale locale, CommandIssuer issuer) {
        user.clearAllJobs();
        MessageBuilder.create(plugin).locale(locale).prefix()
                .message(CommandMessage.JOBS_REMOVEALL_REMOVED, "player", user.getUsername())
                .send(issuer);
    }

}
