package dev.aurelium.auraskills.common.reward.type;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.data.PlayerData;

public class ItemReward extends MessagedReward {

    protected final NamespacedId itemKey;
    protected final int amount; // Amount of -1 means no amount was specified and should use amount of registered item

    public ItemReward(AuraSkillsPlugin plugin, String menuMessage, String chatMessage, NamespacedId itemKey, int amount) {
        super(plugin, menuMessage, chatMessage);
        this.itemKey = itemKey;
        this.amount = amount;
    }

    @Override
    public void giveReward(PlayerData playerData, Skill skill, int level) {
        plugin.getItemRegistry().giveItem(itemKey, amount);
    }
}
