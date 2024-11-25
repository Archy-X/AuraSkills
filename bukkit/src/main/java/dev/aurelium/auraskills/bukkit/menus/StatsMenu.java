package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.bukkit.BukkitTraitHandler;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.CustomStat;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.info.TemplatePlaceholderInfo;
import dev.aurelium.slate.item.provider.ListBuilder;
import dev.aurelium.slate.position.PositionProvider;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class StatsMenu {

    private final AuraSkills plugin;

    public StatsMenu(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void build(MenuBuilder menu) {
        var globalItems = new GlobalItems(plugin);
        menu.item("back", globalItems::back);
        menu.fillItem(globalItems::fill);

        menu.item("skull", item -> {
            item.replace("player", p -> p.player().getName());
            item.replace("entries", p -> {
                User user = plugin.getUser(p.player());
                Locale locale = user.getLocale();
                // Handle each stat entry
                ListBuilder builder = new ListBuilder(p.data().listData());

                for (Stat stat : plugin.getStatManager().getEnabledStats()) {
                    String entry = p.menu().getFormat("player_stat_entry");
                    entry = TextUtil.replace(entry,
                            "{color}", stat.getColor(locale),
                            "{symbol}", stat.getSymbol(locale),
                            "{stat}", stat.getDisplayName(locale, false),
                            "{level}", getDisplayLevel(stat, user));
                    builder.append(entry);
                }
                return builder.build();
            });

            item.modify(i -> {
                if (i.item().getItemMeta() instanceof SkullMeta meta) {
                    meta.setOwningPlayer(Bukkit.getOfflinePlayer(i.player().getUniqueId()));
                    i.item().setItemMeta(meta);
                }
                return i.item();
            });
        });

        menu.template("stat", Stat.class, template -> {
            template.replace("color", p -> p.value().getColor(p.locale()));
            template.replace("stat", p -> p.value().getDisplayName(p.locale(), false));
            template.replace("stat_desc", p -> p.value().getDescription(p.locale(), false));
            template.replace("level", p -> getDisplayLevel(p.value(), plugin.getUser(p.player())));
            template.replace("traits", this::getTraitEntries);

            template.definedContexts(m -> {
                for (Stat context : plugin.getStatManager().getEnabledStats()) {
                    if (!(context instanceof CustomStat stat)) continue;
                    // Handle custom stat and set position provider
                    try {
                        ConfigurateItemParser parser = new ConfigurateItemParser(plugin);
                        ConfigurationNode config = parser.parseItemContext(stat.getDefined().getItem());

                        PositionProvider provider = parser.parsePositionProvider(config, m.menu(), "stat");
                        if (provider != null) {
                            m.menu().setPositionProvider("stat", context, provider);
                        }
                    } catch (SerializationException e) {
                        plugin.logger().warn("Error parsing ItemContext of CustomStat " + stat.getId());
                        e.printStackTrace();
                    }
                }
                return new HashSet<>(plugin.getStatManager().getEnabledStats());
            });

            template.modify(t -> {
                if (t.item() == null && t.value() instanceof CustomStat stat) {
                    try {
                        ConfigurateItemParser parser = new ConfigurateItemParser(plugin);
                        // Get item of API-defined custom stat
                        return parser.parseBaseItem(parser.parseItemContext(stat.getDefined().getItem()));
                    } catch (SerializationException | IllegalArgumentException e) {
                        plugin.logger().warn("Error parsing ItemContext of CustomStat " + stat.getId());
                        e.printStackTrace();
                    }
                }
                return t.item();
            });
        });

        menu.component("leveled_by", Stat.class, component -> {
            component.replace("color", p -> p.value().getColor(p.locale()));

            component.replace("skills", p -> {
                Locale locale = p.locale();
                ListBuilder builder = new ListBuilder(p.data().listData());

                List<Skill> skillsLeveledBy = plugin.getRewardManager().getSkillsLeveledBy(p.value());
                skillsLeveledBy.forEach(s -> builder.append(s.getDisplayName(locale, false)));

                return builder.build();
            });
            component.shouldShow(t -> !plugin.getRewardManager().getSkillsLeveledBy(t.value()).isEmpty());
        });
    }

    private String getTraitEntries(TemplatePlaceholderInfo<Stat> info) {
        Stat stat = info.value();
        User user = plugin.getUser(info.player());
        Locale locale = user.getLocale();
        ListBuilder builder = new ListBuilder(info.data().listData());

        for (Trait trait : stat.getTraits()) {
            if (!trait.isEnabled()) continue;
            BukkitTraitHandler impl = plugin.getTraitManager().getTraitImpl(trait);
            if (impl == null) continue;
            builder.append(info.menu().getFormat("trait_entry"),
                    "{trait}", trait.getDisplayName(locale),
                    "{color}", stat.getColor(locale),
                    "{level}", impl.getMenuDisplay(user.getEffectiveTraitLevel(trait), trait, locale));
        }
        return builder.build();
    }

    private String getDisplayLevel(Stat stat, User user) {
        if (isOneToOneWithTrait(stat) && plugin.configBoolean(Option.MENUS_STATS_SHOW_TRAIT_VALUES_DIRECTLY)) {
            // Displays the trait value directly instead of the stat level if the stat has exactly one trait and its modifier is 1
            Trait trait = stat.getTraits().get(0);
            double value = user.getEffectiveTraitLevel(trait);

            BukkitTraitHandler impl = plugin.getTraitManager().getTraitImpl(trait);
            if (impl != null) {
                return impl.getMenuDisplay(value, trait, user.getLocale());
            } else {
                return NumberUtil.format1(value);
            }
        } else {
            return NumberUtil.format1(user.getStatLevel(stat));
        }
    }

    private boolean isOneToOneWithTrait(Stat stat) {
        if (stat.getTraits().size() > 1) return false;
        return stat.getTraitModifier(stat.getTraits().get(0)) == 1.0;
    }

}
