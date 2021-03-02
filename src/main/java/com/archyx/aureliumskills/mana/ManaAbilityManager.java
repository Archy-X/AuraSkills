package com.archyx.aureliumskills.mana;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.api.event.ManaAbilityActivateEvent;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.configuration.OptionValue;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ManaAbilityManager implements Listener {

    private final Map<UUID, Map<MAbility, Integer>> cooldowns;
    private final Map<UUID, Map<MAbility, Boolean>> ready;
    private final Map<UUID, Map<MAbility, Boolean>> activated;
    private final Map<UUID, Map<MAbility, Integer>> errorTimer;

    private final Map<UUID, List<ManaAbility>> activeAbilities;
    private final ManaAbilityActivator activator;

    private final AureliumSkills plugin;

    public ManaAbilityManager(AureliumSkills plugin) {
        this.plugin = plugin;
        cooldowns = new HashMap<>();
        ready = new HashMap<>();
        activated = new HashMap<>();
        errorTimer = new HashMap<>();
        activeAbilities = new HashMap<>();
        activator = new ManaAbilityActivator(plugin);
    }

    public void init() {
        startTimer();
        startUpdating();
    }

    public ManaAbilityActivator getActivator() {
        return activator;
    }

    //Sets cooldown
    public void setPlayerCooldown(UUID id, MAbility ability, int cooldown) {
        cooldowns.get(id).put(ability, cooldown);
    }

    public void setPlayerCooldown(Player player, MAbility mAbility) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData != null) {
            double cooldown = getCooldown(mAbility, playerData);
            if (cooldown != 0) {
                setPlayerCooldown(player.getUniqueId(), mAbility, (int) (cooldown * 20));
            }
        }
    }

    //Gets cooldown
    public int getPlayerCooldown(UUID id, MAbility ability) {
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

    //Sets error timer
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
        ManaAbilityActivateEvent event = new ManaAbilityActivateEvent(player, ability, durationTicks);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            activated.get(player.getUniqueId()).put(ability, true);
            activeAbilities.get(player.getUniqueId()).add(manaAbility);
            manaAbility.activate(player);
            int duration = event.getDuration();
            if (duration != 0) {
                //Schedules stop
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        manaAbility.onStop(player);
                        activeAbilities.get(player.getUniqueId()).remove(manaAbility);
                        activated.get(player.getUniqueId()).put(ability, false);
                        ready.get(player.getUniqueId()).put(ability, false);
                    }
                }.runTaskLater(plugin, duration);
            } else {
                manaAbility.onStop(player);
                activeAbilities.get(player.getUniqueId()).remove(manaAbility);
                activated.get(player.getUniqueId()).put(ability, false);
                ready.get(player.getUniqueId()).put(ability, false);
            }
        }
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
            }
        }.runTaskTimer(plugin, 0L, 1L);
        new BukkitRunnable() {
            @Override
            public void run() {
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
            cooldowns.put(id, new HashMap<>());
        }
        if (!ready.containsKey(id)) {
            ready.put(id, new HashMap<>());
        }
        if (!activated.containsKey(id)) {
            activated.put(id, new HashMap<>());
        }
        if (!errorTimer.containsKey(id)) {
            errorTimer.put(id, new HashMap<>());
        }
        if (!activeAbilities.containsKey(id)) {
            activeAbilities.put(id, new LinkedList<>());
        }
    }

    public double getValue(MAbility mAbility, int level) {
        return getBaseValue(mAbility) + (getValuePerLevel(mAbility) * (level - 1));
    }

    public double getValue(MAbility mAbility, PlayerData playerData) {
        return getBaseValue(mAbility) + (getValuePerLevel(mAbility) * (playerData.getManaAbilityLevel(mAbility) - 1));
    }

    public double getDisplayValue(MAbility mAbility, int level) {
        if (mAbility != MAbility.SHARP_HOOK) {
            return getBaseValue(mAbility) + (getValuePerLevel(mAbility) * (level - 1));
        }
        else {
            if (getOptionAsBooleanElseTrue(mAbility, "display_damage_with_scaling")) {
                return (getBaseValue(mAbility) + (getValuePerLevel(mAbility) * (level - 1))) * OptionL.getDouble(Option.HEALTH_HP_INDICATOR_SCALING);
            } else {
                return getBaseValue(mAbility) + (getValuePerLevel(mAbility) * (level - 1));
            }
        }
    }

    public double getBaseValue(MAbility mAbility) {
        ManaAbilityOption option = plugin.getAbilityManager().getAbilityOption(mAbility);
        if (option != null) {
            return option.getBaseValue();
        }
        return mAbility.getDefaultBaseValue();
    }

    public double getValuePerLevel(MAbility mAbility) {
        ManaAbilityOption option = plugin.getAbilityManager().getAbilityOption(mAbility);
        if (option != null) {
            return option.getValuePerLevel();
        }
        return mAbility.getDefaultValuePerLevel();
    }

    public double getCooldown(MAbility mAbility, int level) {
        double cooldown = getBaseCooldown(mAbility) + (getCooldownPerLevel(mAbility) * (level - 1));
        return cooldown > 0 ? cooldown : 0;
    }

    public double getCooldown(MAbility mAbility, PlayerData playerData) {
        double cooldown = getBaseCooldown(mAbility) + (getCooldownPerLevel(mAbility) * (playerData.getManaAbilityLevel(mAbility) - 1));
        return cooldown > 0 ? cooldown : 0;
    }

    public double getBaseCooldown(MAbility mAbility) {
        ManaAbilityOption option = plugin.getAbilityManager().getAbilityOption(mAbility);
        if (option != null) {
            return option.getBaseCooldown();
        }
        return mAbility.getDefaultBaseCooldown();
    }

    public double getCooldownPerLevel(MAbility mAbility) {
        ManaAbilityOption option = plugin.getAbilityManager().getAbilityOption(mAbility);
        if (option != null) {
            return option.getCooldownPerLevel();
        }
        return mAbility.getDefaultCooldownPerLevel();
    }

    public double getManaCost(MAbility mAbility, PlayerData playerData) {
        return getBaseManaCost(mAbility) + (getManaCostPerLevel(mAbility) * (playerData.getManaAbilityLevel(mAbility) - 1));
    }

    public double getManaCost(MAbility mAbility, int level) {
        return getBaseManaCost(mAbility) + (getManaCostPerLevel(mAbility) * (level - 1));
    }

    public double getBaseManaCost(MAbility mAbility) {
        ManaAbilityOption option = plugin.getAbilityManager().getAbilityOption(mAbility);
        if (option != null) {
            return option.getBaseManaCost();
        }
        return mAbility.getDefaultBaseManaCost();
    }

    public double getManaCostPerLevel(MAbility mAbility) {
        ManaAbilityOption option = plugin.getAbilityManager().getAbilityOption(mAbility);
        if (option != null) {
            return option.getManaCostPerLevel();
        }
        return mAbility.getDefaultManaCostPerLevel();
    }

    public int getUnlock(MAbility mAbility) {
        ManaAbilityOption option = plugin.getAbilityManager().getAbilityOption(mAbility);
        if (option != null) {
            return option.getUnlock();
        }
        return 7;
    }

    public int getLevelUp(MAbility mAbility) {
        ManaAbilityOption option = plugin.getAbilityManager().getAbilityOption(mAbility);
        if (option != null) {
            return option.getLevelUp();
        }
        return 7;
    }

    public int getMaxLevel(MAbility mAbility) {
        ManaAbilityOption option = plugin.getAbilityManager().getAbilityOption(mAbility);
        if (option != null) {
            return option.getMaxLevel();
        }
        return 0;
    }

    /**
     * Gets the mana ability unlocked or leveled up at a certain level
     * @param skill The skill
     * @param level The skill level
     * @return The mana ability unlocked or leveled up, or null
     */
    @Nullable
    public MAbility getManaAbility( Skill skill, int level) {
        MAbility mAbility = skill.getManaAbility();
        if (mAbility != null) {
            if (level >= getUnlock(mAbility) && (level - getUnlock(mAbility)) % getLevelUp(mAbility) == 0) {
                return mAbility;
            }
        }
        return null;
    }

    @Nullable
    public OptionValue getOption(MAbility mAbility, String key) {
        ManaAbilityOption option = plugin.getAbilityManager().getAbilityOption(mAbility);
        if (option != null) {
            return option.getOption(key);
        } else {
            return mAbility.getDefaultOptions().get(key);
        }
    }

    public boolean getOptionAsBooleanElseTrue(MAbility mAbility, String key) {
        OptionValue value = getOption(mAbility, key);
        if (value != null) {
            return value.asBoolean();
        }
        return true;
    }

    public boolean getOptionAsBooleanElseFalse(MAbility mAbility, String key) {
        OptionValue value = getOption(mAbility, key);
        if (value != null) {
            return value.asBoolean();
        }
        return false;
    }

    @Nullable
    public Set<String> getOptionKeys(MAbility mAbility) {
        if (mAbility.getDefaultOptions() != null) {
            return mAbility.getDefaultOptions().keySet();
        }
        return null;
    }

}
