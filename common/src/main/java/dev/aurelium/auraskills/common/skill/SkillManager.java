package dev.aurelium.auraskills.common.skill;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.SourceTag;
import dev.aurelium.auraskills.common.source.SourceType;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class SkillManager {

    private final AuraSkillsPlugin plugin;
    private final Map<Skill, LoadedSkill> skillMap;
    private final Map<SourceTag, List<XpSource>> sourceTagMap;
    private final SkillSupplier supplier;
    private final Set<File> contentDirectories;

    public SkillManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.skillMap = new LinkedHashMap<>();
        this.sourceTagMap = new HashMap<>();
        this.supplier = new SkillSupplier(this, plugin.getMessageProvider());
        this.contentDirectories = new LinkedHashSet<>();
    }

    public SkillSupplier getSupplier() {
        return supplier;
    }

    public void register(Skill skill, LoadedSkill loadedSkill) {
        skillMap.put(skill, loadedSkill);
    }

    public void unregisterAll() {
        skillMap.clear();
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
        Set<Skill> skills = new LinkedHashSet<>();
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

    @Nullable
    public XpSource getSourceById(NamespacedId id) {
        for (Skill skill : getSkillValues()) {
            for (XpSource source : skill.getSources()) {
                if (source.getId().equals(id)) {
                    return source;
                }
            }
        }
        return null;
    }

    @Nullable
    public <T extends XpSource> Pair<T, Skill> getSingleSourceOfType(Class<T> typeClass) {
        var sources = plugin.getSkillManager().getSourcesOfType(typeClass);
        var opt = sources.entrySet().stream().findFirst();
        return opt.map(entry -> new Pair<>(entry.getKey(), entry.getValue())).orElse(null);
    }

    public void registerSourceTag(SourceTag tag, List<XpSource> sources) {
        sourceTagMap.put(tag, sources);
    }

    @NotNull
    public List<XpSource> getSourcesWithTag(SourceTag tag) {
        return sourceTagMap.getOrDefault(tag, new ArrayList<>());
    }

    public boolean hasTag(XpSource source, SourceTag tag) {
        return sourceTagMap.getOrDefault(tag, new ArrayList<>()).contains(source);
    }

    public Set<File> getContentDirectories() {
        return contentDirectories;
    }

    public void addContentDirectory(File file) {
        contentDirectories.add(file);
    }

}
