package dev.aurelium.auraskills.bukkit.requirement.blocks;

import org.bukkit.Material;

import java.util.LinkedList;

public class BlockRequirement {

    private final Material material;
    private final boolean checksPlace;
    private final boolean checksBreak;
    private final LinkedList<RequirementNode> nodes;

    public BlockRequirement(Material material, boolean checksPlace, boolean checksBreak, LinkedList<RequirementNode> nodes) {
        this.material = material;
        this.checksPlace = checksPlace;
        this.checksBreak = checksBreak;
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

    public LinkedList<RequirementNode> getNodes() {
        return nodes;
    }
}
