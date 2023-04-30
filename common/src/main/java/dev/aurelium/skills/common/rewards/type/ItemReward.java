package dev.aurelium.skills.common.rewards.type;

import dev.aurelium.skills.api.skill.Skill;
import dev.aurelium.skills.api.util.NamespacedId;
import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.data.PlayerData;

public class ItemReward extends MessagedReward {

    protected final NamespacedId itemKey;
    protected final int amount; // Amount of -1 means no amount was specified and should use amount of registered item

    public ItemReward(AureliumSkillsPlugin plugin, String menuMessage, String chatMessage, NamespacedId itemKey, int amount) {
        super(plugin, menuMessage, chatMessage);
        this.itemKey = itemKey;
        this.amount = amount;
    }

    @Override
    public void giveReward(PlayerData playerData, Skill skill, int level) {
        plugin.getItemRegistry().giveItem(itemKey, amount);
    }
}
