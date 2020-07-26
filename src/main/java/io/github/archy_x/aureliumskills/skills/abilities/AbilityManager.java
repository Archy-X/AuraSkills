package io.github.archy_x.aureliumskills.skills.abilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AbilityManager implements Listener {

	private Map<UUID, Map<Ability, Integer>> cooldowns;
	private Map<UUID, Map<Ability, Boolean>> ready;
	private Map<UUID, Map<Ability, Boolean>> activated;
	private Map<UUID, Map<Ability, Integer>> errorTimer;
	
	private Map<UUID, List<RightClickAbility>> activeAbilities;
	
	private Plugin plugin;
	
	public AbilityManager(Plugin plugin) {
		this.plugin = plugin;
		cooldowns = new HashMap<>();
		ready = new HashMap<>();
		activated = new HashMap<>();
		errorTimer = new HashMap<>();
		activeAbilities = new HashMap<>();
	}
	
	public void init() {
		startTimer();
		startUpdating();
	}

	//Sets cooldown
	public void setCooldown(UUID id, Ability ability, int cooldown) {
		cooldowns.get(id).put(ability, cooldown);
	}
	
	//Gets cooldown
	public int getCooldown(UUID id, Ability ability) {
		if (cooldowns.get(id).containsKey(ability)) {
			return cooldowns.get(id).get(ability);
		}
		else {
			cooldowns.get(id).put(ability, 0);
			return 0;
		}
	}
	
	//Gets if ability is ready
	public boolean isReady(UUID id, Ability ability) {
		if (ready.get(id).containsKey(ability)) {
			return ready.get(id).get(ability);
		}
		else {
			ready.get(id).put(ability, false);
			return false;
		}
	}
	
	//Gets the error timer
	public int getErrorTimer(UUID id, Ability ability) {
		if (errorTimer.get(id).containsKey(ability)) {
			return errorTimer.get(id).get(ability);
		}
		else {
			errorTimer.get(id).put(ability, 2);
			return 0;
		}
	}
	
	//Sets error timerr
	public void setErrorTimer(UUID id, Ability ability, int time) {
		errorTimer.get(id).put(ability, time);
	}
	
	//Gets if ability is ready
	public boolean isActivated(UUID id, Ability ability) {
		if (activated.containsKey(id)) {
			if (activated.get(id).containsKey(ability)) {
				return activated.get(id).get(ability);
			} else {
				activated.get(id).put(ability, false);
				return false;
			}
		}
		else {
			activated.put(id, new HashMap<>());
			return false;
		}
	}
	
	//Activates an ability
	public void activateAbility(Player player, Ability ability, int durationTicks, RightClickAbility rca) {
		activated.get(player.getUniqueId()).put(ability, true);
		activeAbilities.get(player.getUniqueId()).add(rca);
		rca.start(player);
		//Schedules stop
		new BukkitRunnable() {
			@Override
			public void run() {
				rca.stop(player);
				activeAbilities.get(player.getUniqueId()).remove(rca);
				activated.get(player.getUniqueId()).put(ability, false);
				ready.get(player.getUniqueId()).put(ability, false);
			}
		}.runTaskLater(plugin, durationTicks);
	}
	
	//Sets ability ready status
	public void setReady(UUID id, Ability ability, boolean isReady) {
		ready.get(id).put(ability, isReady);
	}
	
	private void startUpdating() {
		//Updates active abilities
		new BukkitRunnable() {
			@Override
			public void run() {
				for (UUID id : activeAbilities.keySet()) {
					for (RightClickAbility ab : activeAbilities.get(id)) {
						if (Bukkit.getPlayer(id) != null) {
							ab.update(Bukkit.getPlayer(id));
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0L, 1L);
	}
	
	private void startTimer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (UUID id : cooldowns.keySet()) {
					for (Ability ab : cooldowns.get(id).keySet()) {
						if (cooldowns.get(id).get(ab) > 0) {
							cooldowns.get(id).put(ab, cooldowns.get(id).get(ab) - 1);
						}
					}
				}
				for (UUID id : errorTimer.keySet()) {
					for (Ability ab : errorTimer.get(id).keySet()) {
						if (errorTimer.get(id).get(ab) > 0) {
							errorTimer.get(id).put(ab, errorTimer.get(id).get(ab) - 1);
						}
					}
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		UUID id = event.getPlayer().getUniqueId();
		if (!cooldowns.containsKey(id)) {
			cooldowns.put(id, new HashMap<Ability, Integer>());
		}
		if (!ready.containsKey(id)) {
			ready.put(id, new HashMap<Ability, Boolean>());
		}
		if (!activated.containsKey(id)) {
			activated.put(id, new HashMap<Ability, Boolean>());
		}
		if (!errorTimer.containsKey(id)) {
			errorTimer.put(id, new HashMap<Ability, Integer>());
		}
		if (!activeAbilities.containsKey(id)) {
			activeAbilities.put(id, new LinkedList<RightClickAbility>());
		}
	}
}
