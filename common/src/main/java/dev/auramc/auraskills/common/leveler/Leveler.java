package dev.auramc.auraskills.common.leveler;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.data.PlayerData;

public interface Leveler {

    void addXp(PlayerData playerData, Skill skill, double amount);

    void checkLevelUp(PlayerData playerData, Skill skill);

    void updateStats(PlayerData playerData);

}
