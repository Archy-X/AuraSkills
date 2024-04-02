package dev.aurelium.auraskills.bukkit.menus.shared;

import com.archyx.slate.builder.MenuBuilder;
import com.archyx.slate.builder.TemplateBuilder;
import com.archyx.slate.item.provider.ListBuilder;
import com.archyx.slate.menu.ConfigurableMenu;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.util.PlaceholderHelper;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.MessageProvider;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.Replacer;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Locale;

public class SkillItem {

    private final AuraSkills plugin;
    private final PlaceholderHelper helper;

    public SkillItem(AuraSkills plugin) {
        this.plugin = plugin;
        this.helper = new PlaceholderHelper(plugin);
    }

    /**
     * Builds the replacers and modifier for the skill item to the template.
     *
     * @param template the TemplateBuilder
     */
    public void baseSkillItem(TemplateBuilder<Skill> template) {
        MessageProvider msg = plugin.getMessageProvider();
        template.replace("skill", p -> msg.getRaw(msg.getSkillDisplayNameKey(p.value()), p.locale()));
        template.replace("desc", p -> msg.getRaw(msg.getSkillDescriptionKey(p.value()), p.locale()));
        template.replace("level", p -> RomanNumber.toRoman(plugin.getUser(p.player()).getSkillLevel(p.value()), plugin));
        template.replace("skill_click", p -> plugin.getMsg(MenuMessage.SKILL_CLICK, p.locale()));

        template.modify(t -> {
            if (!t.value().isEnabled()) return null;
            if (t.value() instanceof CustomSkill customSkill) {
                try { // Get custom skill API-defined item
                    ConfigurateItemParser parser = new ConfigurateItemParser(plugin);
                    return parser.parseBaseItem(parser.parseItemContext(customSkill.getDefined().getItem()));
                } catch (SerializationException | IllegalArgumentException e) {
                    plugin.logger().warn("Error parsing ItemContext of CustomSkill " + customSkill.getId());
                    e.printStackTrace();
                }
            }
            return t.item();
        });
    }

    /**
     * Builds all components necessary for the skill item into the menu
     *
     * @param menu the MenuBuilder
     */
    public void buildComponents(MenuBuilder menu) {
        statsLeveled(menu);
        abilityLevels(menu);
        manaAbilityInfo(menu);
        progress(menu);
        maxLevel(menu);
    }

    public void statsLeveled(MenuBuilder menu) {
        menu.component("stats_leveled", Skill.class, component -> {
            component.replace("entries", p -> {
                String entry = p.menu().getFormat("stat_leveled_entry");
                var builder = new ListBuilder(p.data().getListData());
                Locale locale = p.locale();

                for (Stat stat : plugin.getRewardManager().getRewardTable(p.value()).getStatsLeveled()) {
                    builder.append(entry, "{color}", stat.getColor(locale), "{stat}", stat.getDisplayName(locale));
                }
                return builder.build();
            });
            component.shouldShow(t -> !plugin.getRewardManager().getRewardTable(t.value()).getStatsLeveled().isEmpty());
        });
    }

    public void abilityLevels(MenuBuilder menu) {
        menu.component("ability_levels", Skill.class, component -> {
            component.replace("entries", p -> {
                Locale locale = p.locale();
                var builder = new ListBuilder(p.data().getListData());

                for (Ability ability : p.value().getAbilities()) {
                    if (!ability.isEnabled()) continue;
                    // Get the format depending on whether ability is unlocked
                    int level = plugin.getUser(p.player()).getAbilityLevel(ability);
                    String entry = level > 0 ? p.menu().getFormat("unlocked_ability_entry") : p.menu().getFormat("locked_ability_entry");
                    builder.append(entry, "{name}", ability.getDisplayName(locale),
                            "{level}", RomanNumber.toRoman(level, plugin),
                            "{info}", TextUtil.replace(ability.getInfo(locale),
                                    "{value}", NumberUtil.format2(ability.getValue(level)),
                                    "{value_2}", NumberUtil.format2(ability.getSecondaryValue(level))));
                }
                return builder.build();
            });
            component.shouldShow(t -> !t.value().getAbilities().isEmpty());
        });
    }

    public void manaAbilityInfo(MenuBuilder menu) {
        menu.component("mana_ability_info", Skill.class, component -> {
            component.replace("name", p -> {
                ManaAbility manaAbility = p.value().getManaAbility();
                return manaAbility != null ? manaAbility.getDisplayName(p.locale()) : null;
            });
            component.replace("level", p -> {
                ManaAbility manaAbility = p.value().getManaAbility();
                if (manaAbility == null) return null;
                return RomanNumber.toRoman(plugin.getUser(p.player()).getManaAbilityLevel(manaAbility), plugin);
            });
            component.replace("entries", p -> {
                ManaAbility manaAbility = p.value().getManaAbility();
                if (manaAbility == null) return null;

                var builder = new ListBuilder(p.data().getListData());
                int level = plugin.getUser(p.player()).getManaAbilityLevel(manaAbility);
                for (String format : getFormatEntries(manaAbility)) {
                    String message = helper.replaceMenuMessages(p.menu().getFormat(format), p.player(), p.menu(), new Replacer());
                    switch (format) {
                        case "duration_entry" ->
                                builder.append(message, "{duration}", NumberUtil.format1(getDuration(manaAbility, level)));
                        case "mana_cost_entry", "max_mana_cost_entry" -> {
                            if (plugin.configBoolean(Option.MANA_ENABLED)) {
                                builder.append(message, "{mana_cost}", NumberUtil.format1(manaAbility.getManaCost(level)));
                            }
                        }
                        case "cooldown_entry" ->
                                builder.append(message, "{cooldown}", NumberUtil.format1(manaAbility.getCooldown(level)));
                        case "damage_entry", "damage_per_mana_entry", "attack_speed_entry" ->
                                builder.append(message, "{value}", NumberUtil.format1(manaAbility.getValue(level)));
                    }
                }
                return builder.build();
            });
            component.shouldShow(t -> {
                ManaAbility manaAbility = t.value().getManaAbility();
                return manaAbility != null && manaAbility.isEnabled() && plugin.getUser(t.player()).getManaAbilityLevel(manaAbility) > 0;
            });
        });
    }

    public void progress(MenuBuilder menu) {
        menu.component("progress", Skill.class, component -> {
            component.replaceAny(p -> {
                User user = plugin.getUser(p.player());
                int skillLevel = user.getSkillLevel(p.value());
                double currentXp = user.getSkillXp(p.value());
                double xpToNext = plugin.getXpRequirements().getXpRequired(p.value(), skillLevel + 1);

                return switch (p.placeholder()) {
                    case "next_level" -> RomanNumber.toRoman(skillLevel + 1, plugin);
                    case "percent" -> NumberUtil.format2(currentXp / xpToNext * 100);
                    case "current_xp" -> NumberUtil.format2(currentXp);
                    case "level_xp" -> String.valueOf((int) xpToNext);
                    case "bar" -> getBar(plugin, currentXp, xpToNext);
                    default -> null;
                };
            });
            component.shouldShow(t -> t.value().getMaxLevel() > plugin.getUser(t.player()).getSkillLevel(t.value()));
        });
    }

    public void maxLevel(MenuBuilder menu) {
        menu.component("max_level", Skill.class, component ->
                component.shouldShow(t -> t.value().getMaxLevel() == plugin.getUser(t.player()).getSkillLevel(t.value())));
    }

    public static String getBar(AuraSkills plugin, double currentXp, double xpToNext) {
        ConfigurableMenu menu = plugin.getMenuManager().getMenu("skills");
        if (menu == null) return "";

        int length = (int) menu.getOptions().getOrDefault("bar_length", 10);
        double progress = Math.min(currentXp / xpToNext, 1.0);
        int currentPos = (int) Math.round(progress * (length - 1));

        StringBuilder bar = new StringBuilder();
        // Completed portion
        String completed = menu.getFormats().getOrDefault("bar_completed", "<green>■");
        bar.append(completed.repeat(Math.max(0, currentPos)));
        // Current
        bar.append(menu.getFormats().getOrDefault("bar_current", "<yellow>■"));
        // Remaining
        String remaining = menu.getFormats().getOrDefault("bar_remaining", "<gray>■");
        bar.append(remaining.repeat(Math.max(0, length - currentPos - 1)));
        return bar.toString();
    }

    private double getDuration(ManaAbility manaAbility, int level) {
        if (manaAbility.equals(ManaAbilities.LIGHTNING_BLADE)) {
            double baseDuration = ManaAbilities.LIGHTNING_BLADE.optionDouble("base_duration");
            double durationPerLevel = ManaAbilities.LIGHTNING_BLADE.optionDouble("duration_per_level");
            return baseDuration + (durationPerLevel * (level - 1));
        } else {
            return manaAbility.getValue(level);
        }
    }

    private List<String> getFormatEntries(ManaAbility manaAbility) {
        if (manaAbility.equals(ManaAbilities.SHARP_HOOK)) {
            return List.of("damage_entry", "mana_cost_entry", "cooldown_entry");
        } else if (manaAbility.equals(ManaAbilities.CHARGED_SHOT)) {
            return List.of("damage_per_mana_entry", "max_mana_cost_entry");
        } else if (manaAbility.equals(ManaAbilities.LIGHTNING_BLADE)) {
            return List.of("attack_speed_entry", "duration_entry", "mana_cost_entry", "cooldown_entry");
        } else {
            return List.of("duration_entry", "mana_cost_entry", "cooldown_entry");
        }
    }

}
