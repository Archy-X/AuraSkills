package dev.aurelium.auraskills.common.loot;

import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootValues;
import dev.aurelium.auraskills.common.action.UserAction;

public class ActionLoot extends Loot {

    private final UserAction action;

    public ActionLoot(LootValues values, UserAction action) {
        super(values);
        this.action = action;
    }

    public UserAction getAction() {
        return action;
    }
}
