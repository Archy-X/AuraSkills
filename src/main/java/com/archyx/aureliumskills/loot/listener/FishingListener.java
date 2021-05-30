package com.archyx.aureliumskills.loot.listener;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.abilities.Ability;
import com.archyx.aureliumskills.abilities.AbilityProvider;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.loot.LootPool;
import com.archyx.aureliumskills.loot.LootTable;
import com.archyx.aureliumskills.loot.type.FishingItemLoot;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.support.WorldGuardFlags;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;
import java.util.Random;

public class FishingListener extends AbilityProvider implements Listener {

    private final Random random = new Random();

    public FishingListener(AureliumSkills plugin) {
        super(plugin, Skills.FISHING);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFish(PlayerFishEvent event) {
        if (!OptionL.isEnabled(Skills.FISHING)) return;
        Player player = event.getPlayer();
        if (blockAbility(player)) return;
        if (plugin.getWorldManager().isInBlockedWorld(player.getLocation())) {
            return;
        }
        if (plugin.isWorldGuardEnabled()) {
            if (plugin.getWorldGuardSupport().isInBlockedRegion(player.getLocation())) {
                return;
            }
            // Check if blocked by flags
            else if (plugin.getWorldGuardSupport().blockedByFlag(player.getLocation(), player, WorldGuardFlags.FlagKey.XP_GAIN)) {
                return;
            }
        }

        if (!(event.getCaught() instanceof Item)) return;
        if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) return;
        if (event.getExpToDrop() == 0) return;

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) return;

        LootTable table = plugin.getLootTableManager().getLootTable(Skills.FISHING);
        if (table == null) return;
        for (LootPool pool : table.getPools()) {
            // Calculate chance for pool
            double chance = pool.getBaseChance();
            if (pool.getName().equals("rare") && plugin.getAbilityManager().isEnabled(Ability.TREASURE_HUNTER)) {
                chance *= 1 + (getValue(Ability.TREASURE_HUNTER, playerData) / 100);
            } else if (pool.getName().equals("epic") && plugin.getAbilityManager().isEnabled(Ability.EPIC_CATCH)) {
                chance *= 1 + (getValue(Ability.EPIC_CATCH, playerData) / 100);
            }

            if (random.nextDouble() < chance) { // Pool is selected
                List<Loot> lootList = pool.getLoot();
                // Loot selected based on weight
                int totalWeight = 0;
                for (Loot loot : lootList) {
                    totalWeight += loot.getWeight();
                }
                int selected = random.nextInt(totalWeight);
                int currentWeight = 0;
                Loot selectedLoot = null;
                for (Loot loot : lootList) {
                    if (selected >= currentWeight && selected < currentWeight + loot.getWeight()) {
                        selectedLoot = loot;
                        break;
                    }
                    currentWeight += loot.getWeight();
                }

                // Give loot
                if (selectedLoot != null) {
                    if (selectedLoot instanceof FishingItemLoot) {
                        FishingItemLoot fishingItemLoot = (FishingItemLoot) selectedLoot;
                        fishingItemLoot.giveLoot(event);
                    }
                    break;
                }
            }
        }
    }

}
