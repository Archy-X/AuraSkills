package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.EnchantingXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EnchantingLeveler extends SourceLeveler {

    public EnchantingLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.ENCHANTING);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchant(EnchantItemEvent event) {
        if (disabled()) return;

        Player player = event.getEnchanter();

        var skillSource = getSource(event.getItem());
        if (skillSource == null) return;

        EnchantingXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        if (failsChecks(event, player, event.getEnchantBlock().getLocation(), skill)) return;

        User user = plugin.getUser(player);

        double xp = source.getXp();
        String unit = source.getUnit();
        if (unit != null) {
            if (unit.equals("{sources.units.enchant_level}")) {
                // Get the sum of levels of enchants added
                int totalLevel = event.getEnchantsToAdd().values().stream().reduce(Integer::sum).orElse(0);
                xp *= totalLevel;
            } else if (unit.equals("{sources.units.exp_requirement")) {
                xp *= event.getExpLevelCost();
            }
        }

        plugin.getLevelManager().addXp(user, skill, source, xp);
    }

    @Nullable
    private SkillSource<EnchantingXpSource> getSource(ItemStack item) {
        for (SkillSource<EnchantingXpSource> entry : plugin.getSkillManager().getSourcesOfType(EnchantingXpSource.class)) {
            if (plugin.getItemRegistry().passesFilter(item, entry.source().getItem())) { // Return source that passes item filter
                return entry;
            }
        }
        return null;
    }

}
