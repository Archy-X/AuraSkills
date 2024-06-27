package dev.aurelium.auraskills.bukkit.mana;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.event.mana.ManaAbilityRefreshEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class TimerCountdown {

    private final AuraSkills plugin;
    private final int PERIOD;

    public TimerCountdown(AuraSkills plugin) {
        this.plugin = plugin;
        this.PERIOD = Math.min(plugin.configInt(Option.MANA_COOLDOWN_TIMER_PERIOD), 1);
        startCountdown();
    }

    public void startCountdown() {
        // Count cooldown by period every period ticks
        plugin.getScheduler().timerSync(new TaskRunnable() {
            @Override
            public void run() {
                countCooldown();
            }
        }, 0, PERIOD * 50L, TimeUnit.MILLISECONDS);
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
                if (cooldown > PERIOD) {
                    data.setCooldown(cooldown - PERIOD);
                } else if (cooldown > 0) {
                    // Cooldown is less than or equal to period
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
