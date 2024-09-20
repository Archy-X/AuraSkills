package dev.aurelium.auraskills.bukkit.menus.shared;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.CustomManaAbility;
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
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.Replacer;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.builder.TemplateBuilder;
import dev.aurelium.slate.info.TemplateInfo;
import dev.aurelium.slate.item.provider.ListBuilder;
import dev.aurelium.slate.menu.LoadedMenu;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        template.replace("skill", p -> p.value().getDisplayName(p.locale(), false));
        template.replace("desc", p -> p.value().getDescription(p.locale(), false));
        template.replace("level", p -> RomanNumber.toRoman(plugin.getUser(p.player()).getSkillLevel(p.value()), plugin));
        template.replace("skill_click", p -> plugin.getMsg(MenuMessage.SKILL_CLICK, p.locale()));

        template.modify(t -> {
            if (!t.value().isEnabled()) return null;
            ItemStack item = t.item();
            if (t.value() instanceof CustomSkill customSkill) {
                try { // Get custom skill API-defined item
                    ConfigurateItemParser parser = new ConfigurateItemParser(plugin);
                    item = parser.parseBaseItem(parser.parseItemContext(customSkill.getDefined().getItem()));
                } catch (SerializationException | IllegalArgumentException e) {
                    plugin.logger().warn("Error parsing ItemContext of CustomSkill " + customSkill.getId());
                    e.printStackTrace();
                }
            }
            addSelectedJobGlint(item, t);
            return item;
        });
    }

    private void addSelectedJobGlint(ItemStack item, TemplateInfo<Skill> t) {
        if (!t.menu().getName().equals("skills")) return; // Don't apply to level progression menu
        if (!plugin.config().jobSelectionEnabled()) return;

        // Show enchant glint if selected as job
        User user = plugin.getUser(t.player());
        Skill skill = t.value();
        if (user.getJobs().contains(skill)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.addEnchant(Enchantment.MENDING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }
        }
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
        skillJobActive(menu);
    }

    private void skillJobActive(MenuBuilder menu) {
        menu.component("skill_job_active", Skill.class, component ->
                component.shouldShow(t -> plugin.config().jobSelectionEnabled() && plugin.getUser(t.player()).getJobs().contains(t.value())));
    }

    public void statsLeveled(MenuBuilder menu) {
        menu.component("stats_leveled", Skill.class, component -> {
            component.replace("entries", p -> {
                String entry = p.menu().getFormat("stat_leveled_entry");
                var builder = new ListBuilder(p.data().listData());
                Locale locale = p.locale();

                for (Stat stat : plugin.getRewardManager().getRewardTable(p.value()).getStatsLeveled()) {
                    builder.append(entry, "{color}", stat.getColor(locale), "{stat}", stat.getDisplayName(locale, false));
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
                var builder = new ListBuilder(p.data().listData());

                for (Ability ability : p.value().getAbilities()) {
                    if (!ability.isEnabled()) continue;
                    // Get the format depending on whether ability is unlocked
                    int level = plugin.getUser(p.player()).getAbilityLevel(ability);
                    String entry = level > 0 ? p.menu().getFormat("unlocked_ability_entry") : p.menu().getFormat("locked_ability_entry");
                    builder.append(entry, "{name}", ability.getDisplayName(locale, false),
                            "{level}", RomanNumber.toRoman(level, plugin),
                            "{info}", TextUtil.replace(ability.getInfo(locale, false),
                                    "{value}", NumberUtil.format2(ability.getValue(level)),
                                    "{value_2}", NumberUtil.format2(ability.getSecondaryValue(level)),
                                    "{chance_value}", plugin.getAbilityManager().getChanceValue(ability, level),
                                    "{guaranteed_value}", plugin.getAbilityManager().getGuaranteedValue(ability, level)));
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
                return manaAbility != null ? manaAbility.getDisplayName(p.locale(), false) : null;
            });
            component.replace("level", p -> {
                ManaAbility manaAbility = p.value().getManaAbility();
                if (manaAbility == null) return null;
                return RomanNumber.toRoman(plugin.getUser(p.player()).getManaAbilityLevel(manaAbility), plugin);
            });
            component.replace("entries", p -> {
                ManaAbility manaAbility = p.value().getManaAbility();
                if (manaAbility == null) return null;

                var builder = new ListBuilder(p.data().listData());
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
                        default ->
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
        LoadedMenu menu = plugin.getSlate().getLoadedMenu("skills");
        if (menu == null) return "";

        int length = (int) menu.options().getOrDefault("bar_length", 10);
        double progress = Math.min(currentXp / xpToNext, 1.0);
        int currentPos = (int) Math.round(progress * (length - 1));

        StringBuilder bar = new StringBuilder();
        // Completed portion
        String completed = menu.formats().getOrDefault("bar_completed", "<green>■");
        bar.append(completed.repeat(Math.max(0, currentPos)));
        // Current
        bar.append(menu.formats().getOrDefault("bar_current", "<yellow>■"));
        // Remaining
        String remaining = menu.formats().getOrDefault("bar_remaining", "<gray>■");
        bar.append(remaining.repeat(Math.max(0, length - currentPos - 1)));
        return bar.toString();
    }

    private double getDuration(ManaAbility manaAbility, int level) {
        if (manaAbility.equals(ManaAbilities.LIGHTNING_BLADE)) {
            double baseDuration = ManaAbilities.LIGHTNING_BLADE.optionDouble("base_duration", 5.0);
            double durationPerLevel = ManaAbilities.LIGHTNING_BLADE.optionDouble("duration_per_level", 4.0);
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
        } else if (manaAbility instanceof CustomManaAbility custom && !custom.getInfoFormats().isEmpty()) {
            return custom.getInfoFormats();
        } else {
            return List.of("duration_entry", "mana_cost_entry", "cooldown_entry");
        }
    }

}
