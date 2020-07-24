package io.github.archy_x.aureliumskills.skills.abilities;

import io.github.archy_x.aureliumskills.AureliumSkills;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class AbilityOptionManager {

    private Map<Ability, AbilityOption> abilityOptions;
    private FileConfiguration config;
    private Plugin plugin;

    public AbilityOptionManager(Plugin plugin) {
        this.plugin = plugin;
        abilityOptions = new HashMap<>();
    }

    public void loadOptions() {
        config = plugin.getConfig();
        //Loads ability options
        int amountLoaded = 0;
        long startTime = System.currentTimeMillis();
        for (String ability : config.getConfigurationSection("abilities").getKeys(false)) {
            double baseValue = config.getDouble("abilities." + ability + ".base");
            double valuePerLevel = config.getDouble("abilities." + ability + ".per-level");
            AbilityOption option = new AbilityOption(baseValue, valuePerLevel);
            abilityOptions.put(Ability.valueOf(ability.toUpperCase().replace("-", "_")), option);
            amountLoaded++;
        }
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        Bukkit.getConsoleSender().sendMessage(AureliumSkills.tag + ChatColor.AQUA + "Loaded " + ChatColor.GOLD + amountLoaded + ChatColor.AQUA + " Ability Options in " + ChatColor.GOLD + amountLoaded + "ms");
    }

    public AbilityOption getAbilityOption(Ability ability) {
        return abilityOptions.get(ability);
    }

    public boolean containsOption(Ability ability) {
        return abilityOptions.containsKey(ability);
    }
}
