package dev.aurelium.auraskills.bukkit.stat;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.MessageProvider;
import org.bukkit.entity.Player;

import java.util.Locale;

public class StatFormat {

    private final MessageProvider provider;

    public StatFormat(AuraSkills plugin) {
        this.provider = plugin.getMessageProvider();
    }

    public String applyPlaceholders(String input, StatModifier modifier, Locale locale) {
        Stat stat = modifier.stat();
        double value = modifier.value();
        String name = modifier.name();
        return provider.applyFormatting(input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale, false))
                .replace("{value}", NumberUtil.format1(value))
                .replace("{name}", name));
    }

    public String applyPlaceholders(String input, StatModifier modifier, Player player, Locale locale) {
        Stat stat = modifier.stat();
        return provider.applyFormatting(input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale, false))
                .replace("{value}", NumberUtil.format1(modifier.value()))
                .replace("{name}", modifier.name())
                .replace("{player}", player.getName()));
    }

    public String applyPlaceholders(String input, Stat stat, Player player, Locale locale) {
        return provider.applyFormatting(input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale, false))
                .replace("{player}", player.getName()));
    }

    public String applyPlaceholders(String input, Stat stat, double value, Locale locale) {
        return provider.applyFormatting(input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale, false))
                .replace("{value}", NumberUtil.format1(value)));
    }

    public String applyPlaceholders(String input, Stat stat, Locale locale) {
        return provider.applyFormatting(input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale, false)));
    }

    public String applyPlaceholders(String input, String name, Player player) {
        return input.replace("{name}", name)
                .replace("{player}", player.getName());
    }

    public String applyPlaceholders(String input, Player player) {
        return input.replace("{player}", player.getName());
    }
    
}
