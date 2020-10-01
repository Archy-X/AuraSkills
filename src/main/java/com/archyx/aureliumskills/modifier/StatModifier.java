package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.Message;
import com.archyx.aureliumskills.stats.Stat;
import org.bukkit.entity.Player;

public class StatModifier {

    private final String name;
    private final Stat stat;
    private final int value;

    public StatModifier(String name, Stat stat, int value) {
        this.name = name;
        this.stat = stat;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Stat getStat() {
        return stat;
    }

    public int getValue() {
        return value;
    }

    public static String applyPlaceholders(String input, StatModifier modifier) {
        Stat stat = modifier.getStat();
        int value = modifier.getValue();
        String name = modifier.getName();
        return input.replace("$stat_color$", stat.getColor())
                .replace("$stat_symbol$", stat.getSymbol())
                .replace("$stat_name$", Lang.getMessage(Message.valueOf(stat.name().toUpperCase() + "_NAME")))
                .replace("$modifier_value$", String.valueOf(value))
                .replace("$modifier_name$", name)
                .replace("&", "§");
    }

    public static String applyPlaceholders(String input, StatModifier modifier, Player player) {
        Stat stat = modifier.getStat();
        int value = modifier.getValue();
        String name = modifier.getName();
        return input.replace("$stat_color$", stat.getColor())
                .replace("$stat_symbol$", stat.getSymbol())
                .replace("$stat_name$", Lang.getMessage(Message.valueOf(stat.name().toUpperCase() + "_NAME")))
                .replace("$modifier_value$", String.valueOf(value))
                .replace("$modifier_name$", name)
                .replace("$player_name$", player.getName())
                .replace("&", "§");
    }

    public static String applyPlaceholders(String input, Stat stat, Player player) {
        return input.replace("$stat_color$", stat.getColor())
                .replace("$stat_symbol$", stat.getSymbol())
                .replace("$stat_name$", Lang.getMessage(Message.valueOf(stat.name().toUpperCase() + "_NAME")))
                .replace("$player_name$", player.getName())
                .replace("&", "§");
    }

    public static String applyPlaceholders(String input, Stat stat, int value) {
        return input.replace("$stat_color$", stat.getColor())
                .replace("$stat_symbol$", stat.getSymbol())
                .replace("$stat_name$", Lang.getMessage(Message.valueOf(stat.name().toUpperCase() + "_NAME")))
                .replace("$modifier_value$", String.valueOf(value))
                .replace("&", "§");
    }

    public static String applyPlaceholders(String input, Stat stat) {
        return input.replace("$stat_color$", stat.getColor())
                .replace("$stat_symbol$", stat.getSymbol())
                .replace("$stat_name$", Lang.getMessage(Message.valueOf(stat.name().toUpperCase() + "_NAME")))
                .replace("&", "§");
    }

    public static String applyPlaceholders(String input, String name, Player player) {
        return input.replace("$modifier_name$", name)
                .replace("$player_name$", player.getName())
                .replace("&", "§");
    }

    public static String applyPlaceholders(String input, Player player) {
        return input.replace("$player_name$", player.getName())
                .replace("&", "§");
    }

}
