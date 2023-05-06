package dev.auramc.auraskills.common.rewards.builder;

import dev.auramc.auraskills.api.util.NamespacedId;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.rewards.Reward;
import dev.auramc.auraskills.common.rewards.type.ItemReward;
import dev.auramc.auraskills.common.util.data.Validate;

public class ItemRewardBuilder extends MessagedRewardBuilder {

    private NamespacedId itemKey;
    private int amount;

    public ItemRewardBuilder(AuraSkillsPlugin plugin) {
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
