package com.archyx.aureliumskills.modifier;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.SkillLoader;
import com.archyx.aureliumskills.skills.abilities.ForagingAbilities;
import com.archyx.aureliumskills.skills.abilities.MiningAbilities;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ItemListener implements Listener {

    private final Plugin plugin;
    private final Map<Player, ItemStack> heldItems;


    public ItemListener(Plugin plugin) {
        this.plugin = plugin;
        heldItems = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();
        heldItems.put(player, held);
        if (SkillLoader.playerStats.containsKey(player.getUniqueId())) {
            PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
            if (!held.getType().equals(Material.AIR)) {
                for (StatModifier modifier : ItemModifier.getItemModifiers(held)) {
                    playerStat.addModifier(modifier, false);
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        heldItems.remove(player);
    }

    public void scheduleTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                //For every player
                for (Player player : Bukkit.getOnlinePlayers()) {
                    //Gets stats profile
                    PlayerStat playerStat = SkillLoader.playerStats.get(player.getUniqueId());
                    if (playerStat != null) {
                        //Gets stored and held items
                        ItemStack stored = heldItems.get(player);
                        ItemStack held = player.getInventory().getItemInMainHand();
                        //If stored item is no null
                        if (stored != null) {
                            //If stored item is different than held
                            if (!stored.equals(held)) {
                                //Remove modifiers from stored item
                                if (!stored.getType().equals(Material.AIR)) {
                                    for (StatModifier modifier : ItemModifier.getItemModifiers(stored)) {
                                        playerStat.removeModifier(modifier.getName());
                                    }
                                    //Remove valor
                                    if (ItemUtils.isAxe(stored.getType())) {
                                        ForagingAbilities.removeValor(playerStat);
                                    }
                                    //Remove stamina
                                    if (ItemUtils.isPickaxe(stored.getType())) {
                                        MiningAbilities.removeStamina(playerStat);
                                    }
                                }
                                //Add modifiers from held item
                                if (!held.getType().equals(Material.AIR)) {
                                    for (StatModifier modifier : ItemModifier.getItemModifiers(held)) {
                                        playerStat.addModifier(modifier);
                                    }
                                    //Apply valor
                                    if (ItemUtils.isAxe(held.getType())) {
                                        ForagingAbilities.applyValor(player, playerStat);
                                    }
                                    //Apply stamina
                                    if (ItemUtils.isPickaxe(held.getType())) {
                                        MiningAbilities.applyStamina(player, playerStat);
                                    }
                                }
                                //Set stored item to held item
                                heldItems.put(player, held);
                            }
                        }
                        //If no mapping exists, add held item
                        else {
                            heldItems.put(player, held);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, OptionL.getInt(Option.MODIFIER_ITEM_CHECK_PERIOD));
    }

}
