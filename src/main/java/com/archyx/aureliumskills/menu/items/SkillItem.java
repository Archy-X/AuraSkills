package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.menu.MenuLoader;
import com.archyx.aureliumskills.menu.templates.SkillInfoItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.util.text.TextUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SkillItem extends ConfigurableItem {

    private final SkillInfoItem skillInfoItem;
    private final Map<Skill, ItemStack> baseItems = new HashMap<>();

    public SkillItem(AureliumSkills plugin) {
        super(plugin, ItemType.SKILL, new String[] {"skill_desc", "stats_leveled", "ability_levels", "mana_ability", "level", "progress_to_level", "max_level"});
        this.skillInfoItem = new SkillInfoItem(plugin);
    }

    @Override
    public void load(ConfigurationSection config) {
        try {
            pos = SlotPos.of(config.getInt("row"), config.getInt("column"));
            // Load base items
            for (String materialInput : config.getStringList("material")) {
                String[] splitInput = materialInput.split(" ", 2);
                Skill skill = Skills.valueOf(splitInput[0]);
                baseItems.put(skill, MenuLoader.parseItem(splitInput[1]));
            }
            displayName = TextUtil.replace(Objects.requireNonNull(config.getString("display_name")),"&", "§");
            // Load lore
            List<String> lore = new ArrayList<>();
            Map<Integer, Set<String>> lorePlaceholders = new HashMap<>();
            int lineNum = 0;
            for (String line : config.getStringList("lore")) {
                Set<String> linePlaceholders = new HashSet<>();
                lore.add(TextUtil.replace(line,"&", "§"));
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
            Bukkit.getLogger().warning("[AureliumSkills] Error parsing item " + itemType.toString() + ", check error above for details!");
        }
    }

    public ItemStack getItem(Skill skill, PlayerData playerData, Player player, Locale locale) {
        ItemStack baseItem = baseItems.get(skill);
        if (baseItem == null) {
            baseItem = new ItemStack(Material.STONE);
        }
        baseItem = baseItem.clone();
        return skillInfoItem.getItem(baseItem, skill, playerData, locale, displayName, lore, lorePlaceholders, player);
    }
}
