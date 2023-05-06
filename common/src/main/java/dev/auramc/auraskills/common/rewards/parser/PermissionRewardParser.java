package dev.auramc.auraskills.common.rewards.parser;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.rewards.Reward;
import dev.auramc.auraskills.common.rewards.builder.PermissionRewardBuilder;

import java.util.Map;

public class PermissionRewardParser extends RewardParser {
    
    public PermissionRewardParser(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public Reward parse(Map<?, ?> map) {
        PermissionRewardBuilder builder = new PermissionRewardBuilder(plugin);

        builder.permission(getString(map, "permission"));

        if (map.containsKey("value")) {
            builder.value(getBoolean(map, "value"));
        }

        if (map.containsKey("menu_message")) {
            builder.menuMessage(getString(map, "menu_message"));
        }

        if (map.containsKey("chat_message")) {
            builder.chatMessage(getString(map, "chat_message"));
        }

        if (map.containsKey("message")) {
            String message = getString(map, "message");
            builder.chatMessage(message).menuMessage(message);
        }

        return builder.build();
    }
    
}
