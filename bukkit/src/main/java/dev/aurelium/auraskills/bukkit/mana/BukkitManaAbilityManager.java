package dev.aurelium.auraskills.bukkit.mana;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.skills.archery.ChargedShot;
import dev.aurelium.auraskills.bukkit.skills.defense.Absorption;
import dev.aurelium.auraskills.bukkit.skills.excavation.Terraform;
import dev.aurelium.auraskills.bukkit.skills.farming.Replenish;
import dev.aurelium.auraskills.bukkit.skills.fighting.LightningBlade;
import dev.aurelium.auraskills.bukkit.skills.fishing.SharpHook;
import dev.aurelium.auraskills.bukkit.skills.foraging.Treecapitator;
import dev.aurelium.auraskills.bukkit.skills.mining.SpeedMine;
import dev.aurelium.auraskills.common.mana.ManaAbilityManager;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class BukkitManaAbilityManager extends ManaAbilityManager {

    private final AuraSkills plugin;
    private final Map<Class<?>, ManaAbilityProvider> providerMap;

    public BukkitManaAbilityManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
        this.providerMap = new HashMap<>();
    }

    public void registerProviders() {
        new TimerCountdown(plugin); // Start counting down cooldown and error timers
        registerProvider(new Replenish(plugin));
        registerProvider(new Treecapitator(plugin));
        registerProvider(new SpeedMine(plugin));
        registerProvider(new SharpHook(plugin));
        registerProvider(new Terraform(plugin));
        registerProvider(new ChargedShot(plugin));
        registerProvider(new Absorption(plugin));
        registerProvider(new LightningBlade(plugin));
    }

    private void registerProvider(ManaAbilityProvider provider) {
        providerMap.put(provider.getClass(), provider);
        Bukkit.getPluginManager().registerEvents(provider, plugin);
    }

    public <T extends ManaAbilityProvider> T getProvider(Class<T> clazz) {
        ManaAbilityProvider provider = providerMap.get(clazz);
        if (provider != null) {
            return clazz.cast(provider);
        }
        throw new IllegalArgumentException("Mana ability provider of type " + clazz.getSimpleName() + " not found!");
    }

}
