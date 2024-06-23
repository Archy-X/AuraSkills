package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.config.ConfigNode;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public interface SourceManager {

    /**
     * Gets a list of {@link SkillSource} of a specific type. Each list element contains the XpSource
     * instance and the skill it belongs to.
     *
     * @param typeClass the class of the {@link XpSource} type
     * @return a list of sources
     * @param <T> an instance of XpSource
     */
    @NotNull
    <T extends XpSource> List<SkillSource<T>> getSourcesOfType(Class<T> typeClass);

    /**
     * Gets the first loaded source found of a given source type. Used for sources where only
     * one instance is expected due to having no variants.
     *
     * @param typeClass the class of the {@link XpSource} type
     * @return the {@link SkillSource}
     * @param <T> an instance of XpSource
     */
    @Nullable
    <T extends XpSource> SkillSource<T> getSingleSourceOfType(Class<T> typeClass);

    @Nullable
    String getUnitName(XpSource source, Locale locale);

    @Internal
    SourceIncome loadSourceIncome(ConfigNode source);

}
