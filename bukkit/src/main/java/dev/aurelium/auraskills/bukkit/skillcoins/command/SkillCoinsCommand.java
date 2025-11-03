package dev.aurelium.auraskills.bukkit.skillcoins.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("skillcoins|sc")
public class SkillCoinsCommand extends BaseCommand {
    
    private final AuraSkills plugin;
    
    public SkillCoinsCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }
    
    @Subcommand("balance|bal")
    @CommandPermission("auraskills.command.skillcoins.balance")
    @Description("Check your SkillCoins balance")
    public void onBalance(Player player) {
        double coins = plugin.getSkillCoinsEconomy().getBalance(player.getUniqueId(), CurrencyType.COINS);
        double tokens = plugin.getSkillCoinsEconomy().getBalance(player.getUniqueId(), CurrencyType.TOKENS);
        
        player.sendMessage(ChatColor.GOLD + "━━━━━━━ " + ChatColor.YELLOW + "Your Balance" + ChatColor.GOLD + " ━━━━━━━");
        player.sendMessage(ChatColor.YELLOW + "SkillCoins: " + ChatColor.WHITE + String.format("%.2f", coins));
        player.sendMessage(ChatColor.AQUA + "SkillTokens: " + ChatColor.WHITE + String.format("%.2f", tokens));
        player.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    @Subcommand("give")
    @CommandPermission("auraskills.command.skillcoins.give")
    @CommandCompletion("@players coins|tokens @nothing")
    @Description("Give SkillCoins or Tokens to a player")
    @Syntax("<player> <coins|tokens> <amount>")
    public void onGive(CommandSender sender, String playerName, String currencyStr, double amount) {
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be positive!");
            return;
        }
        
        CurrencyType type;
        try {
            type = CurrencyType.valueOf(currencyStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid currency type! Use 'coins' or 'tokens'.");
            return;
        }
        
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        
        plugin.getSkillCoinsEconomy().addBalance(target.getUniqueId(), type, amount);
        sender.sendMessage(ChatColor.GREEN + "Gave " + String.format("%.2f", amount) + " " + 
                type.getDisplayName() + " to " + playerName);
        
        if (target.isOnline()) {
            target.getPlayer().sendMessage(ChatColor.GREEN + "You received " + String.format("%.2f", amount) + 
                    " " + type.getDisplayName() + "!");
        }
    }
    
    @Subcommand("take")
    @CommandPermission("auraskills.command.skillcoins.take")
    @CommandCompletion("@players coins|tokens @nothing")
    @Description("Take SkillCoins or Tokens from a player")
    @Syntax("<player> <coins|tokens> <amount>")
    public void onTake(CommandSender sender, String playerName, String currencyStr, double amount) {
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be positive!");
            return;
        }
        
        CurrencyType type;
        try {
            type = CurrencyType.valueOf(currencyStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid currency type! Use 'coins' or 'tokens'.");
            return;
        }
        
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        
        plugin.getSkillCoinsEconomy().subtractBalance(target.getUniqueId(), type, amount);
        sender.sendMessage(ChatColor.GREEN + "Took " + String.format("%.2f", amount) + " " + 
                type.getDisplayName() + " from " + playerName);
        
        if (target.isOnline()) {
            target.getPlayer().sendMessage(ChatColor.YELLOW + "You lost " + String.format("%.2f", amount) + 
                    " " + type.getDisplayName() + "!");
        }
    }
    
    @Subcommand("set")
    @CommandPermission("auraskills.command.skillcoins.set")
    @CommandCompletion("@players coins|tokens @nothing")
    @Description("Set a player's SkillCoins or Tokens balance")
    @Syntax("<player> <coins|tokens> <amount>")
    public void onSet(CommandSender sender, String playerName, String currencyStr, double amount) {
        if (amount < 0) {
            sender.sendMessage(ChatColor.RED + "Amount cannot be negative!");
            return;
        }
        
        CurrencyType type;
        try {
            type = CurrencyType.valueOf(currencyStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid currency type! Use 'coins' or 'tokens'.");
            return;
        }
        
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        
        plugin.getSkillCoinsEconomy().setBalance(target.getUniqueId(), type, amount);
        sender.sendMessage(ChatColor.GREEN + "Set " + playerName + "'s " + type.getDisplayName() + 
                " balance to " + String.format("%.2f", amount));
    }
    
    @Subcommand("check")
    @CommandPermission("auraskills.command.skillcoins.check")
    @CommandCompletion("@players")
    @Description("Check another player's balance")
    @Syntax("<player>")
    public void onCheck(CommandSender sender, String playerName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        
        double coins = plugin.getSkillCoinsEconomy().getBalance(target.getUniqueId(), CurrencyType.COINS);
        double tokens = plugin.getSkillCoinsEconomy().getBalance(target.getUniqueId(), CurrencyType.TOKENS);
        
        sender.sendMessage(ChatColor.GOLD + "━━━━━━━ " + ChatColor.YELLOW + playerName + "'s Balance" + ChatColor.GOLD + " ━━━━━━━");
        sender.sendMessage(ChatColor.YELLOW + "SkillCoins: " + ChatColor.WHITE + String.format("%.2f", coins));
        sender.sendMessage(ChatColor.AQUA + "SkillTokens: " + ChatColor.WHITE + String.format("%.2f", tokens));
        sender.sendMessage(ChatColor.GOLD + "━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
