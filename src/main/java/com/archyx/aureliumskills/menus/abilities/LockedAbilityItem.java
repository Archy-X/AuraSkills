package com.archyx.aureliumskills.menus.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.math.RomanNumber;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Supplier;

public class LockedAbilityItem extends AbstractItem implements TemplateItemProvider<Ability> {

    public LockedAbilityItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Ability> getContext() {
        return Ability.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderType type, Ability ability) {
        Locale locale = plugin.getLang().getLocale(player);
        switch (placeholder) {
            case "name":
                return ability.getDisplayName(locale);
            case "desc":
                return "&fDescription:\n  &7" + TextUtil.replace(ability.getDescription(locale),
                        "{value}", NumberUtil.format1(plugin.getAbilityManager().getValue(ability, 1)),
                        "{value_2}", NumberUtil.format1(plugin.getAbilityManager().getValue2(ability, 1)));
            case "unlocked_at":
                Skill skill = (Skill) menu.getProperty("skill");
                return "&7Unlocked at &3" + skill.getDisplayName(locale) + " " + RomanNumber.toRoman(plugin.getAbilityManager().getUnlock(ability));
            case "locked":
                return "&c&lLOCKED";
        }
        return placeholder;
    }

    @Override
    public Set<Ability> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        Set<Ability> lockedAbilities = new HashSet<>();
        if (playerData != null) {
            // Add abilities that player has not unlocked yet
            for (Supplier<Ability> abilitySupplier : skill.getAbilities()) {
                Ability ability = abilitySupplier.get();
                if (playerData.getAbilityLevel(ability) <= 0) {
                    lockedAbilities.add(ability);
                }
            }
        }
        return lockedAbilities;
    }

    @Override
    public SlotPos getSlotPos(Player player, ActiveMenu activeMenu, Ability ability) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        List<Ability> abilityList = new ArrayList<>();
        for (Supplier<Ability> abilitySupplier : skill.getAbilities()) {
            abilityList.add(abilitySupplier.get());
        }
        int index = abilityList.indexOf(ability);
        return SlotPos.of(1, 2 + index);
    }
}
