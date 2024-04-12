package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.ItemConsumeXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemConsumeLeveler extends SourceLeveler {

    public ItemConsumeLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.ITEM_CONSUME);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        var skillSource = getSource(event.getItem());
        if (skillSource == null) return;

        ItemConsumeXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        if (failsChecks(event, player, player.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(plugin.getUser(player), skill, source, source.getXp());
    }

    @Nullable
    private SkillSource<ItemConsumeXpSource> getSource(ItemStack item) {
        for (SkillSource<ItemConsumeXpSource> entry : plugin.getSkillManager().getSourcesOfType(ItemConsumeXpSource.class)) {
            if (plugin.getItemRegistry().passesFilter(item, entry.source().getItem())) { // Return source that passes item filter
                return entry;
            }
        }
        return null;
    }

}
