package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.skill.CustomSkill;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.bukkit.menus.shared.SkillItem;
import dev.aurelium.auraskills.bukkit.menus.util.LevelProgressionOpener;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.position.PositionProvider;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashSet;
import java.util.Map;

public class SkillsMenu {

    private final AuraSkills plugin;

    public SkillsMenu(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void build(MenuBuilder menu) {
        menu.defaultOptions(Map.of("bar_length", 20));

        var globalItems = new GlobalItems(plugin);
        menu.item("close", globalItems::close); // Close item
        menu.fillItem(globalItems::fill);

        var skillItem = new SkillItem(plugin);
        skillItem.buildComponents(menu);

        menu.item("your_skills", item -> item.replace("player", p -> p.player().getName()));
        menu.item("stats", item -> {
            item.onClick(c -> plugin.getSlate().openMenu(c.player(), "stats", Map.of("previous_menu", "skills")));
            item.modify(i -> {
                if (i.item().getItemMeta() instanceof SkullMeta meta) {
                    meta.setOwningPlayer(Bukkit.getOfflinePlayer(i.player().getUniqueId()));
                    i.item().setItemMeta(meta);
                }
                return i.item();
            });
        });

        menu.template("skill", Skill.class, template -> {
            // Sets text replacements and modifier
            skillItem.baseSkillItem(template);

            template.onClick(c -> {
                User user = plugin.getUser(c.player());
                if (user.hasSkillPermission(c.value())) {
                    new LevelProgressionOpener(plugin).open(c.player(), c.value());
                }
            });

            template.definedContexts(m -> {
                for (Skill context : plugin.getSkillManager().getEnabledSkills()) {
                    if (!(context instanceof CustomSkill skill)) continue;
                    try {
                        ConfigurateItemParser parser = new ConfigurateItemParser(plugin);
                        ConfigurationNode config = parser.parseItemContext(skill.getDefined().getItem());

                        PositionProvider provider = parser.parsePositionProvider(config, m.menu(), "skill");
                        if (provider != null) {
                            m.menu().setPositionProvider("skill", context, provider);
                        }
                    } catch (SerializationException e) {
                        plugin.logger().warn("Error parsing ItemContext of CustomSkill " + skill.getId());
                        e.printStackTrace();
                    }
                }
                return new HashSet<>(plugin.getSkillManager().getEnabledSkills());
            });
        });
    }

}
