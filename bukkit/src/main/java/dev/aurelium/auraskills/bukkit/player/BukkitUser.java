package dev.aurelium.auraskills.bukkit.player;

import com.archyx.aureliumskills.util.text.TextUtil;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.player.User;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

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

    public Player getPlayer() {
        return player;
    }

    @Override
    public String getUsername() {
        return player.getName();
    }

    @Override
    public double getPermissionMultiplier(Skill skill) {
        double multiplier = 0.0;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            String permission = info.getPermission().toLowerCase(Locale.ROOT);
            if (permission.startsWith("auraskills.multiplier.")) {
                permission = TextUtil.replace(permission, "auraskills.multiplier.", "");
                if (pattern.matcher(permission).matches()) { // Parse all skills multiplier
                    multiplier += Double.parseDouble(permission) / 100;
                } else if (skill != null) { // Skill specific multiplier
                    String skillName = skill.toString().toLowerCase(Locale.ROOT);
                    if (permission.startsWith(skillName)) {
                        permission = TextUtil.replace(permission, skillName + ".", "");
                        if (pattern.matcher(permission).matches()) {
                            multiplier += Double.parseDouble(permission) / 100;
                        }
                    }
                }
            }
        }
        return multiplier / 100.0;
    }

    @Override
    public void sendMessage(Component component) {
        ((AuraSkills) plugin).getAudiences().player(player).sendMessage(component);
    }
}
