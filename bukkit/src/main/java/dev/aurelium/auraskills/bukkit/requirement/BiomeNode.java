package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public class BiomeNode extends RequirementNode {

    private final String biome;

    public BiomeNode(AuraSkills plugin, String biome, String message) {
        super(plugin, message);
        this.biome = biome;
    }

    @Override
    public boolean check(Player player) {
        if (!player.getLocation().getBlock().getBiome().toString().equalsIgnoreCase(biome)) {
            return false;
        }

        return true;
    }

}
