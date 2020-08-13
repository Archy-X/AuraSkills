package com.archyx.aureliumskills.skills;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Leaderboard {

    public List<PlayerSkill> getPowerLeaderBoard() {
        List<PlayerSkill> playerSkills = new ArrayList<>(SkillLoader.playerSkills.values());
        Collections.sort(playerSkills, new LeaderboardSorter());
        return playerSkills;
    }

    public List<PlayerSkill> getSkillLeaderBoard(Skill skill) {
        List<PlayerSkill> playerSkills = new ArrayList<>(SkillLoader.playerSkills.values());
        Collections.sort(playerSkills, new SkillLeaderboardSorter(skill));
        return playerSkills;
    }

    class LeaderboardSorter implements Comparator<PlayerSkill> {

        public int compare(PlayerSkill a, PlayerSkill b) {
            return b.getPowerLevel() - a.getPowerLevel();
        }
    }

    class SkillLeaderboardSorter implements Comparator<PlayerSkill> {

        private Skill skill;

        public SkillLeaderboardSorter(Skill skill) {
            this.skill = skill;
        }

        public int compare(PlayerSkill a, PlayerSkill b) {
            return b.getSkillLevel(skill) - a.getSkillLevel(skill);
        }
    }
}
