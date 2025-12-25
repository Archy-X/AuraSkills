package dev.aurelium.auraskills.bukkit.listeners;

import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.BlockXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.source.BlockLeveler;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent.ChangeReason;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Set;

public class BlockInteractions implements Listener {

    private final AuraSkills plugin;
    private final Set<ChangeReason> cauldronLevelChangeReasons = Set.of(
            CauldronLevelChangeEvent.ChangeReason.BOTTLE_EMPTY,
            CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY);

    public BlockInteractions(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockFertilize(BlockFertilizeEvent event) {
        Block block = event.getBlock();
        SkillSource<BlockXpSource> skillSource = plugin.getLevelManager().getLeveler(BlockLeveler.class).getSourceByMaterialOnly(block);
        if (skillSource == null) {
            return;
        }
        BlockXpSource source = skillSource.source();
        if (source.allowBoneMeal()) { // Don't mark if bone meal should give XP
            return;
        }
        block.setMetadata("fertilized", new FixedMetadataValue(plugin, true));
    }

    @EventHandler
    public void onCauldronFill(CauldronLevelChangeEvent event) {
        CauldronLevelChangeEvent.ChangeReason reason = event.getReason();
        if (cauldronLevelChangeReasons.contains(reason)) {
            Block cauldron = event.getBlock();
            cauldron.setMetadata("filledManually", new FixedMetadataValue(plugin, true));
        }
    }

}
