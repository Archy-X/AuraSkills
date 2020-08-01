package io.github.archy_x.aureliumskills.skills.abilities.mana_abilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ManaAbilityManager implements Listener {

    private Map<UUID, Map<MAbility, Integer>> cooldowns;
    private Map<UUID, Map<MAbility, Boolean>> ready;
    private Map<UUID, Map<MAbility, Boolean>> activated;
    private Map<UUID, Map<MAbility, Integer>> errorTimer;

    private Map<UUID, List<ManaAbility>> activeAbilities;

    private Plugin plugin;

    public ManaAbilityManager(Plugin plugin) {
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
    public void setCooldown(UUID id, MAbility ability, int cooldown) {
        cooldowns.get(id).put(ability, cooldown);
    }

    //Gets cooldown
    public int getCooldown(UUID id, MAbility ability) {
        if (!cooldowns.containsKey(id)) {
            return 0;
        }
        if (cooldowns.get(id).containsKey(ability)) {
            return cooldowns.get(id).get(ability);
        }
        else {
            cooldowns.get(id).put(ability, 0);
            return 0;
        }
    }

    //Gets if ability is ready
    public boolean isReady(UUID id, MAbility ability) {
        if (!ready.containsKey(id)) {
            return false;
        }
        if (ready.get(id).containsKey(ability)) {
            return ready.get(id).get(ability);
        }
        else {
            ready.get(id).put(ability, false);
            return false;
        }
    }

    //Gets the error timer
    public int getErrorTimer(UUID id, MAbility ability) {
        if (!errorTimer.containsKey(id)) {
            return 0;
        }
        if (errorTimer.get(id).containsKey(ability)) {
            return errorTimer.get(id).get(ability);
        }
        else {
            errorTimer.get(id).put(ability, 2);
            return 0;
        }
    }

    //Sets error timerr
    public void setErrorTimer(UUID id, MAbility ability, int time) {
        if (!errorTimer.containsKey(id)) {
            return;
        }
        errorTimer.get(id).put(ability, time);
    }

    //Gets if ability is ready
    public boolean isActivated(UUID id, MAbility ability) {
        if (!activated.containsKey(id)) {
            return false;
        }
        if (activated.get(id).containsKey(ability)) {
            return activated.get(id).get(ability);
        } else {
            activated.get(id).put(ability, false);
            return false;
        }
    }

    //Activates an ability
    public void activateAbility(Player player, MAbility ability, int durationTicks, ManaAbility manaAbility) {
        activated.get(player.getUniqueId()).put(ability, true);
        activeAbilities.get(player.getUniqueId()).add(manaAbility);
        manaAbility.activate(player);
        //Schedules stop
        new BukkitRunnable() {
            @Override
            public void run() {
                manaAbility.stop(player);
                activeAbilities.get(player.getUniqueId()).remove(manaAbility);
                activated.get(player.getUniqueId()).put(ability, false);
                ready.get(player.getUniqueId()).put(ability, false);
            }
        }.runTaskLater(plugin, durationTicks);
    }

    //Sets ability ready status
    public void setReady(UUID id, MAbility ability, boolean isReady) {
        if (!ready.containsKey(id)) {
            return;
        }
        ready.get(id).put(ability, isReady);
    }

    private void startUpdating() {
        //Updates active abilities
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID id : activeAbilities.keySet()) {
                    for (ManaAbility ab : activeAbilities.get(id)) {
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
                    for (MAbility ab : cooldowns.get(id).keySet()) {
                        if (cooldowns.get(id).get(ab) > 0) {
                            cooldowns.get(id).put(ab, cooldowns.get(id).get(ab) - 1);
                        }
                    }
                }
                for (UUID id : errorTimer.keySet()) {
                    for (MAbility ab : errorTimer.get(id).keySet()) {
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
            cooldowns.put(id, new HashMap<MAbility, Integer>());
        }
        if (!ready.containsKey(id)) {
            ready.put(id, new HashMap<MAbility, Boolean>());
        }
        if (!activated.containsKey(id)) {
            activated.put(id, new HashMap<MAbility, Boolean>());
        }
        if (!errorTimer.containsKey(id)) {
            errorTimer.put(id, new HashMap<MAbility, Integer>());
        }
        if (!activeAbilities.containsKey(id)) {
            activeAbilities.put(id, new LinkedList<ManaAbility>());
        }
    }

}
