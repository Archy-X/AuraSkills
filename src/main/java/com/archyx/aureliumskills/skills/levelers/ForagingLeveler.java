package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.abilities.ForagingAbilities;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.Skill;
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

	public ForagingLeveler(AureliumSkills plugin) {
		super(plugin, Ability.FORAGER);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (OptionL.isEnabled(Skill.FORAGING)) {
			//Check cancelled
			if (OptionL.getBoolean(Option.FORAGING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			Block b = event.getBlock();
			if (blockXpGainLocation(b.getLocation())) return;
			//Check block replace
			if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE)) {
				if (event.getBlock().hasMetadata("skillsPlaced")) {
					return;
				}
			}
			Player p = event.getPlayer();
			Skill s = Skill.FORAGING;
			Material mat = event.getBlock().getType();
			if (blockXpGainPlayer(p)) return;
			//If 1.13+
			if (XMaterial.isNewVersion()) {
				if (mat.equals(XMaterial.OAK_LOG.parseMaterial()) || mat.equals(XMaterial.OAK_WOOD.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.OAK_LOG));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.SPRUCE_LOG.parseMaterial()) || mat.equals(XMaterial.SPRUCE_WOOD.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.SPRUCE_LOG));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.BIRCH_LOG.parseMaterial()) || mat.equals(XMaterial.BIRCH_WOOD.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.BIRCH_LOG));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.JUNGLE_LOG.parseMaterial()) || mat.equals(XMaterial.JUNGLE_WOOD.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.JUNGLE_LOG));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.ACACIA_LOG.parseMaterial()) || mat.equals(XMaterial.ACACIA_WOOD.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.ACACIA_LOG));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.DARK_OAK_LOG.parseMaterial()) || mat.equals(XMaterial.DARK_OAK_WOOD.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.DARK_OAK_LOG));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.OAK_LEAVES.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.OAK_LEAVES));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.SPRUCE_LEAVES.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.SPRUCE_LEAVES));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.BIRCH_LEAVES.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.BIRCH_LEAVES));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.JUNGLE_LEAVES.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.JUNGLE_LEAVES));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.ACACIA_LEAVES.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.ACACIA_LEAVES));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.DARK_OAK_LEAVES.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.DARK_OAK_LEAVES));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.CRIMSON_STEM.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.CRIMSON_STEM));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.WARPED_STEM.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.WARPED_STEM));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.NETHER_WART_BLOCK.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.NETHER_WART_BLOCK));
					applyAbilities(p, b);
				} else if (mat.equals(XMaterial.WARPED_WART_BLOCK.parseMaterial())) {
					Leveler.addXp(p, s, getXp(p, Source.WARPED_WART_BLOCK));
					applyAbilities(p, b);
				}
			}
			//If legacy version (1.12)
			else {
				//If legacy material LOG
				if (mat.equals(XMaterial.OAK_LOG.parseMaterial())) {
					switch (event.getBlock().getData()) {
						case 0:
						case 4:
						case 8:
							Leveler.addXp(p, s, getXp(p, Source.OAK_LOG));
							applyAbilities(p, b);
							break;
						case 1:
						case 5:
						case 9:
							Leveler.addXp(p, s, getXp(p, Source.SPRUCE_LOG));
							applyAbilities(p, b);
							break;
						case 2:
						case 6:
						case 10:
							Leveler.addXp(p, s, getXp(p, Source.BIRCH_LOG));
							applyAbilities(p, b);
							break;
						case 3:
						case 7:
						case 11:
							Leveler.addXp(p, s, getXp(p, Source.JUNGLE_LOG));
							applyAbilities(p, b);
							break;
					}
				}
				//If legacy material LOG_2
				else if (mat.equals(XMaterial.ACACIA_LOG.parseMaterial())) {
					switch (event.getBlock().getData()) {
						case 0:
						case 4:
						case 8:
							Leveler.addXp(p, s, getXp(p, Source.ACACIA_LOG));
							applyAbilities(p, b);
							break;
						case 1:
						case 5:
						case 9:
							Leveler.addXp(p, s, getXp(p, Source.DARK_OAK_LOG));
							applyAbilities(p, b);
							break;
					}
				}
				//If legacy material LEAVES
				else if (mat.equals(XMaterial.OAK_LEAVES.parseMaterial())) {
					byte data = event.getBlock().getData();
					if (data == 0 || data == 8) {
						Leveler.addXp(p, s, getXp(p, Source.OAK_LEAVES));
						applyAbilities(p, b);
					} else if (data == 1 || data == 9) {
						Leveler.addXp(p, s, getXp(p, Source.SPRUCE_LEAVES));
						applyAbilities(p, b);
					} else if (data == 2 || data == 10) {
						Leveler.addXp(p, s, getXp(p, Source.BIRCH_LEAVES));
						applyAbilities(p, b);
					} else if (data == 3 || data == 11) {
						Leveler.addXp(p, s, getXp(p, Source.JUNGLE_LEAVES));
						applyAbilities(p, b);
					}
				}
				//If legacy material LEAVES_2
				else if (mat.equals(XMaterial.ACACIA_LEAVES.parseMaterial())) {
					byte data = event.getBlock().getData();
					if (data == 0 || data == 8) {
						Leveler.addXp(p, s, getXp(p, Source.ACACIA_LEAVES));
						applyAbilities(p, b);
					} else if (data == 1 || data == 9) {
						Leveler.addXp(p, s, getXp(p, Source.DARK_OAK_LEAVES));
						applyAbilities(p, b);
					}
				}
			}
			// Check custom blocks
			checkCustomBlocks(p, b, s);
		}
	}
	
	private void applyAbilities(Player p, Block b) {
		ForagingAbilities.lumberjack(p, b);
	}
}
