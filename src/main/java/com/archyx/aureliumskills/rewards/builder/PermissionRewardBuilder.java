package com.archyx.aureliumskills.rewards.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.rewards.PermissionReward;
import com.archyx.aureliumskills.rewards.Reward;
import com.archyx.aureliumskills.util.misc.Validate;
import org.jetbrains.annotations.NotNull;

public class PermissionRewardBuilder extends MessagedRewardBuilder {

    private String permission;
    private boolean value;

    public PermissionRewardBuilder(AureliumSkills plugin) {
        super(plugin);
        this.value = true;
    }

    public @NotNull PermissionRewardBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }
    
    public @NotNull PermissionRewardBuilder value(boolean value) {
        this.value = value;
        return this;
    }

    @Override
    public @NotNull Reward build() {
        Validate.notNull(permission, "You must specify a permission");
        return new PermissionReward(plugin, menuMessage, chatMessage, permission, value);
    }
    

}
