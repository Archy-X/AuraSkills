package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.PotionSplashXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceTypes;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

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

        var skillSource = getSource(item);
        if (skillSource == null) return;

        PotionSplashXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        if (failsChecks(event, player, potion.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(plugin.getUser(player), skill, source, source.getXp());
    }

    @Nullable
    private SkillSource<PotionSplashXpSource> getSource(ItemStack item) {
        for (SkillSource<PotionSplashXpSource> entry : plugin.getSkillManager().getSourcesOfType(PotionSplashXpSource.class)) {
            if (plugin.getItemRegistry().passesFilter(item, entry.source().getItem())) { // Return source that passes item filter
                return entry;
            }
        }
        return null;
    }

}
