package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.menu.MenuLoader;
import com.archyx.aureliumskills.util.text.TextUtil;
import fr.minuskube.inv.content.SlotPos;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class ConfigurableItem {

    protected final AureliumSkills plugin;
    protected final ItemType itemType;
    protected SlotPos pos;
    protected ItemStack baseItem;
    protected String displayName;
    protected List<String> lore;
    protected Map<Integer, Set<String>> lorePlaceholders;
    protected final String[] definedPlaceholders;

    public ConfigurableItem(AureliumSkills plugin, ItemType itemType, String[] definedPlaceholders) {
        this.plugin = plugin;
        this.itemType = itemType;
        this.definedPlaceholders = definedPlaceholders;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public SlotPos getPos() {
        return pos;
    }

    protected List<String> applyPlaceholders(List<String> lore, Player player) {
        if (plugin.isPlaceholderAPIEnabled() && OptionL.getBoolean(Option.MENUS_PLACEHOLDER_API)) {
            List<String> appliedList = new ArrayList<>();
            for (String entry : lore) {
                appliedList.add(PlaceholderAPI.setPlaceholders(player, entry));
            }
            return appliedList;
        }
        return lore;
    }

    protected String applyPlaceholders(String input, Player player) {
        if (plugin.isPlaceholderAPIEnabled() && OptionL.getBoolean(Option.MENUS_PLACEHOLDER_API)) {
            return PlaceholderAPI.setPlaceholders(player, input);
        }
        return input;
    }

    public void load(ConfigurationSection config) {
        try {
            this.pos = SlotPos.of(config.getInt("row"), config.getInt("column"));
            this.baseItem = MenuLoader.parseItem(Objects.requireNonNull(config.getString("material")));
            this.displayName = TextUtil.replace(Objects.requireNonNull(config.getString("display_name")), "&", "§");
            // Load lore
            this.lore = new ArrayList<>();
            this.lorePlaceholders = new HashMap<>();
            int lineNum = 0;
            for (String line : config.getStringList("lore")) {
                Set<String> linePlaceholders = new HashSet<>();
                this.lore.add(TextUtil.replace(line, "&", "§"));
                // Find lore placeholders
                for (String placeholder : definedPlaceholders) {
                    if (line.contains("{" + placeholder + "}")) {
                        linePlaceholders.add(placeholder);
                    }
                }
                this.lorePlaceholders.put(lineNum, linePlaceholders);
                lineNum++;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("[AureliumSkills] Error parsing item " + itemType.toString() + ", check error above for details!");
        }
    }
}
