package dev.aurelium.auraskills.common.skill;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SkillManager {

    private final AuraSkillsPlugin plugin;
    private final Map<Skill, LoadedSkill> skillMap;
    private final SkillSupplier supplier;

    public SkillManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
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

    public Set<Skill> getSkillValues() {
        Set<Skill> skills = new HashSet<>();
        for (LoadedSkill loaded : skillMap.values()) {
            skills.add(loaded.skill());
        }
        return skills;
    }

    public boolean isLoaded(Skill skill) {
        return skillMap.containsKey(skill);
    }

}
