package com.archyx.aureliumskills.menus.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.menus.common.AbstractItem;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.math.RomanNumber;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import fr.minuskube.inv.content.SlotPos;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LockedManaAbilityItem extends AbstractItem implements TemplateItemProvider<MAbility> {

    public LockedManaAbilityItem(AureliumSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<MAbility> getContext() {
        return MAbility.class;
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderType type, MAbility mAbility) {
        Locale locale = plugin.getLang().getLocale(player);
        Skill skill = (Skill) menu.getProperty("skill");
        switch (placeholder) {
            case "name":
                return mAbility.getDisplayName(locale);
            case "desc":
                return mAbility.getDescription(locale);
            case "unlocked_at":
                return "&7Unlocked at &3" + skill.getDisplayName(locale) + " " + RomanNumber.toRoman(plugin.getManaAbilityManager().getUnlock(mAbility));
            case "locked":
                return "&c&lLOCKED";
        }
        return placeholder;
    }

    @Override
    public Set<MAbility> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Set<MAbility> lockedManaAbilities = new HashSet<>();
        Skill skill = (Skill) activeMenu.getProperty("skill");
        MAbility mAbility = skill.getManaAbility();
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        if (mAbility != null && playerData != null) {
            if (playerData.getManaAbilityLevel(mAbility) <= 0) {
                lockedManaAbilities.add(mAbility);
            }
        }
        return lockedManaAbilities;
    }

    @Override
    public SlotPos getSlotPos(Player player, ActiveMenu activeMenu, MAbility context) {
        return SlotPos.of(2,4);
    }
}
