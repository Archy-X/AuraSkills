package dev.aurelium.auraskills.bukkit.stat;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier;
import dev.aurelium.auraskills.api.util.AuraSkillsModifier.Operation;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.message.MessageProvider;
import org.bukkit.entity.Player;

import java.util.Locale;

public class StatFormat {

    private final AuraSkills plugin;
    private final MessageProvider provider;

    public StatFormat(AuraSkills plugin) {
        this.plugin = plugin;
        this.provider = plugin.getMessageProvider();
    }

    public String applyPlaceholders(String input, StatModifier modifier, Locale locale) {
        Stat stat = modifier.stat();
        String name = modifier.name();
        return provider.applyFormatting(input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale, false))
                .replace("{value}", formatValue(modifier))
                .replace("{name}", name));
    }

    public String applyPlaceholders(String input, TraitModifier modifier, Locale locale) {
        Trait trait = modifier.trait();
        String name = modifier.name();
        Stat stat = plugin.getTraitManager().getLinkedStats(trait).stream().findFirst().orElse(null);
        return provider.applyFormatting(input.replace("{color}", stat != null ? stat.getColor(locale) : "")
                .replace("{symbol}", "")
                .replace("{stat}", trait.getDisplayName(locale, false))
                .replace("{value}", formatValue(modifier))
                .replace("{name}", name));
    }

    public String applyPlaceholders(String input, StatModifier modifier, Player player, Locale locale) {
        Stat stat = modifier.stat();
        return provider.applyFormatting(input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale, false))
                .replace("{value}", formatValue(modifier))
                .replace("{name}", modifier.name())
                .replace("{operation}", modifier.operation().toString())
                .replace("{player}", player.getName()));
    }

    public String applyPlaceholders(String input, TraitModifier modifier, Player player, Locale locale) {
        Trait trait = modifier.trait();
        Stat stat = plugin.getTraitManager().getLinkedStats(trait).stream().findFirst().orElse(null);
        return provider.applyFormatting(input.replace("{color}", stat != null ? stat.getColor(locale) : "")
                .replace("{symbol}", "")
                .replace("{stat}", trait.getDisplayName(locale, false))
                .replace("{trait}", trait.getDisplayName(locale, false))
                .replace("{value}", formatValue(modifier))
                .replace("{name}", modifier.name())
                .replace("{player}", player.getName()));
    }

    private String formatValue(AuraSkillsModifier<?> modifier) {
        return switch (modifier.operation()) {
            case ADD -> (modifier.value() >= 0.0 ? "+" : "") + NumberUtil.format2(modifier.value());
            case ADD_PERCENT -> (modifier.value() >= 0.0 ? "+" : "") + NumberUtil.format2(modifier.value()) + "%";
            case MULTIPLY -> NumberUtil.format2(modifier.value()) + "x";
        };
    }

    public String applyPlaceholders(String input, Stat stat, Player player, Locale locale) {
        return provider.applyFormatting(input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale, false))
                .replace("{player}", player.getName()));
    }

    public String applyPlaceholders(String input, Trait trait, Player player, Locale locale) {
        Stat stat = plugin.getTraitManager().getLinkedStats(trait).stream().findFirst().orElse(null);
        return provider.applyFormatting(input.replace("{color}", stat != null ? stat.getColor(locale) : "")
                .replace("{symbol}", "")
                .replace("{stat}", trait.getDisplayName(locale, false))
                .replace("{trait}", trait.getDisplayName(locale, false))
                .replace("{player}", player.getName()));
    }

    public String applyPlaceholders(String input, Stat stat, double value, Operation operation, Locale locale) {
        return provider.applyFormatting(input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale, false))
                .replace("{value}", NumberUtil.format1(value))
                .replace("{operation}", operation.toString()));
    }

    public String applyPlaceholders(String input, Trait trait, double value, Operation operation, Locale locale) {
        Stat stat = plugin.getTraitManager().getLinkedStats(trait).stream().findFirst().orElse(null);
        return provider.applyFormatting(input.replace("{color}", stat != null ? stat.getColor(locale) : "")
                .replace("{symbol}", "")
                .replace("{trait}", trait.getDisplayName(locale, false))
                .replace("{value}", NumberUtil.format1(value))
                .replace("{operation}", operation.toString()));
    }

    public String applyPlaceholders(String input, Stat stat, Locale locale) {
        return provider.applyFormatting(input.replace("{color}", stat.getColor(locale))
                .replace("{symbol}", stat.getSymbol(locale))
                .replace("{stat}", stat.getDisplayName(locale, false)));
    }

    public String applyPlaceholders(String input, Trait trait, Locale locale) {
        Stat stat = plugin.getTraitManager().getLinkedStats(trait).stream().findFirst().orElse(null);
        return provider.applyFormatting(input.replace("{color}", stat != null ? stat.getColor(locale) : "")
                .replace("{symbol}", "")
                .replace("{stat}", trait.getDisplayName(locale, false))
                .replace("{trait}", trait.getDisplayName(locale, false)));
    }

    public String applyPlaceholders(String input, String name, Player player) {
        return input.replace("{name}", name)
                .replace("{player}", player.getName());
    }

    public String applyPlaceholders(String input, Player player) {
        return input.replace("{player}", player.getName());
    }

}
