package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.api.ApiAuraSkills;
import dev.aurelium.auraskills.common.source.parser.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public enum SourceTypes implements SourceType {

    ANVIL(AnvilSourceParser.class),
    BLOCK(BlockSourceParser.class),
    BREWING(BrewingSourceParser.class),
    DAMAGE(DamageSourceParser.class),
    ENCHANTING(EnchantingSourceParser.class),
    ENTITY(EntitySourceParser.class),
    FISHING(FishingSourceParser.class),
    GRINDSTONE(GrindstoneSourceParser.class),
    ITEM_CONSUME(ItemConsumeSourceParser.class),
    JUMPING(JumpingSourceParser.class),
    MANA_ABILITY_USE(ManaAbilityUseSourceParser.class),
    POTION_SPLASH(PotionSplashSourceParser.class),
    STATISTIC(StatisticSourceParser.class);

    private final NamespacedId id;
    private final Class<? extends SourceParser<?>> parserClass;

    SourceTypes(Class<? extends SourceParser<?>> parserClass) {
        this.id = NamespacedId.of(NamespacedId.AURASKILLS, this.name().toLowerCase(Locale.ROOT));
        this.parserClass = parserClass;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public SourceParser<?> getParser() {
        AuraSkillsPlugin plugin = ((ApiAuraSkills) AuraSkillsApi.get()).getPlugin();
        try {
            return (SourceParser<?>) parserClass.getConstructors()[0].newInstance(plugin);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Could not instantiate parser class " + parserClass.getName());
        }
    }

    @Override
    public boolean isEnabled() {
        AuraSkillsPlugin plugin = ((ApiAuraSkills) AuraSkillsApi.get()).getPlugin();
        return plugin.getSkillManager().isSourceEnabled(this);
    }

}
