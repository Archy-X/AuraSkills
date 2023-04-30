package dev.aurelium.skills.common.rewards.builder;

import dev.aurelium.skills.api.util.NamespacedId;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.rewards.Reward;
import dev.aurelium.skills.common.rewards.type.ItemReward;
import dev.aurelium.skills.common.util.data.Validate;

public class ItemRewardBuilder extends MessagedRewardBuilder {

    private NamespacedId itemKey;
    private int amount;

    public ItemRewardBuilder(AureliumSkillsPlugin plugin) {
        super(plugin);
        this.amount = -1;
    }

    public ItemRewardBuilder itemKey(NamespacedId itemKey) {
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
