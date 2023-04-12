package dev.aurelium.skills.common.leveler;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.common.data.PlayerData;

public interface Leveler {

    void addXp(PlayerData playerData, Skill skill, double amount);

    void checkLevelUp(PlayerData playerData, Skill skill);

    void updateStats(PlayerData playerData);

}
