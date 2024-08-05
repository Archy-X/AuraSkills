package dev.aurelium.auraskills.bukkit.user;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.antiafk.CheckData;
import dev.aurelium.auraskills.bukkit.antiafk.CheckType;
import dev.aurelium.auraskills.bukkit.hooks.BukkitLuckPermsHook;
import dev.aurelium.auraskills.bukkit.skills.agility.AgilityAbilities;
import dev.aurelium.auraskills.common.api.implementation.ApiSkillsUser;
import dev.aurelium.auraskills.common.user.User;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BukkitUser extends User {

    @Nullable
    private final Player player;
    private final AuraSkills plugin;
    // Non-persistent data
    private final Map<CheckType, CheckData> checkData;

    public BukkitUser(UUID uuid, @Nullable Player player, AuraSkills plugin) {
        super(uuid, plugin);
        this.player = player;
        this.plugin = plugin;
        this.checkData = new HashMap<>();
    }

    @Nullable
    public static Player getPlayer(SkillsUser skillsUser) {
        return ((BukkitUser) ((ApiSkillsUser) skillsUser).getUser()).getPlayer();
    }

    public static BukkitUser getUser(SkillsUser skillsUser) {
        return (BukkitUser) ((ApiSkillsUser) skillsUser).getUser();
    }

    @NotNull
    public CheckData getCheckData(CheckType type) {
        return checkData.computeIfAbsent(type, t -> new CheckData());
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    @Override
    public String getUsername() {
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        return name != null ? name : "?";
    }

    @Override
    public double getPermissionMultiplier(@Nullable Skill skill) {
        if (player == null) {
            return 0.0;
        }
        double multiplier = 0.0;

        if (plugin.getHookManager().isRegistered(BukkitLuckPermsHook.class)
                && plugin.getHookManager().getHook(BukkitLuckPermsHook.class).usePermissionCache()) {
            Set<String> permissions = plugin.getHookManager().getHook(BukkitLuckPermsHook.class).getMultiplierPermissions(player);
            for (String permission : permissions) {
                multiplier += getMultiplierFromPermission(permission, skill);
            }
            return multiplier;
        } else {
            for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
                if (!permission.getValue()) continue;
                multiplier += getMultiplierFromPermission(permission.getPermission(), skill);
            }
        }

        return multiplier;
    }

    private double getMultiplierFromPermission(String permission, @Nullable Skill skill) {
        final String prefix = "auraskills.multiplier.";
        if (!permission.startsWith(prefix)) {
            return 0.0;
        }

        permission = permission.substring(prefix.length());

        if (isNumeric(permission)) {
            return Double.parseDouble(permission) / 100.0;
        }

        if (skill != null) {
            String namespacedName = skill.toString().toLowerCase(Locale.ROOT) + ".";
            String plainName = skill.name().toLowerCase(Locale.ROOT) + ".";

            if (permission.startsWith(namespacedName)) {
                permission = permission.substring(namespacedName.length());
            } else if (permission.startsWith(plainName)) {
                permission = permission.substring(plainName.length());
            } else {
                return 0.0;
            }

            if (isNumeric(permission)) {
                return Double.parseDouble(permission) / 100.0;
            }
        }

        return 0.0;
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        boolean decimalSeen = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i == 0 && c == '-') {
                if (str.length() == 1) return false; // "-" alone is not a number
                continue;
            }
            if (c == '.') {
                if (decimalSeen || i == 0 || i == str.length() - 1)
                    return false; // Double decimal or leading/trailing decimal
                decimalSeen = true;
            } else if (c < '0' || c > '9') {
                return false; // Non-digit character
            }
        }
        return true;
    }

    @Override
    public boolean hasSkillPermission(Skill skill) {
        if (player == null) return true;

        return player.hasPermission("auraskills.skill." + skill.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public void setCommandLocale(Locale locale) {
        if (player != null) {
            plugin.getCommandManager().setPlayerLocale(player, locale);
        }
    }

    @Override
    public int getPermissionJobLimit() {
        if (player == null) return 0;

        final String prefix = "auraskills.jobs.limit.";
        int highestLimit = 0;

        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            String permission = permissionInfo.getPermission();

            if (!permission.startsWith(prefix)) continue;

            permission = permission.substring(prefix.length());

            if (isNumeric(permission)) {
                try {
                    int value = Integer.parseInt(permission);
                    if (value > highestLimit) {
                        highestLimit = value;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return highestLimit;
    }

    @Override
    public boolean canSelectJob(@NotNull Skill skill) {
        if (player == null) return true;

        final String prefix = "auraskills.jobs.block.";

        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            String permission = permissionInfo.getPermission();

            if (!permission.startsWith(prefix)) continue;

            String skillName = permission.substring(prefix.length());
            if (skillName.equals(skill.getId().getKey()) || skillName.equals(skill.getId().toString())) {
                if (permissionInfo.getValue()) { // If permission is true, selection is blocked
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void sendMessage(Component component) {
        // Don't send empty messages
        if (plugin.getMessageProvider().componentToString(component).isEmpty()) {
            return;
        }
        if (player != null) {
            plugin.getAudiences().player(player).sendMessage(component);
        }
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        // Remove fleeting
        removeTraitModifier(AgilityAbilities.FLEETING_ID);
    }
}
