package dev.aurelium.auraskills.common.source;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.common.source.serializer.*;
import dev.aurelium.auraskills.common.source.type.*;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum SourceTypes implements SourceType {

    ANVIL(AnvilSource.class, AnvilSourceSerializer.class),
    BLOCK(BlockSource.class, BlockSourceSerializer.class),
    BREWING(BrewingSource.class, BrewingSourceSerializer.class),
    DAMAGE(DamageSource.class, DamageSourceSerializer.class),
    ENCHANTING(EnchantingSource.class, EnchantingSourceSerializer.class),
    ENTITY(EntitySource.class, EntitySourceSerializer.class),
    FISHING(FishingSource.class, FishingSourceSerializer.class),
    GRINDSTONE(GrindstoneSource.class, GrindstoneSourceSerializer.class),
    ITEM_CONSUME(ItemConsumeSource.class, ItemConsumeSourceSerializer.class),
    JUMPING(JumpingSource.class, JumpingSourceSerializer.class),
    MANA_ABILITY_USE(ManaAbilityUseSource.class, ManaAbilityUseSourceSerializer.class),
    POTION_SPLASH(PotionSplashSource.class, PotionSplashSourceSerializer.class),
    STATISTIC(StatisticSource.class, StatisticSourceSerializer.class);

    private final NamespacedId id;
    private final Class<? extends Source> sourceClass;
    private final Class<? extends SourceSerializer<?>> serializerClass;

    SourceTypes(Class<? extends Source> sourceClass, Class<? extends SourceSerializer<?>> serializerClass) {
        this.id = NamespacedId.of(NamespacedId.AURASKILLS, this.name().toLowerCase(Locale.ROOT));
        this.sourceClass = sourceClass;
        this.serializerClass = serializerClass;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public String getName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public Class<? extends Source> getSourceClass() {
        return sourceClass;
    }

    @Override
    public Class<? extends SourceSerializer<?>> getSerializerClass() {
        return serializerClass;
    }

    @Nullable
    public static SourceType getFromSource(Source source) {
        for (SourceType sourceType : values()) {
            if (sourceType.getSourceClass().equals(source.getClass())) {
                return sourceType;
            }
        }
        return null;
    }

}
