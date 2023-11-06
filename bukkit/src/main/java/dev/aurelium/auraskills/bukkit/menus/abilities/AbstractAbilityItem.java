package dev.aurelium.auraskills.bukkit.menus.abilities;

import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import dev.aurelium.auraskills.api.util.NumberUtil;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAbilityItem extends AbstractItem implements TemplateItemProvider<Ability> {

    private final String itemName;

    public AbstractAbilityItem(AuraSkills plugin, String itemName) {
        super(plugin);
        this.itemName = itemName;
    }

    @Override
    public Class<Ability> getContext() {
        return Ability.class;
    }

    @Override
    public SlotPos getSlotPos(Player player, ActiveMenu activeMenu, Ability ability) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        Object obj =  activeMenu.getItemOption(itemName, "slots");
        if (obj instanceof List<?>) {
            List<String> slots = DataUtil.castStringList(obj);
            // Get the index of the ability
            int index = 0;
            for (Ability skillAbility : skill.getAbilities()) {
                if (skillAbility.isEnabled() && skill.isEnabled()) {
                    if (skillAbility == ability) {
                        break;
                    }
                    index++;
                }
            }
            if (slots.size() > index) {
                String slot = slots.get(index);
                return parseSlot(slot);
            }
        }
        // Default slots
        List<Ability> abilityList = new ArrayList<>();
        for (Ability skillAbility : skill.getAbilities()) {
            if (skillAbility.isEnabled() && skill.isEnabled()) {
                abilityList.add(skillAbility);
            }
        }
        int index = abilityList.indexOf(ability);
        return SlotPos.of(1, 2 + index);
    }

    private SlotPos parseSlot(String slotString) {
        String[] split = slotString.split(",", 2);
        if (split.length == 2) {
            return SlotPos.of(NumberUtil.toInt(split[0]), NumberUtil.toInt(split[1]));
        } else {
            int num = NumberUtil.toInt(split[0]);
            int row = num / 9;
            int column = num % 9;
            return SlotPos.of(row, column);
        }
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, Ability ability) {
        // Hide abilities that are disabled
        if (!ability.isEnabled()) {
            return null;
        }
        return baseItem;
    }
}
