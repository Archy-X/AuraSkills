package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.math.NumberUtil;
import org.bukkit.entity.Player;

import java.util.Locale;

public class StatModifier {

    private final String name;
    private final Stat stat;
    private final double value;

    public StatModifier(String name, Stat stat, double value) {
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

    public double getValue() {
        return value;
    }

    public static String applyPlaceholders(String input, StatModifier modifier, Locale locale) {
        Stat stat = modifier.getStat();
        double value = modifier.getValue();
        String name = modifier.getName();
        return input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale))
                .replace("{value}", NumberUtil.format1(value))
                .replace("{name}", name);
    }

    public static String applyPlaceholders(String input, StatModifier modifier, Player player, Locale locale) {
        Stat stat = modifier.getStat();
        return input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale))
                .replace("{value}", NumberUtil.format1(modifier.getValue()))
                .replace("{name}", modifier.getName())
                .replace("{player}", player.getName());
    }

    public static String applyPlaceholders(String input, Stat stat, Player player, Locale locale) {
        return input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale))
                .replace("{player}", player.getName());
    }

    public static String applyPlaceholders(String input, Stat stat, double value, Locale locale) {
        return input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale))
                .replace("{value}", NumberUtil.format1(value));
    }

    public static String applyPlaceholders(String input, Stat stat, Locale locale) {
        return input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale));
    }

    public static String applyPlaceholders(String input, String name, Player player) {
        return input.replace("{name}", name)
                .replace("{player}", player.getName());
    }

    public static String applyPlaceholders(String input, Player player) {
        return input.replace("{player}", player.getName());
    }

}
