package dev.aurelium.auraskills.bukkit.skillcoins.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skillcoins.menu.SellMenu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Sell command - opens the sell GUI
 */
@CommandAlias("sell|sellgui")
public class SellCommand extends BaseCommand {
    
    private final AuraSkills plugin;
    private final SellMenu sellMenu;
    
    public SellCommand(AuraSkills plugin) {
        this.plugin = plugin;
        this.sellMenu = new SellMenu(plugin, plugin.getSkillCoinsEconomy());
    }
    
    @Default
    @CommandPermission("auraskills.command.sell")
    @Description("Open the sell menu to quickly sell items")
    public void onSell(Player player) {
        if (plugin.getShopLoader() == null || plugin.getSkillCoinsEconomy() == null) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ Shop system is not loaded!");
            return;
        }
        
        if (plugin.getShopLoader().getSections().isEmpty()) {
            player.sendMessage(ChatColor.of("#FF5555") + "✖ No shop sections loaded!");
            return;
        }
        
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
        sellMenu.open(player);
    }
}
