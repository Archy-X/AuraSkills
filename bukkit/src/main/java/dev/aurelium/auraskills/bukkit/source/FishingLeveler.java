package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.type.FishingXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceType;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FishingLeveler extends SourceLeveler {

    public FishingLeveler(AuraSkills plugin) {
        super(plugin, SourceType.FISHING);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFish(PlayerFishEvent event) {
        if (disabled()) return;
        if (!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            return;
        }
        if (!(event.getCaught() instanceof Item itemEntity)) {
            return;
        }
        ItemStack item = itemEntity.getItemStack();
        Player player = event.getPlayer();

        var sourcePair = getSource(item);
        if (sourcePair == null) return;

        FishingXpSource source = sourcePair.first();
        Skill skill = sourcePair.second();

        if (failsChecks(event, player, itemEntity.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(plugin.getUser(player), skill, source.getXp());
    }

    @Nullable
    private Pair<FishingXpSource, Skill> getSource(ItemStack item) {
        for (Map.Entry<FishingXpSource, Skill> entry : plugin.getSkillManager().getSourcesOfType(FishingXpSource.class).entrySet()) {
            if (plugin.getItemRegistry().passesFilter(item, entry.getKey().getItem(), entry.getValue())) { // Return source that passes item filter
                return Pair.fromEntry(entry);
            }
        }
        return null;
    }

}
