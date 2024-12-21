package dev.aurelium.auraskills.bukkit.requirement.blocks;

import java.util.LinkedList;

public class BlockRequirement {

    private final boolean checksPlace;
    private final boolean checksBreak;
    private final LinkedList<RequirementNode> nodes;

    public BlockRequirement(boolean checksPlace, boolean checksBreak, LinkedList<RequirementNode> nodes) {
        this.checksPlace = checksPlace;
        this.checksBreak = checksBreak;
        this.nodes = nodes;
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
