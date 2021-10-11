package com.archyx.aureliumskills.menus.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Supplier;

public class UnlockedAbilityItem extends AbstractItem implements TemplateItemProvider<Ability> {

    public UnlockedAbilityItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Ability> getContext() {
        return Ability.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderType type, Ability ability) {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) return placeholder;
        Locale locale = plugin.getLang().getLocale(player);
        switch (placeholder) {
            case "name":
                return ability.getDisplayName(locale);
            case "desc":
                return TextUtil.replace(ability.getDescription(locale), "{value}",
                        String.valueOf(plugin.getAbilityManager().getValue(ability, playerData.getAbilityLevel(ability))));
            case "level":
                if (isNotMaxed(playerData, ability)) {
                    return "&7Level: &f" + playerData.getAbilityLevel(ability);
                } else {
                    return "&7Level: &f" + playerData.getAbilityLevel(ability) + " &6(MAXED)";
                }
            case "next_level_desc":
                if (isNotMaxed(playerData, ability)) {
                    return "\n \n&fNext level: &7" + TextUtil.replace(ability.getDescription(locale), "{value}",
                            String.valueOf(plugin.getAbilityManager().getValue(ability, playerData.getAbilityLevel(ability) + 1)));
                } else {
                    return "";
                }
            case "unlocked":
                return "&a&lUNLOCKED";
        }
        return placeholder;
    }

    private boolean isNotMaxed(PlayerData playerData, Ability ability) {
        int maxLevel = plugin.getAbilityManager().getMaxLevel(ability);
        int unlock = plugin.getAbilityManager().getUnlock(ability);
        int levelUp = plugin.getAbilityManager().getLevelUp(ability);
        if (maxLevel == 0) {
            maxLevel = OptionL.getMaxLevel(ability.getSkill());
        }
        return (unlock + levelUp * (playerData.getAbilityLevel(ability) + 1)) <= maxLevel;
    }

    @Override
    public Set<Ability> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        Set<Ability> unlockedAbilities = new HashSet<>();
        if (playerData != null) {
            // Add abilities that player has not unlocked yet
            for (Supplier<Ability> abilitySupplier : skill.getAbilities()) {
                Ability ability = abilitySupplier.get();
                if (playerData.getAbilityLevel(ability) >= 1) {
                    unlockedAbilities.add(ability);
                }
            }
        }
        return unlockedAbilities;
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
