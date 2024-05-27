package dev.aurelium.auraskills.bukkit.ui;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.ProtocolLibHook;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.ui.ActionBarManager;
import dev.aurelium.auraskills.common.ui.UiProvider;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.slate.text.TextFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.time.Duration;

public class BukkitUiProvider implements UiProvider {

    private final AuraSkills plugin;
    private final ActionBarManager actionBarManager;
    private final BossBarManager bossBarManager;
    private final TextFormatter tf = new TextFormatter();

    public BukkitUiProvider(AuraSkills plugin) {
        this.plugin = plugin;
        this.actionBarManager = new BukkitActionBarManager(plugin, this);
        this.bossBarManager = new BossBarManager(plugin);
        plugin.getServer().getPluginManager().registerEvents(bossBarManager, plugin);
    }

    @Override
    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    @Override
    public NumberFormat getFormat(FormatType type) {
        return switch (type) {
            case XP -> bossBarManager.getXpFormat();
            case PERCENT -> bossBarManager.getPercentFormat();
            case MONEY -> bossBarManager.getMoneyFormat();
        };
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void sendActionBar(User user, String message) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player == null) return;

        if (plugin.getHookManager().isRegistered(ProtocolLibHook.class)) {
            ProtocolLibHook hook = plugin.getHookManager().getHook(ProtocolLibHook.class);
            hook.sendActionBar(player, message);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
    }

    @Override
    public void sendXpBossBar(User user, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed, double income) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player == null) return;
        bossBarManager.sendBossBar(player, skill, currentXp, levelXp, xpGained, level, maxed, income);
    }

    @Override
    public void sendTitle(User user, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Player player = BukkitUser.getPlayer(user.toApi());
        if (player == null) return;

        Component cTitle = tf.toComponent(title);
        Component cSubtitle = tf.toComponent(subtitle);
        int fadeInMs = fadeIn * 50;
        int stayMs = stay * 50;
        int fadeOutMs = fadeOut * 50;
        plugin.getAudiences().player(player).showTitle(Title.title(cTitle, cSubtitle,
                Times.times(Duration.ofMillis(fadeInMs), Duration.ofMillis(stayMs), Duration.ofMillis(fadeOutMs))));
    }
}
