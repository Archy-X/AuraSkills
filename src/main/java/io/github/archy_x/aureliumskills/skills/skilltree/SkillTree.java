package io.github.archy_x.aureliumskills.skills.skilltree;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.abilities.Ability;

public enum SkillTree {

	FARMING(Skill.FARMING, new Ability[]{Ability.BOUNTIFUL_HARVEST, Ability.NOVICE_FARMER, Ability.GENETICIST, Ability.SHINY_APPLES, Ability.FLOWER_POWER,
			Ability.INITIATE_FARMER, Ability.SCYTHE_MASTER, Ability.TRIPLE_HARVEST, Ability.ADVANCED_FARMER, Ability.NULL,
			Ability.GROWTH_AURA, Ability.REPLENISH, Ability.MASTER_FARMER, Ability.NULL, Ability.NULL}),
	FORAGING(Skill.FORAGING, new Ability[]{Ability.METAL_DETECTOR}),
	MINING(Skill.MINING, new Ability[]{Ability.METAL_DETECTOR}),
	FISHING(Skill.FISHING, new Ability[]{Ability.METAL_DETECTOR}),
	EXCAVATION(Skill.EXCAVATION, new Ability[]{Ability.METAL_DETECTOR}),
	ARCHERY(Skill.ARCHERY, new Ability[]{Ability.METAL_DETECTOR}),
	DEFENSE(Skill.DEFENSE, new Ability[]{Ability.METAL_DETECTOR}),
	FIGHTING(Skill.FIGHTING, new Ability[]{Ability.METAL_DETECTOR}),
	ENDURANCE(Skill.ENDURANCE, new Ability[]{Ability.METAL_DETECTOR}),
	AGILITY(Skill.AGILITY, new Ability[]{Ability.METAL_DETECTOR}),
	ALCHEMY(Skill.ALCHEMY, new Ability[]{Ability.METAL_DETECTOR}),
	ENCHANTING(Skill.ENCHANTING, new Ability[]{Ability.METAL_DETECTOR}),
	SORCERY(Skill.SORCERY, new Ability[]{Ability.METAL_DETECTOR}),
	HEALING(Skill.HEALING, new Ability[]{Ability.METAL_DETECTOR}),
	FORGING(Skill.FORGING, new Ability[]{Ability.METAL_DETECTOR});
	
	private Ability[] abilities;
	private Skill skill;
	
	private SkillTree(Skill skill, Ability[] abilities) {
		this.abilities = abilities;
		this.skill = skill;
	}
	
	public Skill getSkill() {
		return skill;
	}
	
	public Ability getAbility(int index) {
		return abilities[index];
	}
	
	public Ability[] getAbilities() {
		return abilities;
	}
	
}
