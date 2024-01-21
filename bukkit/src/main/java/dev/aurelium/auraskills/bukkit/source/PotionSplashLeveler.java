package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.type.PotionSplashXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PotionSplashLeveler extends SourceLeveler {

    public PotionSplashLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.POTION_SPLASH);
    }

    @EventHandler
    public void onSplash(PotionSplashEvent event) {
        handle(event);
    }

    @EventHandler
    public void onLingeringSplash(LingeringPotionSplashEvent event) {
        handle(event);
    }

    public void handle(ProjectileHitEvent event) {
        if (disabled()) return;

        // Only handle potion splash events
        if (!(event instanceof PotionSplashEvent) && !(event instanceof LingeringPotionSplashEvent)) return;

        ThrownPotion potion = (ThrownPotion) event.getEntity();

        if (!(potion.getShooter() instanceof Player player)) return; // Ignore non-player potion throwers

        if (potion.getEffects().isEmpty()) return; // Ignore potions with no effects

        ItemStack item = potion.getItem();

        var sourcePair = getSource(item);
        if (sourcePair == null) return;

        PotionSplashXpSource source = sourcePair.first();
        Skill skill = sourcePair.second();

        if (failsChecks(event, player, potion.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(plugin.getUser(player), skill, source.getXp());
    }

    @Nullable
    private Pair<PotionSplashXpSource, Skill> getSource(ItemStack item) {
        for (Map.Entry<PotionSplashXpSource, Skill> entry : plugin.getSkillManager().getSourcesOfType(PotionSplashXpSource.class).entrySet()) {
            if (plugin.getItemRegistry().passesFilter(item, entry.getKey().getItem())) { // Return source that passes item filter
                return Pair.fromEntry(entry);
            }
        }
        return null;
    }

}
