package dev.aurelium.auraskills.bukkit.user;

import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.api.implementation.ApiSkillsUser;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Pattern;

public class BukkitUser extends User {

    // Permission pattern
    private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    private final Player player;

    public BukkitUser(Player player, AuraSkillsPlugin plugin) {
        super(player.getUniqueId(), plugin);
        this.player = player;
    }

    public static Player getPlayer(SkillsUser skillsUser) {
        return ((BukkitUser) ((ApiSkillsUser) skillsUser).getUser()).getPlayer();
    }

    public static BukkitUser getUser(SkillsUser skillsUser) {
        return (BukkitUser) ((ApiSkillsUser) skillsUser).getUser();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String getUsername() {
        return player.getName();
    }

    @Override
    public double getPermissionMultiplier(@Nullable Skill skill) {
        double multiplier = 0.0;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String permission = info.getPermission().toLowerCase(Locale.ROOT);
            if (permission.startsWith("auraskills.multiplier.")) {
                permission = TextUtil.replace(permission, "auraskills.multiplier.", "");
                if (pattern.matcher(permission).matches()) { // Parse all skills multiplier
                    multiplier += Double.parseDouble(permission) / 100.0;
                } else if (skill != null) { // Skill specific multiplier
                    String skillName = skill.toString().toLowerCase(Locale.ROOT);
                    if (permission.startsWith(skillName)) {
                        permission = TextUtil.replace(permission, skillName + ".", "");
                        if (pattern.matcher(permission).matches()) {
                            multiplier += Double.parseDouble(permission) / 100.0;
                        }
                    }
                }
            }
        }
        return multiplier;
    }

    @Override
    public void sendMessage(Component component) {
        ((AuraSkills) plugin).getAudiences().player(player).sendMessage(component);
    }
}
