package dev.aurelium.auraskills.bukkit.requirement.blocks;

import org.bukkit.Material;

import java.util.List;

public class BlockRequirement {

    private final Material material;
    private final boolean checksPlace;
    private final boolean checksBreak;
    private final boolean checksHarvest;
    private final List<RequirementNode> nodes;

    public BlockRequirement(Material material, boolean checksPlace, boolean checksBreak, boolean checksHarvest, List<RequirementNode> nodes) {
        this.material = material;
        this.checksPlace = checksPlace;
        this.checksBreak = checksBreak;
        this.checksHarvest = checksHarvest;
        this.nodes = nodes;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean checksPlacing() {
        return checksPlace;
    }

    public boolean checksBreaking() {
        return checksBreak;
    }

    public boolean checksHarvesting() {
        return checksHarvest;
    }

    public List<RequirementNode> getNodes() {
        return nodes;
    }
}
