package dev.aurelium.auraskills.common.event;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.jetbrains.annotations.Nullable;

public interface EventHandler {

    void callUserLoadEvent(User user);

    void callSkillLevelUpEvent(User user, Skill skill, int level);

    Pair<Boolean, Double> callXpGainEvent(User user, Skill skill, @Nullable XpSource source, double amount);

}
