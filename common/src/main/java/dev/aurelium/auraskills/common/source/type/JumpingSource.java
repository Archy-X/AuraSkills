package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.api.source.type.JumpingXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;

public class JumpingSource extends Source implements JumpingXpSource {

    private final int interval;

    public JumpingSource(AuraSkillsPlugin plugin, SourceValues values, int interval) {
        super(plugin, values);
        this.interval = interval;
    }

    @Override
    public int getInterval() {
        return interval;
    }
}
