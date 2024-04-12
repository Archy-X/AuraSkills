package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.StatisticXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StatisticLeveler extends SourceLeveler {

    private final Map<UUID, Map<StatisticXpSource, Integer>> tracker = new HashMap<>();

    public StatisticLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.STATISTIC);
        startTracking();
    }

    public void startTracking() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    handlePlayer(player);
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0, plugin.configInt(Option.SOURCE_STATISTIC_GAIN_PERIOD_TICKS) * 50L, TimeUnit.MILLISECONDS);
    }

    private void handlePlayer(Player player) {
        // Disable in spectator mode
        if (player.getGameMode() == GameMode.SPECTATOR) return;

        User user = plugin.getUser(player);

        var sources = plugin.getSkillManager().getSourcesOfType(StatisticXpSource.class);
        for (SkillSource<StatisticXpSource> entry : sources) {
            if (failsChecks(player, player.getLocation(), entry.skill())) return;

            StatisticXpSource source = entry.source();
            try {
                Statistic statistic = Statistic.valueOf(source.getStatistic().toUpperCase(Locale.ROOT));

                // Get the last tracked statistic value and add current value if missing
                int lastValue = tracker.computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>())
                        .computeIfAbsent(source, s -> player.getStatistic(statistic));

                int change = player.getStatistic(statistic) - lastValue;

                // Make sure change in value is at least the minimum
                if (change <= 0) continue;
                if (change < source.getMinimumIncrease()) continue;

                double xpToAdd = change * source.getMultiplier() * source.getXp();

                plugin.getLevelManager().addXp(user, entry.skill(), source, xpToAdd);
                // Update tracker with current value
                tracker.computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>()).put(source, player.getStatistic(statistic));
            } catch (IllegalArgumentException ignored) {}
        }
    }

}
