package com.archyx.aureliumskills.skills.foraging;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.leveler.SkillLeveler;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.SourceTag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ForagingLeveler extends SkillLeveler implements Listener{

	private final ForagingAbilities foragingAbilities;

	public ForagingLeveler(AureliumSkills plugin) {
		super(plugin, Ability.FORAGER);
		this.foragingAbilities = new ForagingAbilities(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!OptionL.isEnabled(Skills.FORAGING)) return;
		//Check cancelled
		if (OptionL.getBoolean(Option.FORAGING_CHECK_CANCELLED) && event.isCancelled()) {
			return;
		}
		Block block = event.getBlock();
		// Check block replace
		if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(block)) {
			return;
		}
		Player player = event.getPlayer();
		if (blockXpGainLocation(event.getBlock().getLocation(), player, Skills.FORAGING)) return;
		if (blockXpGainPlayer(player)) return;
		// Search through sources until a match is found for the block broken
		for (ForagingSource source : ForagingSource.values()) {
			// Add XP to player if matched
			if (source.isMatch(block)) {
				plugin.getLeveler().addXp(player, Skills.FORAGING, getAbilityXp(player, source));
				if (hasTag(source, SourceTag.LUMBERJACK_APPLICABLE) && event.isDropItems()) {
					foragingAbilities.lumberjack(player, block);
				}
				break; // Stop searching if matched
			}
		}
		// Check custom blocks
		checkCustomBlocks(player, block, Skills.FORAGING);
	}
}
