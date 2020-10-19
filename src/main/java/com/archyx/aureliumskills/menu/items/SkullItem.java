package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.stats.PlayerStat;
import com.archyx.aureliumskills.stats.Stat;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.LoreUtil;
import dev.dbassett.skullcreator.SkullCreator;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SkullItem implements ConfigurableItem {

    private final ItemType TYPE = ItemType.SKULL;

    private SlotPos pos;
    private String displayName;
    private List<String> lore;
    private Map<Integer, Set<String>> lorePlaceholders;
    private final String[] definedPlaceholders = new String[] {"strength", "health", "regeneration", "luck", "wisdom", "toughness"};

    @Override
    public ItemType getType() {
        return TYPE;
    }

    @Override
    public void load(ConfigurationSection config) {
        try {
            pos = SlotPos.of(config.getInt("row"), config.getInt("column"));
            displayName = LoreUtil.replace(Objects.requireNonNull(config.getString("display_name")),"&", "§");
            // Load lore
            List<String> lore = new ArrayList<>();
            Map<Integer, Set<String>> lorePlaceholders = new HashMap<>();
            int lineNum = 0;
            for (String line : config.getStringList("lore")) {
                Set<String> linePlaceholders = new HashSet<>();
                lore.add(LoreUtil.replace(line,"&", "§"));
                // Find lore placeholders
                for (String placeholder : definedPlaceholders) {
                    if (line.contains("{" + placeholder + "}")) {
                        linePlaceholders.add(placeholder);
                    }
                }
                lorePlaceholders.put(lineNum, linePlaceholders);
                lineNum++;
            }
            this.lore = lore;
            this.lorePlaceholders = lorePlaceholders;
        }
        catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("[AureliumSkills] Error parsing item " + TYPE.toString() + ", check error above for details!");
        }
    }

    public ItemStack getItem(Player player, PlayerStat playerStat, Locale locale) {
        ItemStack item = SkullCreator.itemFromUuid(player.getUniqueId());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(LoreUtil.replace(displayName,"{player}", player.getName()));
            List<String> builtLore = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                Set<String> placeholders = lorePlaceholders.get(i);
                for (String placeholder : placeholders) {
                    Stat stat = Stat.valueOf(placeholder.toUpperCase());
                    line = LoreUtil.replace(line,"{" + placeholder + "}", LoreUtil.replace(Lang.getMessage(MenuMessage.PLAYER_STAT_ENTRY, locale)
                            ,"{color}", stat.getColor(locale)
                            ,"{symbol}", stat.getSymbol(locale)
                            ,"{stat}", stat.getDisplayName(locale)
                            ,"{level}", String.valueOf(playerStat.getStatLevel(stat))));
                }
                builtLore.add(line);
            }
            meta.setLore(ItemUtils.formatLore(builtLore));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public SlotPos getPos() {
        return pos;
    }
}
