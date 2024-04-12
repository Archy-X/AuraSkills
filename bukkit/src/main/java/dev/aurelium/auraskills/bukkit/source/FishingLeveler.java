package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.FishingXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceTypes;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FishingLeveler extends SourceLeveler {

    public FishingLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.FISHING);
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

        var skillSource = getSource(item);
        if (skillSource == null) return;

        FishingXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        if (failsChecks(event, player, itemEntity.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(plugin.getUser(player), skill, source, source.getXp());
    }

    @Nullable
    public SkillSource<FishingXpSource> getSource(ItemStack item) {
        for (SkillSource<FishingXpSource> entry : plugin.getSkillManager().getSourcesOfType(FishingXpSource.class)) {
            if (plugin.getItemRegistry().passesFilter(item, entry.source().getItem(), entry.skill())) { // Return source that passes item filter
                return entry;
            }
        }
        return null;
    }

}
