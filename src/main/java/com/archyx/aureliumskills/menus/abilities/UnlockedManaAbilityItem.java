package com.archyx.aureliumskills.menus.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class UnlockedManaAbilityItem extends AbstractItem implements TemplateItemProvider<MAbility> {

    public UnlockedManaAbilityItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<MAbility> getContext() {
        return MAbility.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderType type, MAbility mAbility) {
        Locale locale = plugin.getLang().getLocale(player);
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (playerData == null) return placeholder;
        switch (placeholder) {
            case "name":
                return mAbility.getDisplayName(locale);
            case "desc":
                if (mAbility != MAbility.SPEED_MINE) {
                    return TextUtil.replace(mAbility.getDescription(locale), "{value}",
                            String.valueOf(plugin.getManaAbilityManager().getValue(mAbility, playerData.getManaAbilityLevel(mAbility))));
                } else {
                    return TextUtil.replace(mAbility.getDescription(locale), "{value}",
                            String.valueOf(plugin.getManaAbilityManager().getValue(mAbility, playerData.getManaAbilityLevel(mAbility))),
                            "{haste_level}",
                            String.valueOf(plugin.getManaAbilityManager().getOptionAsInt(mAbility, "haste_level", 10)));
                }
            case "level":
                if (isNotMaxed(playerData, mAbility)) {
                    return "&7Level: &f" + playerData.getManaAbilityLevel(mAbility);
                } else {
                    return "&7Level: &f" + playerData.getManaAbilityLevel(mAbility) + " &6(MAXED)";
                }
            case "next_level_desc":
                if (isNotMaxed(playerData, mAbility)) {
                    if (mAbility != MAbility.SPEED_MINE) {
                        return "\n \n&fNext level: &7" + TextUtil.replace(mAbility.getDescription(locale), "{value}",
                                String.valueOf(plugin.getManaAbilityManager().getValue(mAbility, playerData.getManaAbilityLevel(mAbility) + 1)));
                    } else {
                        return "\n \n&fNext level: &7" + TextUtil.replace(mAbility.getDescription(locale), "{value}",
                                String.valueOf(plugin.getManaAbilityManager().getValue(mAbility, playerData.getManaAbilityLevel(mAbility) + 1)),
                                "{haste_level}",
                                String.valueOf(plugin.getManaAbilityManager().getOptionAsInt(mAbility, "haste_level", 10)));
                    }
                } else {
                    return "";
                }
            case "unlocked":
                return "&a&lUNLOCKED";
        }
        return placeholder;
    }

    private boolean isNotMaxed(PlayerData playerData, MAbility mAbility) {
        int maxLevel = plugin.getManaAbilityManager().getMaxLevel(mAbility);
        int unlock = plugin.getManaAbilityManager().getUnlock(mAbility);
        int levelUp = plugin.getManaAbilityManager().getLevelUp(mAbility);
        if (maxLevel == 0) {
            maxLevel = OptionL.getMaxLevel(mAbility.getSkill());
        }
        return (unlock + levelUp * (playerData.getManaAbilityLevel(mAbility) + 1)) <= maxLevel;
    }

    @Override
    public Set<MAbility> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        Set<MAbility> unlockedManaAbilities = new HashSet<>();
        if (playerData != null) {
            // Add abilities that player has not unlocked yet
            MAbility mAbility = skill.getManaAbility();
            if (mAbility != null && playerData.getManaAbilityLevel(mAbility) >= 1) {
                unlockedManaAbilities.add(mAbility);
            }
        }
        return unlockedManaAbilities;
    }

    @Override
    public SlotPos getSlotPos(Player player, ActiveMenu activeMenu, MAbility context) {
        return SlotPos.of(2,4);
    }

}
