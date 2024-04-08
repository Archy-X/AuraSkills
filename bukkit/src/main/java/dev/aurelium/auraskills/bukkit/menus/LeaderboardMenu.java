package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.common.leaderboard.LeaderboardManager;
import dev.aurelium.auraskills.common.leaderboard.SkillValue;
import dev.aurelium.slate.builder.MenuBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeaderboardMenu {

    private final AuraSkills plugin;

    public LeaderboardMenu(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void build(MenuBuilder menu) {
        menu.replaceTitle("skill", p -> ((Skill) p.menu().getProperty("skill")).getDisplayName(p.locale()));

        menu.properties(m -> Map.of(
                "skill", m.menu().getProperty("skill", Skills.FARMING),
                "previous_menu", "level_progression"));

        var globalItems = new GlobalItems(plugin);
        menu.item("back", globalItems::backToLevelProgression);

        menu.template("leaderboard_player", Integer.class, template -> {
            LeaderboardManager lb = plugin.getLeaderboardManager();
            template.replace("place", p -> String.valueOf(p.value()));
            template.replace("player", p -> {
                SkillValue value = lb.getSkillValue((Skill) p.menu().getProperty("skill"), p.value());
                String name = Bukkit.getOfflinePlayer(value != null ? value.id() : UUID.randomUUID()).getName();
                return name != null ? name : "?";
            });
            template.replace("level", p -> {
                SkillValue value = lb.getSkillValue((Skill) p.menu().getProperty("skill"), p.value());
                return String.valueOf(value != null ? value.level() : 0);
            });
            // Places 1-10
            template.definedContexts(m -> IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toSet()));
            // Apply player skin to player head
            template.modify(t -> {
                Skill skill = (Skill) t.menu().getProperty("skill");
                SkillValue value = lb.getSkillValue(skill, t.value());
                if (value == null) return null;
                if (t.item().getItemMeta() instanceof SkullMeta meta) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(value.id());
                    if (offlinePlayer.getName() != null) {
                        meta.setOwningPlayer(offlinePlayer);
                        t.item().setItemMeta(meta);
                    }
                }
                return t.item();
            });
        });
    }
}