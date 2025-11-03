package dev.aurelium.auraskills.bukkit.skillcoins.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skillcoins.menu.ShopMainMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("shop")
public class ShopCommand extends BaseCommand {
    
    private final AuraSkills plugin;
    
    public ShopCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }
    
    @Default
    @CommandPermission("auraskills.command.shop")
    @Description("Open the SkillCoins shop")
    public void onShop(Player player) {
        if (plugin.getShopLoader() == null || plugin.getSkillCoinsEconomy() == null) {
            player.sendMessage(ChatColor.RED + "The shop is not available right now.");
            return;
        }
        
        if (plugin.getShopLoader().getSections().isEmpty()) {
            player.sendMessage(ChatColor.RED + "The shop has no sections loaded.");
            return;
        }
        
        new ShopMainMenu(plugin, plugin.getSkillCoinsEconomy()).open(player);
    }
}
