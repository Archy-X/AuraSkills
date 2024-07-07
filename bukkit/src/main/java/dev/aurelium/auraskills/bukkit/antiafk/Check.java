package dev.aurelium.auraskills.bukkit.antiafk;

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

    public Check(CheckType type, AntiAfkManager manager) {
        this.type = type;
        this.manager = manager;
        this.plugin = manager.getPlugin();
        this.CONFIG_PREFIX = "ANTI_AFK_CHECKS_" + type.toString() + "_";
        this.ENABLED_OPTION = Option.valueOf(CONFIG_PREFIX + "ENABLED");
    }

    protected CheckData getCheckData(Player player) {
        return manager.getCheckData(player, type);
    }

    protected void logFail(Player player) {
        manager.logFail(player, type);
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
