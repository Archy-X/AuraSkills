package com.archyx.aureliumskills.menus.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
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

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LockedManaAbilityItem extends AbstractItem implements TemplateItemProvider<MAbility> {

    private final ManaAbilityManager manager;

    public LockedManaAbilityItem(AureliumSkills plugin) {
        super(plugin);
        this.manager = plugin.getManaAbilityManager();
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
                return "&fDescription:\n  &7" + TextUtil.replace(mAbility.getDescription(locale),
                        "{value}", NumberUtil.format1(manager.getDisplayValue(mAbility, 1)),
                        "{haste_level}", String.valueOf(manager.getOptionAsInt(mAbility, "haste_level", 10)),
                        "{duration}", NumberUtil.format1(getDuration(mAbility)));
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

    private double getDuration(MAbility mAbility) {
        if (mAbility == MAbility.LIGHTNING_BLADE) {
            return manager.getOptionAsDouble(MAbility.LIGHTNING_BLADE, "base_duration");
        } else {
            return manager.getValue(mAbility, 1);
        }
    }

}
