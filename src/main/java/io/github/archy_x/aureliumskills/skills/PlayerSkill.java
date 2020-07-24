package io.github.archy_x.aureliumskills.skills;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.github.archy_x.aureliumskills.skills.abilities.Ability;

public class PlayerSkill {
	
	private UUID playerId;
	
	private Map<Skill, Integer> levels = new HashMap<Skill, Integer>();
	private Map<Skill, Double> xp = new HashMap<Skill, Double>();
	private Map<Ability, Integer> abilities = new HashMap<Ability, Integer>();
	
	public PlayerSkill(UUID id) {
		this.playerId = id;
		for (Skill skill : Skill.values()) {
			levels.put(skill, 1);
			xp.put(skill, 0.0);
		}
		for (Ability ability : Ability.values()) {
			abilities.put(ability, 0);
		}
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
	
	public boolean addXp(Skill skill, double amount) {
		if (xp.containsKey(skill)) {
			xp.put(skill, xp.get(skill) + amount);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean setXp(Skill skill, double amount) {
		if (xp.containsKey(skill)) {
			xp.put(skill, amount);
			return true;
		}
		else {
			return false;
		}
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
		if (levels.containsKey(skill)) {
			return levels.get(skill);
		}
		else {
			return 0;
		}
	}
	
	public Set<Skill> getSkillSet() {
		return levels.keySet();
	}
	
	public boolean setSkillLevel(Skill skill, int level) {
		if (levels.containsKey(skill)) {
			levels.put(skill, level);
			return true;
		}
		else {
			return false;
		}
	}
	
	public UUID getPlayerId() {
		return playerId;
	}
	
	
	
}
