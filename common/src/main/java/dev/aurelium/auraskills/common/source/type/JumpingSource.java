package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.type.JumpingXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;

public class JumpingSource extends Source implements JumpingXpSource {

    private final int interval;

    public JumpingSource(AuraSkillsPlugin plugin, NamespacedId id, double xp, String displayName, int interval) {
        super(plugin, id, xp, displayName);
        this.interval = interval;
    }

    @Override
    public int getInterval() {
        return interval;
    }
}
