package com.archyx.aureliumskills.rewards.builder;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.rewards.ItemReward;
import com.archyx.aureliumskills.rewards.Reward;
import com.archyx.aureliumskills.util.misc.Validate;

public class ItemRewardBuilder extends MessagedRewardBuilder {

    private String itemKey;
    private int amount;

    public ItemRewardBuilder(AureliumSkills plugin) {
        super(plugin);
        this.amount = -1;
    }

    public ItemRewardBuilder itemKey(String itemKey) {
        this.itemKey = itemKey;
        return this;
    }

    public ItemRewardBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public Reward build() {
        Validate.notNull(itemKey, "You must specify an item key");
        return new ItemReward(plugin, menuMessage, chatMessage, itemKey, amount);
    }
}
