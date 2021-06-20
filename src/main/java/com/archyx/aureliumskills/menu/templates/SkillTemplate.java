package com.archyx.aureliumskills.menu.templates;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.menu.MenuLoader;
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

public class SkillTemplate extends ConfigurableTemplate {

    private final SkillInfoItem skillInfoItem;
    private final Map<Skill, SlotPos> positions = new HashMap<>();
    private final Map<Skill, ItemStack> baseItems = new HashMap<>();
    
    public SkillTemplate(AureliumSkills plugin) {
        super(plugin, TemplateType.SKILL,new String[] {"skill_desc", "stats_leveled", "ability_levels", "mana_ability", "level", "progress_to_level", "max_level", "skill_click"} );
        this.skillInfoItem = new SkillInfoItem(plugin);
    }

    @Override
    public void load(ConfigurationSection config) {
        try {
            for (String posInput : config.getStringList("pos")) {
                String[] splitInput = posInput.split(" ");
                Skill skill = plugin.getSkillRegistry().getSkill(splitInput[0]);
                if (skill != null) {
                    int row = Integer.parseInt(splitInput[1]);
                    int column = Integer.parseInt(splitInput[2]);
                    positions.put(skill, SlotPos.of(row, column));
                }
            }
            // Load base items
            for (String materialInput : config.getStringList("material")) {
                String[] splitInput = materialInput.split(" ", 2);
                Skill skill;
                try {
                    skill = plugin.getSkillRegistry().getSkill(splitInput[0]);
                }
                catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("[AureliumSkills] Error while loading SKILL template, " + splitInput[0].toUpperCase() + " is not a valid skill! Using FARMING as a default");
                    skill = Skills.FARMING;
                }
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
            Bukkit.getLogger().warning("[AureliumSkills] Error parsing template " + templateType.toString() + ", check error above for details!");
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

    public SlotPos getPosition(Skill skill) {
        return positions.get(skill);
    }

}
