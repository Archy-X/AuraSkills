package com.archyx.aureliumskills.menus.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.ability.Ability;
import com.archyx.aureliumskills.data.PlayerData;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.lang.MenuMessage;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.math.RomanNumber;
import com.archyx.aureliumskills.util.text.TextUtil;
import com.archyx.slate.item.provider.PlaceholderType;
import com.archyx.slate.menu.ActiveMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

public class LockedAbilityItem extends AbstractAbilityItem {

    public LockedAbilityItem(AureliumSkills plugin) {
        super(plugin, "locked_ability");
    }

    @Override
    public @NotNull String onPlaceholderReplace(@NotNull String placeholder, @NotNull Player player, @NotNull ActiveMenu menu, @NotNull PlaceholderType type, @NotNull Ability ability) {
        @Nullable Locale locale = plugin.getLang().getLocale(player);
        @Nullable String m = placeholder;
        switch (placeholder) {
            case "name":
                m = ability.getDisplayName(locale);
                break;
            case "locked_desc":
                m = TextUtil.replace(Lang.getMessage(MenuMessage.LOCKED_DESC, locale),
                        "{desc}", TextUtil.replace(ability.getDescription(locale),
                        "{value}", NumberUtil.format1(plugin.getAbilityManager().getValue(ability, 1)),
                        "{value_2}", NumberUtil.format1(plugin.getAbilityManager().getValue2(ability, 1))));
                break;
            case "unlocked_at":
                @Nullable Object property = menu.getProperty("skill");
                assert (null != property);
                Skill skill = (Skill) property;
                m = TextUtil.replace(Lang.getMessage(MenuMessage.UNLOCKED_AT, locale),
                        "{skill}", skill.getDisplayName(locale),
                        "{level}", RomanNumber.toRoman(plugin.getAbilityManager().getUnlock(ability)));
                break;
            case "locked":
                m = Lang.getMessage(MenuMessage.LOCKED, locale);
                break;
        }
        assert (null != m);
        return m;
    }

    @Override
    public Set<Ability> getDefinedContexts(@NotNull Player player, @NotNull ActiveMenu activeMenu) {
        @Nullable Object property = activeMenu.getProperty("skill");
        assert (null != property);
        Skill skill = (Skill) property;
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player);
        Set<Ability> lockedAbilities = new HashSet<>();
        if (playerData != null) {
            // Add abilities that player has not unlocked yet
            for (Supplier<@NotNull Ability> abilitySupplier : skill.getAbilities()) {
                @Nullable Ability ability = abilitySupplier.get();
                if (playerData.getAbilityLevel(ability) <= 0) {
                    lockedAbilities.add(ability);
                }
            }
        }
        return lockedAbilities;
    }
}
