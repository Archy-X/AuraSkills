package dev.aurelium.auraskills.paper.util;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

public class AdventureUtil {

    public static void showBossBar(Player player, BossBar bossBar, BukkitAudiences audiences) {
        if (player == null) return;

        if (PaperUtil.IS_PAPER) {
            player.showBossBar(bossBar);
        } else {
            audiences.player(player).showBossBar(bossBar);
        }
    }

    public static void hideBossBar(Player player, BossBar bossBar, BukkitAudiences audiences) {
        if (player == null) return;

        if (PaperUtil.IS_PAPER) {
            player.hideBossBar(bossBar);
        } else {
            audiences.player(player).hideBossBar(bossBar);
        }
    }

    public static void showTitle(Player player, Title title, BukkitAudiences audiences) {
        if (player == null) return;

        if (PaperUtil.IS_PAPER) {
            player.showTitle(title);
        } else {
            audiences.player(player).showTitle(title);
        }
    }

    public static void sendMessage(Player player, Component component, BukkitAudiences audiences) {
        if (player == null) return;

        if (PaperUtil.IS_PAPER) {
            player.sendMessage(component);
        } else {
            audiences.player(player).sendMessage(component);
        }
    }
}
