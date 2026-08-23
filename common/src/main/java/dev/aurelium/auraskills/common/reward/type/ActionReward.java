package dev.aurelium.auraskills.common.reward.type;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.action.UserAction;
import dev.aurelium.auraskills.common.reward.RewardActionContext;
import dev.aurelium.auraskills.common.user.User;
import org.jetbrains.annotations.Nullable;

public class ActionReward extends MessagedReward {

    private final UserAction action;

    public ActionReward(AuraSkillsPlugin plugin,
            Skill skill,
            @Nullable String menuMessage,
            @Nullable String chatMessage,
            UserAction action) {
        super(plugin, skill, menuMessage, chatMessage);
        this.action = action;
    }

    @Override
    public void giveReward(User user, Skill skill, int level) {
        action.run(plugin, user, new RewardActionContext(plugin, user, skill, level));
    }
}
