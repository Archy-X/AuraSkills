package com.archyx.aureliumskills.skills.fishing;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.api.event.LootDropCause;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.loot.handler.LootHandler;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.source.Source;
import com.archyx.aureliumskills.support.WorldGuardFlags;
import com.archyx.aureliumskills.util.version.VersionUtils;
import com.archyx.lootmanager.loot.Loot;
import com.archyx.lootmanager.loot.LootPool;
import com.archyx.lootmanager.loot.LootTable;
import com.archyx.lootmanager.loot.type.CommandLoot;
import com.archyx.lootmanager.loot.type.ItemLoot;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class FishingLootHandler extends LootHandler implements Listener {

    private final Random random = new Random();

    public FishingLootHandler(AureliumSkills plugin) {
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
            else if (plugin.getWorldGuardSupport().blockedByFlag(player.getLocation(), player, WorldGuardFlags.FlagKey.CUSTOM_LOOT)) {
                return;
            }
        }

        if (!(event.getCaught() instanceof Item)) return;
        if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) return;
        if (event.getExpToDrop() == 0) return;

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) return;

        ItemStack originalItem = ((Item) event.getCaught()).getItemStack();
        FishingSource originalSource = FishingSource.valueOf(originalItem);

        LootTable table = plugin.getLootTableManager().getLootTable(Skills.FISHING);
        if (table == null) return;
        for (LootPool pool : table.getPools()) {
            // Check if in open water
            if (pool.getOption("require_open_water", Boolean.class, false) && VersionUtils.isAtLeastVersion(16, 5)) {
                if (!event.getHook().isInOpenWater()) continue;
            }
            // Calculate chance for pool
            Source source;
            double chance = getCommonChance(pool, playerData);
            LootDropCause cause = LootDropCause.FISHING_OTHER_LOOT;
            if (pool.getName().equals("rare") && plugin.getAbilityManager().isEnabled(Ability.TREASURE_HUNTER)) {
                chance += (getValue(Ability.TREASURE_HUNTER, playerData) / 100);
                source = FishingSource.RARE;
                cause = LootDropCause.TREASURE_HUNTER;
            } else if (pool.getName().equals("epic") && plugin.getAbilityManager().isEnabled(Ability.EPIC_CATCH)) {
                chance += (getValue(Ability.EPIC_CATCH, playerData) / 100);
                source = FishingSource.EPIC;
                cause = LootDropCause.EPIC_CATCH;
            } else {
                source = originalSource;
            }

            if (random.nextDouble() < chance) { // Pool is selected
                Loot selectedLoot = selectLoot(pool, originalSource);
                // Give loot
                if (selectedLoot != null) {
                    if (selectedLoot instanceof ItemLoot) {
                        ItemLoot itemLoot = (ItemLoot) selectedLoot;
                        giveFishingItemLoot(player, itemLoot, event, source, cause);
                    } else if (selectedLoot instanceof CommandLoot) {
                        CommandLoot commandLoot = (CommandLoot) selectedLoot;
                        giveCommandLoot(player, commandLoot, source);
                    }
                    break;
                }
            }
        }
    }
}
