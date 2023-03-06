package com.archyx.aureliumskills.api.implementation;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.mana.MAbility;
import dev.aurelium.skills.api.ability.Ability;
import dev.aurelium.skills.api.ability.ManaAbility;
import dev.aurelium.skills.api.player.SkillsPlayer;
import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.stat.Stat;
import dev.aurelium.skills.api.stat.StatModifier;
import org.bukkit.entity.Player;

import java.util.Locale;

public class ApiSkillsPlayer implements SkillsPlayer {

    private final PlayerData handle;
    private final AureliumSkills plugin;
    private final Player player;

    public ApiSkillsPlayer(PlayerData handle) {
        this.handle = handle;
        this.plugin = handle.getPlugin();
        this.player = handle.getPlayer();
    }

    public PlayerData getHandle() {
        return handle;
    }

    @Override
    public double getSkillXp(Skill skill) {
        return handle.getSkillXp(convertSkill(skill));
    }

    @Override
    public void addSkillXp(Skill skill, double amountToAdd) {
        plugin.getLeveler().addXp(player, convertSkill(skill), amountToAdd);
    }

    @Override
    public void addSkillXpRaw(Skill skill, double amountToAdd) {
        com.archyx.aureliumskills.skills.Skill asSkill = convertSkill(skill);
        handle.addSkillXp(asSkill, amountToAdd);
        plugin.getLeveler().checkLevelUp(player, asSkill);
    }

    @Override
    public void setSkillXp(Skill skill, double amount) {
        com.archyx.aureliumskills.skills.Skill asSkill = convertSkill(skill);
        handle.setSkillXp(asSkill, amount);
        plugin.getLeveler().checkLevelUp(player, asSkill);
    }

    @Override
    public int getSkillLevel(Skill skill) {
        return handle.getSkillLevel(convertSkill(skill));
    }

    @Override
    public void setSkillLevel(Skill skill, int level) {
        handle.setSkillLevel(convertSkill(skill), level);
    }

    @Override
    public double getStatLevel(Stat stat) {
        return handle.getStatLevel(convertStat(stat));
    }

    @Override
    public double getBaseStatLevel(Stat stat) {
        return handle.getBaseStatLevel(convertStat(stat));
    }

    @Override
    public double getMana() {
        return handle.getMana();
    }

    @Override
    public double getMaxMana() {
        return handle.getMaxMana();
    }

    @Override
    public void setMana(double mana) {
        handle.setMana(mana);
    }

    @Override
    public int getPowerLevel() {
        return handle.getPowerLevel();
    }

    @Override
    public void addStatModifier(StatModifier statModifier) {
        handle.addStatModifier(convertStatModifier(statModifier));
    }

    @Override
    public void removeStatModifier(String name) {
        handle.removeStatModifier(name);
    }

    @Override
    public int getAbilityLevel(Ability ability) {
        return handle.getAbilityLevel(convertAbility(ability));
    }

    @Override
    public int getManaAbilityLevel(ManaAbility manaAbility) {
        return handle.getManaAbilityLevel(convertManaAbility(manaAbility));
    }

    private com.archyx.aureliumskills.skills.Skill convertSkill(Skill skill) {
        return plugin.getSkillRegistry().getFromApi(skill);
    }

    private com.archyx.aureliumskills.stats.Stat convertStat(Stat stat) {
        return plugin.getStatRegistry().getFromApi(stat);
    }

    private com.archyx.aureliumskills.modifier.StatModifier convertStatModifier(StatModifier modifier) {
        return new com.archyx.aureliumskills.modifier.StatModifier(modifier.getName(), convertStat(modifier.getStat()), modifier.getValue());
    }

    private com.archyx.aureliumskills.ability.Ability convertAbility(Ability ability) {
        return com.archyx.aureliumskills.ability.Ability.valueOf(ability.getId().getKey().toUpperCase(Locale.ROOT));
    }

    private MAbility convertManaAbility(ManaAbility manaAbility) {
        return MAbility.valueOf(manaAbility.getId().getKey().toUpperCase(Locale.ROOT));
    }

}
