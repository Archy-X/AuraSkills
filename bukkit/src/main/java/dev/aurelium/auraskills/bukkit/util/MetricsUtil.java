package dev.aurelium.auraskills.bukkit.util;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider;
import dev.aurelium.auraskills.common.storage.sql.pool.MySqlConnectionPool;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public class MetricsUtil {

    private final AuraSkills plugin;

    public MetricsUtil(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void registerCustomCharts(Metrics metrics) {
        metrics.addCustomChart(new SimplePie("skills_enabled", () ->
                String.valueOf(plugin.getSkillManager().getEnabledSkills().size())));
        metrics.addCustomChart(new SimplePie("default_language", () ->
                plugin.configString(Option.DEFAULT_LANGUAGE)));
        metrics.addCustomChart(new SimplePie("storage_type", () -> {
            String type = "yaml";
            if (plugin.getStorageProvider() instanceof SqlStorageProvider sql) {
                if (sql.getPool() instanceof MySqlConnectionPool) {
                    type = "mysql";
                }
            }
            return type;
        }));
    }

}
