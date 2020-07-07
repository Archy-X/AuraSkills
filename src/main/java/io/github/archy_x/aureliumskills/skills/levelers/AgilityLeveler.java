package io.github.archy_x.aureliumskills.skills.levelers;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Sets;

import io.github.archy_x.aureliumskills.skills.Skill;
import io.github.archy_x.aureliumskills.skills.SkillLoader;

public class AgilityLeveler implements Listener {
	
	private Set<UUID> prevPlayersOnGround = Sets.newHashSet();
	private Plugin plugin;
	
	public AgilityLeveler(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFall(EntityDamageEvent event) {
		if (event.isCancelled() == false) {
			if (event.getCause().equals(DamageCause.FALL)) {
				if (event.getEntity() instanceof Player) {
					Player player = (Player) event.getEntity();
					if (event.getFinalDamage() < player.getHealth()) {
						if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
							SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.AGILITY, event.getDamage() * 2);
							Leveler.playSound(player);
							Leveler.checkLevelUp(player, Skill.AGILITY);
							Leveler.sendActionBarMessage(player, Skill.AGILITY, event.getDamage() * 2);
						}
					}
				}
			}
		}
	}
	
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getVelocity().getY() > 0) {
            double jumpVelocity = (double) 0.42F;
            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                jumpVelocity += (double) ((float) (player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() + 1) * 0.1F);
            }
            if (e.getPlayer().getLocation().getBlock().getType() != Material.LADDER && prevPlayersOnGround.contains(player.getUniqueId())) {
                if (!player.isOnGround() && Double.compare(player.getVelocity().getY(), jumpVelocity) == 0) {
                	if (player.hasMetadata("skillsJumps")) {
                		player.setMetadata("skillsJumps", new FixedMetadataValue(plugin, player.getMetadata("skillsJumps").get(0).asInt() + 1));
                		if (player.getMetadata("skillsJumps").get(0).asInt() >= 50) {
                			if (SkillLoader.playerSkills.containsKey(player.getUniqueId())) {
        						SkillLoader.playerSkills.get(player.getUniqueId()).addXp(Skill.AGILITY, 5);
        						Leveler.playSound(player);
        						Leveler.checkLevelUp(player, Skill.AGILITY);
        						Leveler.sendActionBarMessage(player, Skill.AGILITY, 5);
        					}
                			player.removeMetadata("skillsJumps", plugin);
                		}
                	}
                	else {
                		player.setMetadata("skillsJumps", new FixedMetadataValue(plugin, 1));
                	}
                }
            }
        }
        if (player.isOnGround()) {
            prevPlayersOnGround.add(player.getUniqueId());
        } else {
            prevPlayersOnGround.remove(player.getUniqueId());
        }
    }
	
}
