package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.menu.MenuLoader;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.ItemUtils;
import com.archyx.aureliumskills.util.LoreUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class RankItem implements ConfigurableItem {

    private final ItemType TYPE = ItemType.RANK;

    private SlotPos pos;
    private ItemStack baseItem;
    private String displayName;
    private List<String> lore;
    private Map<Integer, Set<String>> lorePlaceholders;
    private final String[] definedPlaceholders = new String[] {"out_of", "percent"};
    private final NumberFormat nf = new DecimalFormat("#.##");

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
            // Load lore
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

    public ItemStack getItem(Skill skill, Player player, Locale locale) {
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        int rank = AureliumSkills.leaderboard.getSkillRank(skill, player.getUniqueId());
        int size = AureliumSkills.leaderboard.getSize();
        if (meta != null) {
            meta.setDisplayName(LoreUtil.replace(displayName,"{your_ranking}", Lang.getMessage(MenuMessage.YOUR_RANKING, locale)));
            List<String> builtLore = new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);
                Set<String> placeholders = lorePlaceholders.get(i);
                for (String placeholder : placeholders) {
                    if (placeholder.equals("out_of")) {
                        line = LoreUtil.replace(line,"{out_of}", LoreUtil.replace(Lang.getMessage(MenuMessage.RANK_OUT_OF, locale),"{rank}", String.valueOf(rank), "{total}", String.valueOf(size)));
                    }
                    else if (placeholder.equals("percent")) {
                        double percent = (double) rank / (double) size * 100;
                        if (percent > 1) {
                            line = LoreUtil.replace(line,"{percent}", LoreUtil.replace(Lang.getMessage(MenuMessage.RANK_PERCENT, locale),"{percent}", String.valueOf((int) percent)));
                        }
                        else {
                            line = LoreUtil.replace(line,"{percent}", LoreUtil.replace(Lang.getMessage(MenuMessage.RANK_PERCENT, locale),"{percent}", nf.format(percent)));
                        }
                    }
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
