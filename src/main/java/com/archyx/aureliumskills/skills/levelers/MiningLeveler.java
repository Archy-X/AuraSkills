package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.abilities.MiningAbilities;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.sources.MiningSource;
import com.archyx.aureliumskills.skills.sources.SourceTag;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;


public class MiningLeveler extends SkillLeveler implements Listener {

	private final MiningAbilities miningAbilities;

	public MiningLeveler(AureliumSkills plugin) {
		super(plugin, Ability.MINER);
		this.miningAbilities = new MiningAbilities(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	@SuppressWarnings("deprecation")
	public void onBlockBreak(BlockBreakEvent event) {
		if (OptionL.isEnabled(Skills.MINING)) {
			// Check cancelled
			if (OptionL.getBoolean(Option.MINING_CHECK_CANCELLED) && event.isCancelled()) {
				return;
			}
			Block block = event.getBlock();
			// Check block replace
			if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE) && plugin.getRegionManager().isPlacedBlock(block)) {
				return;
			}

			Player player = event.getPlayer();
			if (blockXpGainLocation(block.getLocation(), player)) return;
			if (blockXpGainPlayer(player)) return;

			String materialName = block.getType().toString();
			// Search through mining sources until a match is found for the block broken
			for (MiningSource source : MiningSource.values()) {
				boolean matched = false;
				if (XMaterial.isNewVersion() || source.getLegacyMaterial() == null) { // Standard block handling
					if (source.toString().equalsIgnoreCase(materialName)) {
						matched = true;
					}
				} else { // Legacy block handling
					if (source.getLegacyData() == (byte) -1) { // No data value
						if (source.allowBothIfLegacy()) { // Allow both new and legacy material names
							if (source.getLegacyMaterial().equalsIgnoreCase(materialName) || source.toString().equalsIgnoreCase(materialName)) {
								matched = true;
							}
						} else if (source.getLegacyMaterial().equalsIgnoreCase(materialName)) {
							matched = true;
						}
					} else { // With data value
						if (source.allowBothIfLegacy()) { // Allow both new and legacy material names
							if ((source.getLegacyMaterial().equalsIgnoreCase(materialName) && source.getLegacyData() == block.getData()
									|| (source.toString().equalsIgnoreCase(materialName) && source.getLegacyData() == block.getData()))) {
								matched = true;
							}
						} else if (source.getLegacyMaterial().equalsIgnoreCase(materialName) && source.getLegacyData() == block.getData()) {
							matched = true;
						}
					}
				}
				// Add XP to player if matched
				if (matched) {
					plugin.getLeveler().addXp(player, Skills.MINING, getXp(player, source));
					// Apply Luck Miner if has tag
					if (hasTag(source, SourceTag.LUCKY_MINER_APPLICABLE)) {
						miningAbilities.luckyMiner(player, block);
					}
					break; // Stop searching if matched
				}
			}
			// Check custom blocks
			checkCustomBlocks(player, block, Skills.MINING);
		}
	}
}
