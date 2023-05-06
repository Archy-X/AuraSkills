package dev.auramc.auraskills.common.rewards.builder;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.rewards.Reward;
import dev.auramc.auraskills.common.util.data.Validate;
import dev.auramc.auraskills.common.rewards.type.PermissionReward;

public class PermissionRewardBuilder extends MessagedRewardBuilder {

    private String permission;
    private boolean value;

    public PermissionRewardBuilder(AuraSkillsPlugin plugin) {
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
