package com.archyx.aureliumskills.loot.listener;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.loot.LootPool;
import com.archyx.aureliumskills.loot.LootTable;
import com.archyx.aureliumskills.loot.type.CommandLoot;
import com.archyx.aureliumskills.loot.type.ItemLoot;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.skills.fishing.FishingSource;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.support.WorldGuardFlags;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Random;

public class FishingListener extends LootHandler implements Listener {

    private final Random random = new Random();

    public FishingListener(AureliumSkills plugin) {
        super(plugin, Skills.FISHING, Ability.FISHER);
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
            Source originalSource = null;
            double chance = pool.getBaseChance();
            if (pool.getName().equals("rare") && plugin.getAbilityManager().isEnabled(Ability.TREASURE_HUNTER)) {
                chance += (getValue(Ability.TREASURE_HUNTER, playerData) / 100);
                originalSource = FishingSource.RARE;
            } else if (pool.getName().equals("epic") && plugin.getAbilityManager().isEnabled(Ability.EPIC_CATCH)) {
                chance += (getValue(Ability.EPIC_CATCH, playerData) / 100);
                originalSource = FishingSource.EPIC;
            }

            if (random.nextDouble() < chance) { // Pool is selected
                Loot selectedLoot = selectLoot(pool);
                // Give loot
                if (selectedLoot != null) {
                    if (selectedLoot instanceof ItemLoot) {
                        ItemLoot itemLoot = (ItemLoot) selectedLoot;
                        giveFishingItemLoot(player, itemLoot, event, originalSource);
                    } else if (selectedLoot instanceof CommandLoot) {
                        CommandLoot commandLoot = (CommandLoot) selectedLoot;
                        giveCommandLoot(player, commandLoot, originalSource);
                    }
                    break;
                }
            }
        }
    }

}
