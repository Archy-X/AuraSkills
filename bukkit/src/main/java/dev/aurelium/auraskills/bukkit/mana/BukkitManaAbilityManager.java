package dev.aurelium.auraskills.bukkit.mana;

import dev.aurelium.auraskills.api.event.AuraSkillsListener;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skills.farming.Replenish;
import dev.aurelium.auraskills.bukkit.skills.mining.SpeedMine;
import dev.aurelium.auraskills.common.mana.ManaAbilityManager;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public class BukkitManaAbilityManager extends ManaAbilityManager {

    private final AuraSkills plugin;
    private final Set<ManaAbilityProvider> providerSet;

    public BukkitManaAbilityManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
        this.providerSet = new HashSet<>();

        registerProviders();
        new TimerCountdown(plugin); // Start counting down cooldown and error timers
    }

    private void registerProviders() {
        registerProvider(new SpeedMine(plugin));
        registerProvider(new Replenish(plugin));
    }

    private void registerProvider(ManaAbilityProvider provider) {
        providerSet.add(provider);
        Bukkit.getPluginManager().registerEvents(provider, plugin);
        if (provider instanceof AuraSkillsListener listener) {
            plugin.getEventManager().registerEvents(plugin, listener);
        }
    }

    public <T extends ManaAbilityProvider> T getProvider(Class<T> clazz) {
        for (ManaAbilityProvider provider : providerSet) {
            if (provider.getClass().equals(clazz)) {
                return clazz.cast(provider);
            }
        }
        throw new IllegalArgumentException("Mana ability provider of type " + clazz.getSimpleName() + " not found!");
    }

}
