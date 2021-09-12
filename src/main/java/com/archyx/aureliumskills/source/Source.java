package com.archyx.aureliumskills.source;

import com.archyx.aureliumskills.lang.CustomMessageKey;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public interface Source {

    Skill getSkill();

    default String getPath() {
        return getSkill().toString().toLowerCase(Locale.ROOT) + "." + toString().toLowerCase(Locale.ROOT);
    }

    default String getDisplayName(Locale locale) {
        String messagePath = "sources." + getSkill().toString().toLowerCase(Locale.ROOT) + "." + toString().toLowerCase(Locale.ROOT);
        return Lang.getMessage(new CustomMessageKey(messagePath), locale);
    }

    /**
     * Only gets the message identifier, not the localized actual name.
     * @return The message identifier, null if the source has no unit
     */
    default String getUnitName() {
        return null;
    }

    /**
     * Gets the item to be displayed in the sources menu
     * @return The item or null if the source does not exist in this version
     */
    ItemStack getMenuItem();

}
