package com.archyx.aureliumskills.lang;

import com.archyx.aureliumskills.skills.abilities.mana_abilities.MAbility;

public enum ManaAbilityMessage implements MessageKey {
    
    REPLENISH_NAME,
    REPLENISH_DESC,
    REPLENISH_RAISE,
    REPLENISH_LOWER,
    REPLENISH_START,
    REPLENISH_END,
    TREECAPITATOR_NAME,
    TREECAPITATOR_DESC,
    TREECAPITATOR_RAISE,
    TREECAPITATOR_LOWER,
    TREECAPITATOR_START,
    TREECAPITATOR_END,
    SPEED_MINE_NAME,
    SPEED_MINE_DESC,
    SPEED_MINE_RAISE,
    SPEED_MINE_LOWER,
    SPEED_MINE_START,
    SPEED_MINE_END,
    ABSORPTION_NAME,
    ABSORPTION_DESC,
    ABSORPTION_RAISE,
    ABSORPTION_LOWER,
    ABSORPTION_START,
    ABSORPTION_END;

    private final MAbility manaAbility = MAbility.valueOf(this.name().substring(0, this.name().lastIndexOf("_") - 1));
    private final String path = "abilities." + manaAbility.name().toLowerCase() + "." + this.name().substring(this.name().lastIndexOf("_") + 1).toLowerCase();
    
    public String getPath() {
        return path;
    }
}
