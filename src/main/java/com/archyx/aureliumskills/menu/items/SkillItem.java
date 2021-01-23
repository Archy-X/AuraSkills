package com.archyx.aureliumskills.menu.items;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.menu.MenuLoader;
import com.archyx.aureliumskills.menu.templates.SkillInfoItem;
import com.archyx.aureliumskills.skills.PlayerSkill;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.LoreUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SkillItem extends SkillInfoItem implements ConfigurableItem {

    private final ItemType TYPE = ItemType.SKILL;

    private SlotPos pos;
    private final Map<Skill, ItemStack> baseItems = new HashMap<>();
    private String displayName;
    private List<String> lore;
    private Map<Integer, Set<String>> lorePlaceholders;
    private final String[] definedPlaceholders = new String[] {"skill_desc", "primary_stat", "secondary_stat", "ability_levels", "mana_ability", "level", "progress_to_level", "max_level"};

    public SkillItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public ItemType getType() {
        return TYPE;
    }

    @Override
    public void load(ConfigurationSection config) {
        try {
            pos = SlotPos.of(config.getInt("row"), config.getInt("column"));
            // Load base items
            for (String materialInput : config.getStringList("material")) {
                String[] splitInput = materialInput.split(" ", 2);
                Skill skill = Skill.valueOf(splitInput[0]);
                baseItems.put(skill, MenuLoader.parseItem(splitInput[1]));
            }
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

    public ItemStack getItem(Skill skill, PlayerSkill playerSkill, Locale locale) {
        ItemStack baseItem = baseItems.get(skill);
        if (baseItem == null) {
            baseItem = new ItemStack(Material.STONE);
        }
        baseItem = baseItem.clone();
        return getItem(baseItem, skill, playerSkill, locale, displayName, lore, lorePlaceholders);
    }

    @Override
    public SlotPos getPos() {
        return pos;
    }
}
