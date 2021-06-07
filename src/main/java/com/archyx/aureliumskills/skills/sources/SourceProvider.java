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
        Set<SourceProvider> sources = new HashSet<>(Arrays.asList(FarmingSource.values()));
        sources.addAll(Arrays.asList(ForagingSource.values()));
        sources.addAll(Arrays.asList(MiningSource.values()));
        sources.addAll(Arrays.asList(FishingSource.values()));
        sources.addAll(Arrays.asList(ExcavationSource.values()));
        sources.addAll(Arrays.asList(ArcherySource.values()));
        sources.addAll(Arrays.asList(DefenseSource.values()));
        sources.addAll(Arrays.asList(FightingSource.values()));
        sources.addAll(Arrays.asList(EnduranceSource.values()));
        sources.addAll(Arrays.asList(AgilitySource.values()));
        sources.addAll(Arrays.asList(AlchemySource.values()));
        sources.addAll(Arrays.asList(EnchantingSource.values()));
        sources.addAll(Arrays.asList(SorcerySource.values()));
        sources.addAll(Arrays.asList(HealingSource.values()));
        sources.addAll(Arrays.asList(ForgingSource.values()));
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
