package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.type.ItemConsumeXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceType;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemConsumeLeveler extends SourceLeveler {

    public ItemConsumeLeveler(AuraSkills plugin) {
        super(plugin, SourceType.ITEM_CONSUME);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        var sourcePair = getSource(event.getItem());
        if (sourcePair == null) return;

        ItemConsumeXpSource source = sourcePair.first();
        Skill skill = sourcePair.second();

        if (failsChecks(event, player, player.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(plugin.getUser(player), skill, source.getXp());
    }

    private Pair<ItemConsumeXpSource, Skill> getSource(ItemStack item) {
        for (Map.Entry<ItemConsumeXpSource, Skill> entry : plugin.getSkillManager().getSourcesOfType(ItemConsumeXpSource.class).entrySet()) {
            if (plugin.getItemRegistry().passesFilter(item, entry.getKey().getItem())) { // Return source that passes item filter
                return Pair.fromEntry(entry);
            }
        }
        return null;
    }

}
