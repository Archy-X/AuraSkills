package com.archyx.aureliumskills.skills;

import com.archyx.aureliumskills.skills.abilities.Ability;
import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerSkill {
	
	private final UUID playerId;
	private String playerName;

	private final Map<Skill, Integer> levels = new HashMap<>();
	private final Map<Skill, Double> xp = new HashMap<>();
	private final Map<Ability, Integer> abilities = new HashMap<>();
	private final Map<MAbility, Integer> manaAbilities = new HashMap<>();
	
	public PlayerSkill(UUID id, String playerName) {
		this.playerId = id;
		this.playerName = playerName;
		for (Skill skill : Skill.values()) {
			levels.put(skill, 1);
			xp.put(skill, 0.0);
		}
		for (Ability ability : Ability.values()) {
			abilities.put(ability, 0);
		}
		for (MAbility mAbility : MAbility.values()) {
			manaAbilities.put(mAbility, 0);
		}
	}

	public void setPlayerName(String name) {
		this.playerName = name;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Map<Ability, Integer> getAbilities() {
		return abilities;
	}
	
	public int getAbilityLevel(Ability ability) {
		return abilities.get(ability);
	}
	
	public void setAbilityLevel(Ability ability, int level) {
		abilities.put(ability, level);
	}
	
	public void levelUpAbility(Ability ability) {
		abilities.put(ability, abilities.get(ability) + 1);
	}

	public int getManaAbilityLevel(MAbility mAbility) {
		return manaAbilities.get(mAbility);
	}

	public void setManaAbilityLevel(MAbility mAbility, int level) {
		manaAbilities.put(mAbility, level);
	}

	public void levelUpManaAbility(MAbility mAbility) {
		manaAbilities.put(mAbility, manaAbilities.get(mAbility) + 1);
	}

	public boolean addXp(Skill skill, double amount) {
		if (xp.containsKey(skill)) {
			xp.put(skill, xp.get(skill) + amount);
			return true;
		}
		else {
			return false;
		}
	}
	
	public void setXp(Skill skill, double amount) {
		xp.put(skill, amount);
	}
	
	public double getXp(Skill skill) {
		if (xp.containsKey(skill)) {
			return xp.get(skill);
		}
		else {
			return 0;
		}
	}
	
	public int getSkillLevel(Skill skill) {
		return levels.getOrDefault(skill, 0);
	}
	
	public Set<Skill> getSkillSet() {
		return levels.keySet();
	}
	
	public void setSkillLevel(Skill skill, int level) {
		if (levels.containsKey(skill)) {
			levels.put(skill, level);
		}
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
