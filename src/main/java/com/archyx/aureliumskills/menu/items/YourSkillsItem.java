package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menu.MenuLoader;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.LoreUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class YourSkillsItem implements ConfigurableItem {

    private final ItemType TYPE = ItemType.YOUR_SKILLS;

    private SlotPos pos;
    private ItemStack baseItem;
    private String displayName;
    private List<String> lore;
    private Map<Integer, Set<String>> lorePlaceholders;
    private final String[] definedPlaceholders = new String[] {"desc", "hover", "click"};

    @Override
    public ItemType getType() {
        return TYPE;
    }

    @Override
    public void load(ConfigurationSection config) {
        try {
            pos = SlotPos.of(config.getInt("row"), config.getInt("column"));
            baseItem = MenuLoader.parseItem(Objects.requireNonNull(config.getString("material")));
            displayName = LoreUtil.replace(Objects.requireNonNull(config.getString("display_name")),"&", "§");
            lore = new ArrayList<>();
            lorePlaceholders = new HashMap<>();
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
        }
        catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("[AureliumSkills] Error parsing item " + TYPE.toString() + ", check error above for details!");
        }
    }

    public ItemStack getItem(Player player, Locale locale) {
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(LoreUtil.replace(displayName,"{your_skills}", LoreUtil.replace(Lang.getMessage(MenuMessage.YOUR_SKILLS, locale)
                    ,"{player}", player.getName())));
            List<String> builtLore = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                Set<String> placeholders = lorePlaceholders.get(i);
                for (String placeholder : placeholders) {
                    switch (placeholder) {
                        case "desc":
                            line = LoreUtil.setPlaceholders("desc", MenuMessage.YOUR_SKILLS_DESC, locale, line);
                            break;
                        case "hover":
                            line = LoreUtil.setPlaceholders("hover", MenuMessage.YOUR_SKILLS_HOVER, locale, line);
                            break;
                        case "click":
                            line = LoreUtil.setPlaceholders("click", MenuMessage.YOUR_SKILLS_CLICK, locale, line);
                            break;
                    }
                }
                builtLore.add(line);
            }
            meta.setLore(ItemUtils.formatLore(builtLore));
            item.setItemMeta(meta);
        }
        return item;
    }

    public SlotPos getPos() {
        return pos;
    }
}
