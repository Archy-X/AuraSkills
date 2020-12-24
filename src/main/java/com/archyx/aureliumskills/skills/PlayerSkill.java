package com.archyx.aureliumskills.skills;

import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.mana.MAbility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerSkill {
	
	private final UUID playerId;
	private String playerName;

	private final Map<Skill, Integer> levels = new HashMap<>();
	private final Map<Skill, Double> xp = new HashMap<>();
	
	public PlayerSkill(UUID id, String playerName) {
		this.playerId = id;
		this.playerName = playerName;
		for (Skill skill : Skill.values()) {
			levels.put(skill, 1);
			xp.put(skill, 0.0);
		}
	}

	public void setPlayerName(String name) {
		this.playerName = name;
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getAbilityLevel(Ability ability) {
		// Check if unlocked
		if (getSkillLevel(ability.getSkill()) < ability.getUnlock()) {
			return 0;
		}
		int level =  (getSkillLevel(ability.getSkill()) - ability.getUnlock()) / ability.getLevelUp() + 1;
		// Check max level
		if (level <= ability.getMaxLevel() || ability.getMaxLevel() == 0) {
			return level;
		} else {
			return ability.getMaxLevel();
		}
	}

	public int getManaAbilityLevel(MAbility mAbility) {
		// Check if unlocked
		if (getSkillLevel(mAbility.getSkill()) < mAbility.getUnlock()) {
			return 0;
		}
		int level = (getSkillLevel(mAbility.getSkill()) - mAbility.getUnlock()) / mAbility.getLevelUp() + 1;
		// Check max level
		if (level <= mAbility.getMaxLevel() || mAbility.getMaxLevel() == 0) {
			return level;
		} else {
			return mAbility.getMaxLevel();
		}
	}

	public void addXp(Skill skill, double amount) {
		xp.merge(skill, amount, Double::sum);
	}
	
	public void setXp(Skill skill, double amount) {
		xp.put(skill, amount);
	}
	
	public double getXp(Skill skill) {
		return xp.getOrDefault(skill, 0.0);
	}
	
	public int getSkillLevel(Skill skill) {
		return levels.getOrDefault(skill, 0);
	}
	
	public Set<Skill> getSkillSet() {
		return levels.keySet();
	}
	
	public void setSkillLevel(Skill skill, int level) {
		levels.put(skill, level);
	}
	
	public UUID getPlayerId() {
		return playerId;
	}
	
	public int getPowerLevel() {
		int power = 0;
		for (int level : levels.values()) {
			power += level;
		}
		return power;
	}

	public boolean hasData() {
		for (Map.Entry<Skill, Integer> entry : levels.entrySet()) {
			if (!entry.getValue().equals(1)) {
				return true;
			}
		}
		for (Map.Entry<Skill, Double> entry : xp.entrySet()) {
			if (!entry.getValue().equals(0.0)) {
				return true;
			}
		}
		return false;
	}

}
