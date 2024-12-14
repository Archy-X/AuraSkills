package dev.aurelium.auraskills.bukkit.commands;

import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.HologramsHook;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import dev.aurelium.auraskills.bukkit.trait.AnvilDiscountTrait;
import dev.aurelium.auraskills.bukkit.trait.DamageReductionTrait;
import dev.aurelium.auraskills.bukkit.trait.HpTrait;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        plugin.getMessageProvider().setACFMessages(plugin.getCommandManager());
        // Load config.yml file
        plugin.config().loadOptions();
        plugin.getMessageProvider().loadDefaultLanguageOption();
        plugin.getRequirementManager().load();
        // Load blocked/disabled worlds lists
        plugin.getWorldManager().loadWorlds();
        // Load skills
        plugin.loadSkills();
        plugin.getLevelManager().loadXpRequirements();
        plugin.getUiProvider().getBossBarManager().loadOptions();
        plugin.getRewardManager().loadRewards();
        plugin.getLootTableManager().loadLootTables();
        plugin.getTraitManager().getTraitImpl(DamageReductionTrait.class).resetFormula();
        plugin.getTraitManager().getTraitImpl(AnvilDiscountTrait.class).resetFormula();
        plugin.getLevelManager().getLeveler(BlockLeveler.class).clearSourceCache();
        // Load menus
        plugin.getMenuFileManager().generateDefaultFiles();
        plugin.getMenuFileManager().loadMenus();
        plugin.getUiProvider().getActionBarManager().resetActionBars();
        plugin.getAntiAfkManager().reload();
        if (plugin.getHookManager().isRegistered(HologramsHook.class)) {
            plugin.getHookManager().getHook(HologramsHook.class).loadConfig();
        }
        reloadPlayers();
        sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.RELOAD, locale));
    }

    private void reloadPlayers() {
        HpTrait hpTrait = plugin.getTraitManager().getTraitImpl(HpTrait.class);
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = plugin.getUser(player);
            plugin.getStatManager().updateStats(user);
            hpTrait.reload(player, Traits.HP); // Recalculate player HP
        }
    }

}
