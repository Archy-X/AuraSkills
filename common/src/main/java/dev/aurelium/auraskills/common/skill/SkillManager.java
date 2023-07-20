package dev.aurelium.auraskills.common.skill;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.SourceType;
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

    public Set<Skill> getEnabledSkills() {
        Set<Skill> skills = new HashSet<>();
        for (LoadedSkill loaded : skillMap.values()) {
            if (loaded.skill().isEnabled()) {
                skills.add(loaded.skill());
            }
        }
        return skills;
    }

    public boolean isLoaded(Skill skill) {
        return skillMap.containsKey(skill);
    }

    @SuppressWarnings("unchecked")
    public <T extends XpSource> Map<T, Skill> getSourcesOfType(Class<T> typeClass) {
        Map<T, Skill> map = new HashMap<>();
        for (Skill skill : getEnabledSkills()) {
            for (XpSource source : skill.getSources()) {
                if (typeClass.isAssignableFrom(source.getClass())) {
                    map.put((T) source, skill);
                }
            }
        }
        return map;
    }

    /**
     * Gets whether there is at least one registered source in an enabled skill of a given type
     *
     * @param sourceType The type of source
     * @return Whether the source is enabled
     */
    public boolean isSourceEnabled(SourceType sourceType) {
        for (Skill skill : getEnabledSkills()) {
            for (XpSource source : skill.getSources()) {
                if (sourceType.getSourceClass().isInstance(source)) {
                    return true;
                }
            }
        }
        return false;
    }

}
