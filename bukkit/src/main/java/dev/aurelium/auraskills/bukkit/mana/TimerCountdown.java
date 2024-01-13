package dev.aurelium.auraskills.bukkit.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.event.mana.ManaAbilityRefreshEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class TimerCountdown {

    private final AuraSkills plugin;

    public TimerCountdown(AuraSkills plugin) {
        this.plugin = plugin;
        startCountdown();
    }

    public void startCountdown() {
        // Count cooldown by 2 every 2 ticks
        plugin.getScheduler().timerSync(new TaskRunnable() {
            @Override
            public void run() {
                countCooldown();
            }
        }, 0, 2 * 50L, TimeUnit.MILLISECONDS);
        // Count error timer by 1 every second
        plugin.getScheduler().timerSync(new TaskRunnable() {
            @Override
            public void run() {
                countErrorTimer();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void countCooldown() {
        for (User user : plugin.getUserManager().getOnlineUsers()) {
            for (ManaAbilityData data : user.getManaAbilityDataMap().values()) {
                int cooldown = data.getCooldown();
                if (cooldown >= 2) {
                    data.setCooldown(cooldown - 2);

                    callRefreshEvent(user, data.getManaAbility());
                } else if (cooldown == 1) {
                    data.setCooldown(0);

                    callRefreshEvent(user, data.getManaAbility());
                }
            }
        }
    }

    private void countErrorTimer() {
        for (User user : plugin.getUserManager().getOnlineUsers()) {
            for (ManaAbilityData data : user.getManaAbilityDataMap().values()) {
                int errorTimer = data.getErrorTimer();
                if (errorTimer > 0) {
                    data.setErrorTimer(errorTimer - 1);
                }
            }
        }
    }

    private void callRefreshEvent(User user, ManaAbility manaAbility) {
        Player player = ((BukkitUser) user).getPlayer();
        if (player != null) {
            ManaAbilityRefreshEvent event = new ManaAbilityRefreshEvent(player, user.toApi(), manaAbility);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

}
