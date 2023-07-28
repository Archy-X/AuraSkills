package dev.aurelium.auraskills.bukkit.ability;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skills.farming.FarmingAbilities;
import dev.aurelium.auraskills.bukkit.skills.foraging.ForagingAbilities;
import dev.aurelium.auraskills.common.ability.AbilityManager;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public class BukkitAbilityManager extends AbilityManager {

    private final AuraSkills plugin;
    private final Set<AbilityImpl> abilityImpls = new HashSet<>();

    public BukkitAbilityManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
        registerAbilityImplementations();
    }

    private void registerAbilityImplementations() {
        registerAbilityImpl(new FarmingAbilities(plugin));
        registerAbilityImpl(new ForagingAbilities(plugin));
    }

    public void registerAbilityImpl(AbilityImpl abilityImpl) {
        abilityImpls.add(abilityImpl);
        Bukkit.getPluginManager().registerEvents(abilityImpl, plugin);
    }

    public <T extends AbilityImpl> T getAbilityImpl(Class<T> clazz) {
        for (AbilityImpl abilityImpl : abilityImpls) {
            if (abilityImpl.getClass().equals(clazz)) {
                return clazz.cast(abilityImpl);
            }
        }
        throw new IllegalArgumentException("Ability implementation of type " + clazz.getSimpleName() + " not found!");
    }

}
