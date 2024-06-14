package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.source.*;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.message.MessageKey;
import dev.aurelium.auraskills.common.source.income.IncomeLoader;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class ApiSourceManager implements SourceManager {

    private final AuraSkillsPlugin plugin;
    private final IncomeLoader incomeLoader;

    public ApiSourceManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.incomeLoader = new IncomeLoader(plugin);
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

    @Override
    @Nullable
    public String getUnitName(XpSource source, Locale locale) {
        String unitName = plugin.getItemRegistry().getSourceMenuItems().getSourceUnit(source);
        if (unitName == null) {
            if (source instanceof CustomSource customSource) {
                unitName = customSource.getValues().getUnitName();
            }
        }
        if (unitName == null) return null;

        // Try to replace placeholders
        for (String keyStr : TextUtil.getPlaceholders(unitName)) {
            MessageKey key = MessageKey.of(keyStr);
            String message = plugin.getMsg(key, locale);
            unitName = TextUtil.replace(unitName, "{" + keyStr + "}", message);
        }
        return unitName;
    }

    @Override
    public SourceIncome loadSourceIncome(ConfigNode source) {
        return incomeLoader.loadSourceIncome(((ApiConfigNode) source).getBacking());
    }
}
