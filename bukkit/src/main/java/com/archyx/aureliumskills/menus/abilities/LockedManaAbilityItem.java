package com.archyx.aureliumskills.menus.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.aureliumskills.mana.ManaAbilityManager;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.math.RomanNumber;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LockedManaAbilityItem extends AbstractManaAbilityItem implements TemplateItemProvider<MAbility> {

    private final ManaAbilityManager manager;

    public LockedManaAbilityItem(AureliumSkills plugin) {
        super(plugin);
        this.manager = plugin.getManaAbilityManager();
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderData data, MAbility mAbility) {
        Locale locale = plugin.getLang().getLocale(player);
        Skill skill = (Skill) menu.getProperty("skill");
        switch (placeholder) {
            case "name":
                return mAbility.getDisplayName(locale);
            case "locked_desc":
                return TextUtil.replace(Lang.getMessage(MenuMessage.LOCKED_DESC, locale),
                        "{desc}", TextUtil.replace(mAbility.getDescription(locale),
                            "{value}", NumberUtil.format1(manager.getDisplayValue(mAbility, 1)),
                            "{haste_level}", String.valueOf(manager.getOptionAsInt(mAbility, "haste_level", 10)),
                            "{duration}", NumberUtil.format1(getDuration(mAbility))));
            case "unlocked_at":
                return TextUtil.replace(Lang.getMessage(MenuMessage.UNLOCKED_AT, locale),
                        "{skill}", skill.getDisplayName(locale),
                        "{level}", RomanNumber.toRoman(plugin.getManaAbilityManager().getUnlock(mAbility)));
            case "locked":
                return Lang.getMessage(MenuMessage.LOCKED, locale);
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

    private double getDuration(MAbility mAbility) {
        if (mAbility == MAbility.LIGHTNING_BLADE) {
            return manager.getOptionAsDouble(MAbility.LIGHTNING_BLADE, "base_duration");
        } else {
            return manager.getValue(mAbility, 1);
        }
    }

}
