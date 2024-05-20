package dev.aurelium.auraskills.common.trait;

import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitHandler;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class TraitManager {

    private final AuraSkillsPlugin plugin;
    private final Map<Trait, LoadedTrait> traitMap;
    private final TraitSupplier supplier;

    public TraitManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.traitMap = new HashMap<>();
        this.supplier = new TraitSupplier(this, plugin.getMessageProvider());
    }

    public TraitSupplier getSupplier() {
        return supplier;
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

    public void unregisterAll() {
        traitMap.clear();
    }

    public Set<Trait> getEnabledTraits() {
        Set<Trait> skills = new HashSet<>();
        for (LoadedTrait loaded : traitMap.values()) {
            if (loaded.trait().isEnabled()) {
                skills.add(loaded.trait());
            }
        }
        return skills;
    }

    public Set<Stat> getLinkedStats(Trait trait) {
        Set<Stat> set = new HashSet<>();
        for (Stat stat : plugin.getStatManager().getEnabledStats()) {
            if (stat.getTraits().contains(trait)) {
                set.add(stat);
            }
        }
        return set;
    }

    public boolean isLoaded(Trait trait) {
        return traitMap.containsKey(trait);
    }

    public abstract void registerTraitHandler(TraitHandler traitHandler);

    public abstract String getMenuDisplay(Trait trait, double value, Locale locale);
}
