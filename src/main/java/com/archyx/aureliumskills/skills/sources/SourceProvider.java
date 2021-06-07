package com.archyx.aureliumskills.skills.sources;

import com.archyx.aureliumskills.skills.Skill;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public interface SourceProvider {

    Skill getSkill();

    default String getPath() {
        return getSkill().toString().toLowerCase(Locale.ROOT) + "." + toString().toLowerCase(Locale.ROOT);
    }

    static Set<SourceProvider> values() {
        Set<SourceProvider> sources = new HashSet<>(Arrays.asList(FarmingSources.values()));
        sources.addAll(Arrays.asList(ForagingSources.values()));
        sources.addAll(Arrays.asList(MiningSources.values()));
        sources.addAll(Arrays.asList(FishingSources.values()));
        sources.addAll(Arrays.asList(ExcavationSources.values()));
        sources.addAll(Arrays.asList(ArcherySources.values()));
        sources.addAll(Arrays.asList(DefenseSources.values()));
        sources.addAll(Arrays.asList(FightingSources.values()));
        sources.addAll(Arrays.asList(EnduranceSources.values()));
        sources.addAll(Arrays.asList(AgilitySources.values()));
        sources.addAll(Arrays.asList(AlchemySources.values()));
        sources.addAll(Arrays.asList(EnchantingSources.values()));
        sources.addAll(Arrays.asList(SorcerySources.values()));
        sources.addAll(Arrays.asList(HealingSources.values()));
        sources.addAll(Arrays.asList(ForgingSources.values()));
        return sources;
    }

    @Nullable
    static SourceProvider valueOf(String sourceString) {
        for (SourceProvider source : values()) {
            if (source.toString().equals(sourceString.toUpperCase(Locale.ROOT))) {
                return source;
            }
        }
        return null;
    }

}
