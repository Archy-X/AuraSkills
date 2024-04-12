package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.type.GrindstoneXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.List;
import java.util.Map;

public class GrindstoneLeveler extends SourceLeveler {

    public GrindstoneLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.GRINDSTONE);
    }

    @EventHandler
    public void onGrindstoneDisenchant(InventoryClickEvent event) {
        if (disabled()) return;
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (failsClickChecks(event)) return;

        if (!inventory.getType().equals(InventoryType.GRINDSTONE)) {
            return;
        }

        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

        var skillSource = plugin.getSkillManager().getSingleSourceOfType(GrindstoneXpSource.class);
        if (skillSource == null) return;

        GrindstoneXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        Location location = inventory.getLocation() != null ? inventory.getLocation() : player.getLocation();

        if (failsChecks(event, player, location, skill)) return;

        double multiplier = getTotalLevelMultiplier(source, inventory, skill);

        plugin.getLevelManager().addXp(plugin.getUser(player), skill, source, multiplier * source.getXp());
    }

    private double getTotalLevelMultiplier(GrindstoneXpSource source, Inventory inventory, Skill skill) {
        // Get the repair cost multiplier from placeholder
        double multiplier = 1;
        String multiplierString = source.getMultiplier();
        if (multiplierString != null) {
            int totalLevel = 0;
            ItemStack topItem = inventory.getItem(0); // Get item in top slot
            totalLevel += getTotalLevel(topItem);
            ItemStack bottomItem = inventory.getItem(1); // Get item in bottom slot
            totalLevel += getTotalLevel(bottomItem);

            multiplierString = TextUtil.replace(multiplierString, "{total_level}", String.valueOf(totalLevel));
            try {
                multiplier = Double.parseDouble(multiplierString);
            } catch (NumberFormatException e) {
                plugin.logger().warn("Invalid multiplier for grindstone source " + source.getId() + " in skill " + skill.getId());
                e.printStackTrace();
            }
        }
        return multiplier;
    }

    private int getTotalLevel(ItemStack item) {
        int totalLevel = 0;
        if (item != null) {
            for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
                if (isDisenchantable(entry.getKey())) {
                    totalLevel += entry.getValue();
                }
            }
            if (item.getItemMeta() instanceof EnchantmentStorageMeta esm) {
                for (Map.Entry<Enchantment, Integer> entry : esm.getStoredEnchants().entrySet()) {
                    if (isDisenchantable(entry.getKey())) {
                        totalLevel += entry.getValue();
                    }
                }
            }
        }
        return totalLevel;
    }

    public boolean isDisenchantable(Enchantment enchant) {
        // Block vanilla curses
        if (enchant.equals(Enchantment.BINDING_CURSE) || enchant.equals(Enchantment.VANISHING_CURSE)) {
            return false;
        }
        // Check blocked list in config
        List<String> blockedList = plugin.configStringList(Option.SOURCE_GRINDSTONE_BLOCKED_ENCHANTS);
        for (String blockedEnchantName : blockedList) {
            if (enchant.getKey().getKey().equalsIgnoreCase(blockedEnchantName)) {
                return false;
            }
        }
        return true;
    }

}
