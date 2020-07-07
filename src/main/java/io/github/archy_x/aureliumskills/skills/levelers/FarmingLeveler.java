package io.github.archy_x.aureliumskills.skills.levelers;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Crops;
import org.bukkit.material.NetherWarts;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.skills.abilities.FarmingAbilities;
import io.github.archy_x.aureliumskills.util.XMaterial;

@SuppressWarnings("deprecation")
public class FarmingLeveler implements Listener{

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (event.isCancelled() == false) {
			Material mat = event.getBlock().getType();
			if (mat.equals(XMaterial.WHEAT.parseMaterial())) {
				Crops crops = (Crops) event.getBlock().getState().getData();
				if (crops.getState().equals(CropState.RIPE)) {
					addXp(player, 2.0, event.getBlock());
					FarmingAbilities.onReplenish(player, event.getBlock(), XMaterial.WHEAT.parseMaterial());
				}
			}
			else if (mat.equals(Material.CARROT)) {
				Crops crops = (Crops) event.getBlock().getState().getData();
				if (crops.getState().equals(CropState.RIPE)) {
					addXp(player, 2.7, event.getBlock());
					FarmingAbilities.onReplenish(player, event.getBlock(), Material.CARROT);
				}
			}
			else if (mat.equals(Material.POTATO)) {
				Crops crops = (Crops) event.getBlock().getState().getData();
				if (crops.getState().equals(CropState.RIPE)) {
					addXp(player, 2.5, event.getBlock());
					FarmingAbilities.onReplenish(player, event.getBlock(), Material.POTATO);
				}
			}
			else if (mat.equals(XMaterial.BEETROOT.parseMaterial())) {
				Crops crops = (Crops) event.getBlock().getState().getData();
				if (crops.getState().equals(CropState.RIPE)) {
					addXp(player, 3.0, event.getBlock());
					FarmingAbilities.onReplenish(player, event.getBlock(), XMaterial.BEETROOT.parseMaterial());
				}
			}
			else if (mat.equals(XMaterial.NETHER_WART.parseMaterial())) {
				NetherWarts crops = (NetherWarts) event.getBlock().getState().getData();
				if (crops.getState().equals(NetherWartsState.RIPE)) {
					addXp(player, 3.0, event.getBlock());
					FarmingAbilities.onReplenish(player, event.getBlock(), XMaterial.NETHER_WART.parseMaterial());
				}
			}
			else if (mat.equals(Material.PUMPKIN)) {
				if (!event.getBlock().hasMetadata("skillsPlaced")) {
					addXp(player, 3.4, event.getBlock());
				}
			}
			else if (mat.equals(XMaterial.MELON.parseMaterial())) {
				if (!event.getBlock().hasMetadata("skillsPlaced")) {
					addXp(player, 3.4, event.getBlock());
				}
			}
		}
	}
	
	public void addXp(Player player, double amount, Block b) {
		if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
			FarmingAbilities.bountifulHarvest(player, b);
			FarmingAbilities.tripleHarvest(player, b);
			double xpGain = FarmingAbilities.getModifiedXp(player, amount); //Applies Ability Modifiers
			SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.FARMING, xpGain);
			Leveler.playSound(player);
			Leveler.checkLevelUp(player, Skill.FARMING);
			Leveler.sendActionBarMessage(player, Skill.FARMING, xpGain);
		}
	}
	
}
