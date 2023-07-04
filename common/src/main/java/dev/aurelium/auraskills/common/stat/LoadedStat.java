package dev.aurelium.auraskills.common.stat;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;

import java.util.Map;

public record LoadedStat(Stat stat, ImmutableList<Trait> traits, Map<Trait, StatTraitConfig> traitConfigs, StatOptions options) {
}
