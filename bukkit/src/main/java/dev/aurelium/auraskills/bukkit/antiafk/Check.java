package dev.aurelium.auraskills.bukkit.antiafk;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.parser.ParseException;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Locale;

public class Check implements Listener {

    private final CheckType type;
    private final AntiAfkManager manager;
    private final AuraSkills plugin;
    private final String CONFIG_PREFIX;
    private final Option ENABLED_OPTION;
    private final int LOG_THRESHOLD;

    public Check(CheckType type, AntiAfkManager manager) {
        this.type = type;
        this.manager = manager;
        this.plugin = manager.getPlugin();
        this.CONFIG_PREFIX = "ANTI_AFK_CHECKS_" + type.toString() + "_";
        this.ENABLED_OPTION = Option.valueOf(CONFIG_PREFIX + "ENABLED");
        int minCount = optionInt("min_count");
        int logThresholdParsed;
        try {
            logThresholdParsed = manager.getLogThresholdExpression()
                    .with("min_count", minCount)
                    .evaluate()
                    .getNumberValue()
                    .intValue();
        } catch (EvaluationException | ParseException e) {
            plugin.logger().warn("Failed to evaluate anti_afk.log_threshold expression: " + e.getMessage());
            e.printStackTrace();
            logThresholdParsed = minCount; // Fallback value
        }
        this.LOG_THRESHOLD = logThresholdParsed;
    }

    public int getLogThreshold() {
        return LOG_THRESHOLD;
    }

    protected CheckData getCheckData(Player player) {
        return manager.getCheckData(player, type);
    }

    protected void logFail(Player player) {
        if (!plugin.configBoolean(Option.ANTI_AFK_LOGGING_ENABLED)) return;

        CheckData checkData = getCheckData(player);
        if (checkData.getLogCount() >= LOG_THRESHOLD) {
            manager.logAndNotifyFail(player, type, checkData);
            checkData.resetLogCount();
        }
    }

    protected boolean isDisabled() {
        return !plugin.configBoolean(ENABLED_OPTION);
    }

    protected int optionInt(String option) {
        return plugin.configInt(Option.valueOf(CONFIG_PREFIX + option.toUpperCase(Locale.ROOT)));
    }

    protected double optionDouble(String option) {
        return plugin.configDouble(Option.valueOf(CONFIG_PREFIX + option.toUpperCase(Locale.ROOT)));
    }

    protected String optionString(String option) {
        return plugin.configString(Option.valueOf(CONFIG_PREFIX + option.toUpperCase(Locale.ROOT)));
    }

    protected boolean optionBoolean(String option) {
        return plugin.configBoolean(Option.valueOf(CONFIG_PREFIX + option.toUpperCase(Locale.ROOT)));
    }

}
