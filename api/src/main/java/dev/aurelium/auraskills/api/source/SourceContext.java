package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.jetbrains.annotations.Nullable;

public class SourceContext extends BaseContext {

    protected final SourceType sourceType;
    protected final String sourceName;

    public SourceContext(AuraSkillsApi api, SourceType sourceType, String sourceName) {
        super(api);
        this.sourceType = sourceType;
        this.sourceName = sourceName;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public SourceValues parseValues(ConfigNode source) {
        NamespacedId id = NamespacedId.of(NamespacedId.AURASKILLS, sourceName);
        double xp = source.node("xp").getDouble(0.0);
        @Nullable String displayName = source.node("display_name").getString();
        @Nullable String unitName = source.node("unit_name").getString();
        SourceIncome income = api.getSourceManager().loadSourceIncome(source);
        return new SourceValues(api, sourceType, id, xp, displayName, unitName, income);
    }

}
