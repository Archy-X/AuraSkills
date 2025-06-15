package dev.aurelium.auraskills.common.antiafk;

import dev.aurelium.auraskills.common.ref.PlayerRef;

import java.util.function.ToDoubleFunction;

public class FacingHandler {

    private final int minCount;
    private final ToDoubleFunction<PlayerRef> yawProvider;
    private final ToDoubleFunction<PlayerRef> pitchProvider;

    public FacingHandler(int minCount, ToDoubleFunction<PlayerRef> yawProvider, ToDoubleFunction<PlayerRef> pitchProvider) {
        this.minCount = minCount;
        this.yawProvider = yawProvider;
        this.pitchProvider = pitchProvider;
    }

    public boolean failsCheck(CheckData data, PlayerRef ref) {
        float prevYaw = data.getCache("previous_yaw", Float.class, -1.0f);
        float prevPitch = data.getCache("previous_pitch", Float.class, -1.0f);
        float currentYaw = (float) yawProvider.applyAsDouble(ref);
        float currentPitch = (float) pitchProvider.applyAsDouble(ref);
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
