package dev.aurelium.auraskills.common.skill;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SkillManager {

    private final Map<Skill, LoadedSkill> skillMap;
    private final SkillSupplier supplier;

    public SkillManager(AuraSkillsPlugin plugin) {
        this.skillMap = new HashMap<>();
        this.supplier = new SkillSupplier(this, plugin.getMessageProvider());
    }

    public SkillSupplier getSupplier() {
        return supplier;
    }

    public void register(Skill skill, LoadedSkill loadedSkill) {
        skillMap.put(skill, loadedSkill);
    }

    @NotNull
    public LoadedSkill getSkill(Skill skill) {
        LoadedSkill loadedSkill = skillMap.get(skill);
        if (loadedSkill == null) {
            throw new IllegalArgumentException("Skill " + skill + " is not loaded!");
        }
        return loadedSkill;
    }

    public Collection<LoadedSkill> getSkills() {
        return skillMap.values();
    }

}
