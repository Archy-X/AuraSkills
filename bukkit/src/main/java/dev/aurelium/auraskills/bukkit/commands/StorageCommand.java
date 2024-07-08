package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.storage.StorageProvider;
import dev.aurelium.auraskills.common.user.UserState;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;

import java.util.HashMap;
import java.util.Map.Entry;

@CommandAlias("%skills_alias")
@Subcommand("storage")
public class StorageCommand extends BaseCommand {

    private final AuraSkills plugin;

    public StorageCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("mergeskills")
    @CommandPermission("auraskills.command.storage")
    public void onMergeSkills(CommandSender sender, String from, String to) {
        if (!(sender instanceof ConsoleCommandSender) && !(sender instanceof RemoteConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "Only console can execute this command!");
            return;
        }
        int failed = 0;
        int success = 0;
        try {
            StorageProvider storage = plugin.getStorageProvider();
            for (UserState state : storage.loadStates(false, false)) {
                Entry<Skill, Integer> fromEntry = state.skillLevels().entrySet().stream()
                        .filter(e -> e.getKey().getId().toString().equals(from))
                        .findFirst()
                        .orElse(null);
                Entry<Skill, Integer> toEntry = state.skillLevels().entrySet().stream()
                        .filter(e -> e.getKey().getId().toString().equals(to))
                        .findFirst()
                        .orElse(null);
                if (fromEntry == null || toEntry == null) {
                    failed++;
                    continue;
                }
                // Apply the fromEntry value to the "to" skill if greater
                if (fromEntry.getValue() > toEntry.getValue()) {
                    state.skillLevels().put(toEntry.getKey(), fromEntry.getValue());
                    // Apply XP of from skill as well
                    state.skillXp().put(toEntry.getKey(), state.skillXp().getOrDefault(fromEntry.getKey(), 0.0));

                    try {
                        storage.applyState(state);
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + "Failed to apply user state of user " + state.uuid().toString());
                        failed++;
                        continue;
                    }
                }
                success++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage("Merged skill " + from + " into " + to + ", applying the higher skill level. Successfully applied to " + success + " users, failed to apply for " + failed + " users.");
    }

    @Subcommand("removeallmodifiers")
    @CommandPermission("auraskills.command.storage")
    public void removeAllModifiers(CommandSender sender) {
        if (!(sender instanceof ConsoleCommandSender) && !(sender instanceof RemoteConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "Only console can execute this command!");
            return;
        }
        int failed = 0;
        int success = 0;
        int skipped = 0;
        try {
            StorageProvider storage = plugin.getStorageProvider();
            for (UserState state : storage.loadStates(false, false)) {
                if (state.statModifiers().isEmpty() && state.traitModifiers().isEmpty()){
                    skipped++;
                    continue;
                }
                UserState newState = new UserState(state.uuid(), state.skillLevels(), state.skillXp(), new HashMap<>(), new HashMap<>(), state.mana());
                try {
                    storage.applyState(newState);
                    success++;
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Failed to apply user state of user " + state.uuid().toString());
                    failed++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage("Removed all stat and trait modifiers for all users. Successfully applied to " + success + " users, failed to apply for " + failed + " users, " + skipped + " users had no modifiers to remove.");
    }

}
