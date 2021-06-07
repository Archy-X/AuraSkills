package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.abilities.ForagingAbilities;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.Source;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
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

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (OptionL.isEnabled(Skills.FORAGING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.FORAGING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			Block b = event.getBlock();
			//Check block replace
			if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE)) {
				if (plugin.getRegionManager().isPlacedBlock(b)) {
					return;
				}
			}
			Player p = event.getPlayer();
			if (blockXpGainLocation(b.getLocation(), p)) return;
			Skill s = Skills.FORAGING;
			Material mat = event.getBlock().getType();
			if (blockXpGainPlayer(p)) return;
			//If 1.13+
			Leveler leveler = plugin.getLeveler();
			if (XMaterial.isNewVersion()) {
				if (mat == XMaterial.OAK_LOG.parseMaterial() || mat == XMaterial.OAK_WOOD.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.OAK_LOG));
					applyAbilities(p, b);
				} else if (mat == XMaterial.SPRUCE_LOG.parseMaterial() || mat == XMaterial.SPRUCE_WOOD.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.SPRUCE_LOG));
					applyAbilities(p, b);
				} else if (mat == XMaterial.BIRCH_LOG.parseMaterial() || mat == XMaterial.BIRCH_WOOD.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.BIRCH_LOG));
					applyAbilities(p, b);
				} else if (mat == XMaterial.JUNGLE_LOG.parseMaterial() || mat == XMaterial.JUNGLE_WOOD.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.JUNGLE_LOG));
					applyAbilities(p, b);
				} else if (mat == XMaterial.ACACIA_LOG.parseMaterial() || mat == XMaterial.ACACIA_WOOD.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.ACACIA_LOG));
					applyAbilities(p, b);
				} else if (mat == XMaterial.DARK_OAK_LOG.parseMaterial() || mat == XMaterial.DARK_OAK_WOOD.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.DARK_OAK_LOG));
					applyAbilities(p, b);
				} else if (mat == XMaterial.OAK_LEAVES.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.OAK_LEAVES));
					applyAbilities(p, b);
				} else if (mat == XMaterial.SPRUCE_LEAVES.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.SPRUCE_LEAVES));
					applyAbilities(p, b);
				} else if (mat == XMaterial.BIRCH_LEAVES.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.BIRCH_LEAVES));
					applyAbilities(p, b);
				} else if (mat == XMaterial.JUNGLE_LEAVES.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.JUNGLE_LEAVES));
					applyAbilities(p, b);
				} else if (mat == XMaterial.ACACIA_LEAVES.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.ACACIA_LEAVES));
					applyAbilities(p, b);
				} else if (mat == XMaterial.DARK_OAK_LEAVES.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.DARK_OAK_LEAVES));
					applyAbilities(p, b);
				} else if (mat == XMaterial.CRIMSON_STEM.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.CRIMSON_STEM));
					applyAbilities(p, b);
				} else if (mat == XMaterial.WARPED_STEM.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.WARPED_STEM));
					applyAbilities(p, b);
				} else if (mat == XMaterial.NETHER_WART_BLOCK.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.NETHER_WART_BLOCK));
					applyAbilities(p, b);
				} else if (mat == XMaterial.WARPED_WART_BLOCK.parseMaterial()) {
					leveler.addXp(p, s, getXp(p, Source.WARPED_WART_BLOCK));
					applyAbilities(p, b);
				}
				// TODO Add 1.17 blocks
			}
			//If legacy version (1.12)
			else {
				//If legacy material LOG
				if (mat == XMaterial.OAK_LOG.parseMaterial()) {
					switch (event.getBlock().getData()) {
						case 0:
						case 4:
						case 8:
						case 12:
							leveler.addXp(p, s, getXp(p, Source.OAK_LOG));
							applyAbilities(p, b);
							break;
						case 1:
						case 5:
						case 9:
						case 13:
							leveler.addXp(p, s, getXp(p, Source.SPRUCE_LOG));
							applyAbilities(p, b);
							break;
						case 2:
						case 6:
						case 10:
						case 14:
							leveler.addXp(p, s, getXp(p, Source.BIRCH_LOG));
							applyAbilities(p, b);
							break;
						case 3:
						case 7:
						case 11:
						case 15:
							leveler.addXp(p, s, getXp(p, Source.JUNGLE_LOG));
							applyAbilities(p, b);
							break;
					}
				}
				//If legacy material LOG_2
				else if (mat == XMaterial.ACACIA_LOG.parseMaterial()) {
					switch (event.getBlock().getData()) {
						case 0:
						case 4:
						case 8:
						case 12:
							leveler.addXp(p, s, getXp(p, Source.ACACIA_LOG));
							applyAbilities(p, b);
							break;
						case 1:
						case 5:
						case 9:
						case 13:
							leveler.addXp(p, s, getXp(p, Source.DARK_OAK_LOG));
							applyAbilities(p, b);
							break;
					}
				}
				//If legacy material LEAVES
				else if (mat == XMaterial.OAK_LEAVES.parseMaterial()) {
					byte data = event.getBlock().getData();
					if (data == 0 || data == 8) {
						leveler.addXp(p, s, getXp(p, Source.OAK_LEAVES));
						applyAbilities(p, b);
					} else if (data == 1 || data == 9) {
						leveler.addXp(p, s, getXp(p, Source.SPRUCE_LEAVES));
						applyAbilities(p, b);
					} else if (data == 2 || data == 10) {
						leveler.addXp(p, s, getXp(p, Source.BIRCH_LEAVES));
						applyAbilities(p, b);
					} else if (data == 3 || data == 11) {
						leveler.addXp(p, s, getXp(p, Source.JUNGLE_LEAVES));
						applyAbilities(p, b);
					}
				}
				//If legacy material LEAVES_2
				else if (mat == XMaterial.ACACIA_LEAVES.parseMaterial()) {
					byte data = event.getBlock().getData();
					if (data == 0 || data == 8) {
						leveler.addXp(p, s, getXp(p, Source.ACACIA_LEAVES));
						applyAbilities(p, b);
					} else if (data == 1 || data == 9) {
						leveler.addXp(p, s, getXp(p, Source.DARK_OAK_LEAVES));
						applyAbilities(p, b);
					}
				}
			}
			// Check custom blocks
			checkCustomBlocks(p, b, s);
		}
	}
	
	private void applyAbilities(Player p, Block b) {
		foragingAbilities.lumberjack(p, b);
	}
}
