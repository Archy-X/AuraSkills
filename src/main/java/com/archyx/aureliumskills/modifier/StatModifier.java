package com.archyx.aureliumskills.modifier;

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
        return input.replace("{color}", stat.getColor())
                .replace("{symbol}", stat.getSymbol())
                .replace("{stat}", stat.getDisplayName())
                .replace("{value}", String.valueOf(value))
                .replace("{name}", name);
    }

    public static String applyPlaceholders(String input, StatModifier modifier, Player player) {
        Stat stat = modifier.getStat();
        return input.replace("{color}", stat.getColor())
                .replace("{symbol}", stat.getSymbol())
                .replace("{stat}", stat.getDisplayName())
                .replace("{value}", String.valueOf(modifier.getValue()))
                .replace("{name}", modifier.getName())
                .replace("{player}", player.getName());
    }

    public static String applyPlaceholders(String input, Stat stat, Player player) {
        return input.replace("{color}", stat.getColor())
                .replace("{symbol}", stat.getSymbol())
                .replace("{stat}", stat.getDisplayName())
                .replace("{player}", player.getName());
    }

    public static String applyPlaceholders(String input, Stat stat, int value) {
        return input.replace("{color}", stat.getColor())
                .replace("{symbol}", stat.getSymbol())
                .replace("{stat}", stat.getDisplayName())
                .replace("{value}", String.valueOf(value));
    }

    public static String applyPlaceholders(String input, Stat stat) {
        return input.replace("{color}", stat.getColor())
                .replace("{symbol}", stat.getSymbol())
                .replace("{stat}", stat.getDisplayName());
    }

    public static String applyPlaceholders(String input, String name, Player player) {
        return input.replace("{name}", name)
                .replace("{player}", player.getName());
    }

    public static String applyPlaceholders(String input, Player player) {
        return input.replace("{player}", player.getName());
    }

}
