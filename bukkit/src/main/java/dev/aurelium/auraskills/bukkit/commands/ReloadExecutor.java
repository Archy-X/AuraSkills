package dev.aurelium.auraskills.bukkit.commands;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.HologramsHook;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class ReloadExecutor {

    private final AuraSkills plugin;

    public ReloadExecutor(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void reload(CommandSender sender) {
        Locale locale = plugin.getLocale(sender);
        // Load messages
        plugin.getMessageProvider().loadMessages();
        // Load config.yml file
        plugin.config().loadOptions();
        // Load blocked/disabled worlds lists
        plugin.getWorldManager().loadWorlds(plugin.getConfig());
        // Load skills
        plugin.loadSkills();
        plugin.getLevelManager().registerLevelers();
        plugin.getUiProvider().getBossBarManager().loadOptions();
        plugin.getRewardManager().loadRewards();
        plugin.getLootTableManager().loadLootTables();
        // Register default traits
        plugin.getTraitManager().registerTraitImplementations();
        // Load menus
        plugin.getMenuFileManager().generateDefaultFiles();
        plugin.getMenuFileManager().loadMenus();
        plugin.getUiProvider().getActionBarManager().resetActionBars();
        if (plugin.getHookManager().isRegistered(HologramsHook.class)) {
            plugin.getHookManager().getHook(HologramsHook.class).loadColors();
        }

        sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.RELOAD, locale));
    }

}
