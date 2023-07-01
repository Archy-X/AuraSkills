package dev.aurelium.auraskills.common.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TraitManager {

    private final AuraSkillsPlugin plugin;
    private final Map<Trait, LoadedTrait> traitMap;

    public TraitManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.traitMap = new HashMap<>();
    }

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
    
}
