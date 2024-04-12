package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.SourceManager;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ApiSourceManager implements SourceManager {

    private final AuraSkillsPlugin plugin;

    public ApiSourceManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public <T extends XpSource> List<SkillSource<T>> getSourcesOfType(Class<T> typeClass) {
        return plugin.getSkillManager().getSourcesOfType(typeClass);
    }

    @Override
    @Nullable
    public <T extends XpSource> SkillSource<T> getSingleSourceOfType(Class<T> typeClass) {
        return plugin.getSkillManager().getSingleSourceOfType(typeClass);
    }
}
