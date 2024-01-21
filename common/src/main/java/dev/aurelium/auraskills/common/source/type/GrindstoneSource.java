package dev.aurelium.auraskills.common.source.type;

import dev.aurelium.auraskills.api.source.SourceValues;
import dev.aurelium.auraskills.api.source.type.GrindstoneXpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.source.Source;

public class GrindstoneSource extends Source implements GrindstoneXpSource {

    private final String multiplier;

    public GrindstoneSource(AuraSkillsPlugin plugin, SourceValues values, String multiplier) {
        super(plugin, values);
        this.multiplier = multiplier;
    }

    @Override
    public String getMultiplier() {
        return multiplier;
    }
}
