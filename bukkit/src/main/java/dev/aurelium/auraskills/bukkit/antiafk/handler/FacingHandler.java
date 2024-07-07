package dev.aurelium.auraskills.bukkit.antiafk.handler;

import dev.aurelium.auraskills.bukkit.antiafk.CheckData;
import org.bukkit.entity.Player;

public record FacingHandler(int minCount) {

    public boolean failsCheck(CheckData data, Player player) {
        float prevYaw = data.getCache("previous_yaw", Float.class, -1.0f);
        float prevPitch = data.getCache("previous_pitch", Float.class, -1.0f);
        float currentYaw = player.getLocation().getYaw();
        float currentPitch = player.getLocation().getPitch();
        // Update cache
        data.setCache("previous_yaw", currentYaw);
        data.setCache("previous_pitch", currentPitch);

        if (prevYaw == -1.0f || prevPitch == -1.0f) {
            return false;
        }

        if (prevYaw == currentYaw && prevPitch == currentPitch) {
            data.incrementCount();
        } else {
            data.resetCount();
        }

        return data.getCount() >= minCount;
    }

}
