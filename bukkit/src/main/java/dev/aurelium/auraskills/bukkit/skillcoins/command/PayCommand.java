package dev.aurelium.auraskills.bukkit.skillcoins.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.skillcoins.CurrencyType;
import dev.aurelium.auraskills.common.skillcoins.EconomyProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

/**
 * Pay command - allows players to send coins to other players
 */
@CommandAlias("pay")
public class PayCommand extends BaseCommand {
    
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final double MIN_PAYMENT = 1.0;
    private static final double MAX_PAYMENT = 1000000.0;
    
    private final AuraSkills plugin;
    private final EconomyProvider economy;
    
    public PayCommand(AuraSkills plugin) {
        this.plugin = plugin;
        this.economy = plugin.getSkillCoinsEconomy();
    }
    
    @Default
    @CommandPermission("auraskills.command.pay")
    @CommandCompletion("@players @nothing")
    @Description("Send coins to another player")
    @Syntax("<player> <amount>")
    public void onPay(Player sender, String targetName, double amount) {
        // Validation: Amount
        if (amount < MIN_PAYMENT) {
            sender.sendMessage(ChatColor.of("#FF5555") + "✖ " + ChatColor.of("#FFFFFF") + 
                    "Minimum payment amount is " + ChatColor.of("#FFD700") + 
                    MONEY_FORMAT.format(MIN_PAYMENT) + " Coins");
            sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        if (amount > MAX_PAYMENT) {
            sender.sendMessage(ChatColor.of("#FF5555") + "✖ " + ChatColor.of("#FFFFFF") + 
                    "Maximum payment amount is " + ChatColor.of("#FFD700") + 
                    MONEY_FORMAT.format(MAX_PAYMENT) + " Coins");
            sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Validation: Target player
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(ChatColor.of("#FF5555") + "✖ " + ChatColor.of("#FFFFFF") + 
                    "Player not found! They must be online.");
            sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Validation: Cannot pay yourself
        if (target.getUniqueId().equals(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.of("#FF5555") + "✖ " + ChatColor.of("#FFFFFF") + 
                    "You cannot pay yourself!");
            sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Validation: Sufficient balance
        double senderBalance = economy.getBalance(sender.getUniqueId(), CurrencyType.COINS);
        if (senderBalance < amount) {
            sender.sendMessage(ChatColor.of("#FF5555") + "✖ " + ChatColor.of("#FFFFFF") + 
                    "Insufficient funds! You have " + ChatColor.of("#FFD700") + 
                    MONEY_FORMAT.format(senderBalance) + " Coins");
            sender.sendMessage(ChatColor.of("#808080") + "  Need: " + ChatColor.of("#FF5555") + 
                    MONEY_FORMAT.format(amount - senderBalance) + " more Coins");
            sender.playSound(sender.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        
        // Perform transaction
        economy.subtractBalance(sender.getUniqueId(), CurrencyType.COINS, amount);
        economy.addBalance(target.getUniqueId(), CurrencyType.COINS, amount);
        
        // Success feedback for sender
        sender.sendMessage(ChatColor.of("#55FF55") + "✔ Payment Sent!");
        sender.sendMessage(ChatColor.of("#FFFFFF") + "Sent " + ChatColor.of("#FFD700") + 
                MONEY_FORMAT.format(amount) + " Coins " + ChatColor.of("#FFFFFF") + 
                "to " + ChatColor.of("#00FFFF") + target.getName());
        sender.sendMessage(ChatColor.of("#808080") + "New balance: " + ChatColor.of("#FFFFFF") + 
                MONEY_FORMAT.format(senderBalance - amount) + " Coins");
        sender.playSound(sender.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
        
        // Notification for recipient
        target.sendMessage(ChatColor.of("#55FF55") + "✔ Payment Received!");
        target.sendMessage(ChatColor.of("#FFFFFF") + "You received " + ChatColor.of("#FFD700") + 
                MONEY_FORMAT.format(amount) + " Coins " + ChatColor.of("#FFFFFF") + 
                "from " + ChatColor.of("#00FFFF") + sender.getName());
        double targetBalance = economy.getBalance(target.getUniqueId(), CurrencyType.COINS);
        target.sendMessage(ChatColor.of("#808080") + "New balance: " + ChatColor.of("#FFFFFF") + 
                MONEY_FORMAT.format(targetBalance) + " Coins");
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }
}
