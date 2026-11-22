package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.leaderboard.LeaderboardManager;
import dev.aurelium.auraskills.common.leaderboard.SkillValue;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.menu.ActiveMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeaderboardMenu {

    private final AuraSkills plugin;
    private final Map<UUID, PlayerProfile> profileCache = new ConcurrentHashMap<>();

    public LeaderboardMenu(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void build(MenuBuilder menu) {
        menu.replace("skill", p -> ((Skill) p.menu().getProperty("skill")).getDisplayName(p.locale(), false));

        menu.properties(m -> Map.of(
                "skill", m.menu().getProperty("skill", Skills.FARMING),
                "previous_menu", "level_progression"));

        var globalItems = new GlobalItems(plugin);
        menu.item("back", globalItems::backToLevelProgression);
        menu.fillItem(globalItems::fill);

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
                    setSkullTexture(t.menu(), t.item(), meta, value, t.value());
                    t.item().setItemMeta(meta);
                }
                return t.item();
            });
        });
    }

    private void setSkullTexture(ActiveMenu menu, ItemStack item, SkullMeta meta, SkillValue value, Integer place) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(value.id());
        if (!VersionUtils.isAtLeastVersion(18, 1)) {
            meta.setOwningPlayer(offlinePlayer);
            return;
        }

        PlayerProfile playerProfile = offlinePlayer.getPlayerProfile();
        if (playerProfile.isComplete()) {
            if (offlinePlayer.isOnline()) {
                profileCache.remove(value.id());
            }
            meta.setOwnerProfile(playerProfile);
            return;
        }

        PlayerProfile cached = profileCache.get(value.id());
        if (cached != null && cached.isComplete()) {
            meta.setOwnerProfile(cached);
            return;
        }

        playerProfile.update().thenAcceptAsync(profile -> {
            profileCache.put(value.id(), profile);

            meta.setOwnerProfile(profile);
            item.setItemMeta(meta);

            // Update the item in the menu
            plugin.getScheduler().executeSync(() -> menu.replaceBaseItem("leaderboard_player", item, place));
        }, plugin.getScheduler()::executeAsync);
    }

}
