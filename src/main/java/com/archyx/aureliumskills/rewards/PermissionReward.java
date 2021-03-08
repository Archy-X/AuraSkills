package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

public class PermissionReward extends Reward {

    private final String permission;
    private boolean value;

    public PermissionReward(AureliumSkills plugin, String info, String message, String permission) {
        super(plugin, info, message);
        this.permission = permission;
        this.value = true;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public void giveReward(Player player, Skill skill, int level) {
        if (plugin.isLuckPermsEnabled()) {
            LuckPerms luckPerms = LuckPermsProvider.get();
            luckPerms.getUserManager().modifyUser(player.getUniqueId(), user -> {
               user.data().add(Node.builder(permission).value(value).build());
            });
        }
    }

}
