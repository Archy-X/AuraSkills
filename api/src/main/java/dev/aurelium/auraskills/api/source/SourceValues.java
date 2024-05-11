package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import org.jetbrains.annotations.Nullable;

public class SourceValues {

    private final AuraSkillsApi api;
    private final SourceType type;
    private final NamespacedId id;
    private final double xp;
    @Nullable
    private final String displayName;
    @Nullable
    private final String unitName;
    private final SourceIncome income;

    public SourceValues(AuraSkillsApi api, SourceType type, NamespacedId id, double xp,
                        @Nullable String displayName, @Nullable String unitName, SourceIncome income) {
        this.api = api;
        this.type = type;
        this.id = id;
        this.xp = xp;
        this.displayName = displayName;
        this.unitName = unitName;
        this.income = income;
    }

    public AuraSkillsApi getApi() {
        return api;
    }

    public SourceType getType() {
        return type;
    }

    public NamespacedId getId() {
        return id;
    }

    public double getXp() {
        return xp;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    public String getUnitName() {
        return unitName;
    }

    public SourceIncome getIncome() {
        return income;
    }
}
