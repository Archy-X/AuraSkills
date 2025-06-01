package dev.aurelium.auraskills.bukkit.requirement.blocks;

import org.bukkit.Material;

import java.util.List;

public class BlockRequirement {

    private final Material material;
    private final boolean allowPlace;
    private final boolean allowBreak;
    private final boolean allowHarvest;
    private final List<RequirementNode> nodes;

    public BlockRequirement(Material material, boolean allowPlace, boolean allowBreak, boolean allowHarvest, List<RequirementNode> nodes) {
        this.material = material;
        this.allowPlace = allowPlace;
        this.allowBreak = allowBreak;
        this.allowHarvest = allowHarvest;
        this.nodes = nodes;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean allowPlace() {
        return allowPlace;
    }

    public boolean allowBreak() {
        return allowBreak;
    }

    public boolean allowHarvest() {
        return allowHarvest;
    }

    public List<RequirementNode> getNodes() {
        return nodes;
    }

}
