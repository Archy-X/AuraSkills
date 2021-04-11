package com.archyx.aureliumskills.skills.levelers;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.abilities.MiningAbilities;
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
			//Check cancelled
			if (OptionL.getBoolean(Option.MINING_CHECK_CANCELLED)) {
				if (event.isCancelled()) {
					return;
				}
			}
			Block b = event.getBlock();
			if (blockXpGainLocation(b.getLocation())) return;
			//Check block replace
			if (OptionL.getBoolean(Option.CHECK_BLOCK_REPLACE)) {
				if (plugin.getRegionManager().isPlacedBlock(b)) {
					return;
				}
			}
			Player p = event.getPlayer();
			Skill s = Skills.MINING;
			Material mat = event.getBlock().getType();
			Leveler leveler = plugin.getLeveler();
			if (blockXpGainPlayer(p)) return;
			if (mat.equals(Material.STONE)) {
				if (XMaterial.isNewVersion()) {
					leveler.addXp(p, s, getXp(p, Source.STONE));
				}
				else {
					switch (b.getData()) {
						case 0:
							leveler.addXp(p, s, getXp(p, Source.STONE));
							break;
						case 1:
							leveler.addXp(p, s, getXp(p, Source.GRANITE));
							break;
						case 3:
							leveler.addXp(p, s, getXp(p, Source.DIORITE));
							break;
						case 5:
							leveler.addXp(p, s, getXp(p, Source.ANDESITE));
							break;
					}
				}
			}
			else if (mat.equals(XMaterial.GRANITE.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, Source.GRANITE));
			}
			else if (mat.equals(XMaterial.DIORITE.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, Source.DIORITE));
			}
			else if (mat.equals(XMaterial.ANDESITE.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, Source.ANDESITE));
			}
			else if (mat.equals(Material.COBBLESTONE)) {
				leveler.addXp(p, s, getXp(p, Source.COBBLESTONE));
			}
			else if (mat.equals(Material.COAL_ORE)) {
				leveler.addXp(p, s, getXp(p, Source.COAL_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.NETHER_QUARTZ_ORE.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, Source.QUARTZ_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.IRON_ORE)) {
				leveler.addXp(p, s, getXp(p, Source.IRON_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.REDSTONE_ORE.parseMaterial()) || mat.name().equals("GLOWING_REDSTONE_ORE")) {
				leveler.addXp(p, s, getXp(p, Source.REDSTONE_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.LAPIS_ORE)) {
				leveler.addXp(p, s, getXp(p, Source.LAPIS_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.GOLD_ORE)) {
				leveler.addXp(p, s, getXp(p, Source.GOLD_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.DIAMOND_ORE)) {
				leveler.addXp(p, s, getXp(p, Source.DIAMOND_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.EMERALD_ORE)) {
				leveler.addXp(p, s, getXp(p, Source.EMERALD_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(Material.NETHERRACK)) {
				leveler.addXp(p, s, getXp(p, Source.NETHERRACK));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.BLACKSTONE.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, Source.BLACKSTONE));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.BASALT.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, Source.BASALT));
			}
			else if (mat.equals(XMaterial.NETHER_GOLD_ORE.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, Source.NETHER_GOLD_ORE));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.ANCIENT_DEBRIS.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, Source.ANCIENT_DEBRIS));
				applyAbilities(p, b);
			}
			else if (mat.equals(XMaterial.END_STONE.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, Source.END_STONE));
			}
			else if (mat.equals(XMaterial.OBSIDIAN.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, Source.OBSIDIAN));
			}
			else if (mat.equals(XMaterial.MAGMA_BLOCK.parseMaterial())) {
				leveler.addXp(p, s, getXp(p, Source.MAGMA_BLOCK));
			}
			else if (XMaterial.isNewVersion()) {
				if (mat.equals(XMaterial.TERRACOTTA.parseMaterial())) {
					leveler.addXp(p, s, getXp(p, Source.TERRACOTTA));
				}
				else if (mat.equals(XMaterial.RED_TERRACOTTA.parseMaterial())) {
					leveler.addXp(p, s, getXp(p, Source.RED_TERRACOTTA));
				}
				else if (mat.equals(XMaterial.ORANGE_TERRACOTTA.parseMaterial())) {
					leveler.addXp(p, s, getXp(p, Source.ORANGE_TERRACOTTA));
				}
				else if (mat.equals(XMaterial.YELLOW_TERRACOTTA.parseMaterial())) {
					leveler.addXp(p, s, getXp(p, Source.YELLOW_TERRACOTTA));
				}
				else if (mat.equals(XMaterial.WHITE_TERRACOTTA.parseMaterial())) {
					leveler.addXp(p, s, getXp(p, Source.WHITE_TERRACOTTA));
				}
				else if (mat.equals(XMaterial.LIGHT_GRAY_TERRACOTTA.parseMaterial())) {
					leveler.addXp(p, s, getXp(p, Source.LIGHT_GRAY_TERRACOTTA));
				}
				else if (mat.equals(XMaterial.BROWN_TERRACOTTA.parseMaterial())) {
					leveler.addXp(p, s, getXp(p, Source.BROWN_TERRACOTTA));
				}
			}
			else {
				if (mat.equals(XMaterial.TERRACOTTA.parseMaterial())) {
					leveler.addXp(p, s, getXp(p, Source.TERRACOTTA));
				}
				else if (mat.equals(XMaterial.WHITE_TERRACOTTA.parseMaterial())) {
					switch (b.getData()) {
						case 0:
							leveler.addXp(p, s, getXp(p, Source.WHITE_TERRACOTTA));
							break;
						case 1:
							leveler.addXp(p, s, getXp(p, Source.ORANGE_TERRACOTTA));
							break;
						case 4:
							leveler.addXp(p, s, getXp(p, Source.YELLOW_TERRACOTTA));
							break;
						case 8:
							leveler.addXp(p, s, getXp(p, Source.LIGHT_GRAY_TERRACOTTA));
							break;
						case 12:
							leveler.addXp(p, s, getXp(p, Source.BROWN_TERRACOTTA));
							break;
						case 14:
							leveler.addXp(p, s, getXp(p, Source.RED_TERRACOTTA));
							break;
					}
				}
			}
			// Check custom blocks
			checkCustomBlocks(p, b, s);
		}
	}
	
	private void applyAbilities(Player p, Block b) {
		miningAbilities.luckyMiner(p, b);
	}
}
