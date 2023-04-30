package dev.aurelium.skills.common.rewards.builder;

import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.rewards.Reward;
import dev.aurelium.skills.common.rewards.type.PermissionReward;
import dev.aurelium.skills.common.util.data.Validate;

public class PermissionRewardBuilder extends MessagedRewardBuilder {

    private String permission;
    private boolean value;

    public PermissionRewardBuilder(AureliumSkillsPlugin plugin) {
        super(plugin);
        this.value = true;
    }

    public PermissionRewardBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }
    
    public PermissionRewardBuilder value(boolean value) {
        this.value = value;
        return this;
    }

    @Override
    public Reward build() {
        Validate.notNull(permission, "You must specify a permission");
        return new PermissionReward(plugin, menuMessage, chatMessage, permission, value);
    }
    

}
