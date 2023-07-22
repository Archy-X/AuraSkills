package dev.aurelium.auraskills.common.trait;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitProvider;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class TraitManager implements TraitProvider {

    private final AuraSkillsPlugin plugin;
    private final Map<Trait, LoadedTrait> traitMap;

    public TraitManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.traitMap = new HashMap<>();
    }

    public abstract double getBaseLevel(User user, Trait trait);

    @NotNull
    public LoadedTrait getTrait(Trait trait) {
        LoadedTrait loadedTrait = traitMap.get(trait);
        if (loadedTrait == null) {
            throw new IllegalArgumentException("Trait " + trait + " is not loaded!");
        }
        return loadedTrait;
    }
    
    public void register(Trait trait, LoadedTrait loadedTrait) {
        traitMap.put(trait, loadedTrait);
    }

    @Override
    public String getDisplayName(Trait trait, Locale locale) {
        return plugin.getMessageProvider().getTraitDisplayName(trait, locale);
    }

    public Set<Stat> getLinkedStats(Trait trait) {
        Set<Stat> set = new HashSet<>();
        for (Stat stat : plugin.getStatManager().getStatValues()) {
            if (stat.getTraits().contains(trait)) {
                set.add(stat);
            }
        }
        return set;
    }

}
