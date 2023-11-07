package dev.aurelium.auraskills.api.skill;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.item.ItemContext;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.registry.NamespacedRegistry;
import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomSkill implements Skill {

    @Inject
    private SkillProvider provider;

    private final NamespacedId id;
    private final ItemContext item;
    private final File contentDirectory;
    private final Ability xpMultiplierAbility;

    private CustomSkill(NamespacedId id, ItemContext item, File contentDirectory, Ability xpMultiplierAbility) {
        this.id = id;
        this.contentDirectory = contentDirectory;
        this.xpMultiplierAbility = xpMultiplierAbility;
        this.item = item;
    }

    public static CustomSkillBuilder builder(String name, NamespacedRegistry registry) {
        return new CustomSkillBuilder(NamespacedId.from(registry.getNamespace(), name), registry.getContentDirectory());
    }

    public ItemContext getItem() {
        return item;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Nullable
    public File getContentDirectory() {
        return contentDirectory;
    }

    @Override
    public boolean isEnabled() {
        return provider.isEnabled(this);
    }

    @Override
    public @NotNull List<Ability> getAbilities() {
        return provider.getAbilities(this);
    }

    @Override
    public @Nullable Ability getXpMultiplierAbility() {
        return xpMultiplierAbility;
    }

    @Override
    public @Nullable ManaAbility getManaAbility() {
        return provider.getManaAbility(this);
    }

    @Override
    public @NotNull List<XpSource> getSources() {
        return provider.getSources(this);
    }

    @Override
    public int getMaxLevel() {
        return provider.getMaxLevel(this);
    }

    @Override
    public String getDisplayName(Locale locale) {
        return provider.getDisplayName(this, locale);
    }

    @Override
    public String getDescription(Locale locale) {
        return provider.getDescription(this, locale);
    }

    @Override
    public String name() {
        return id.getKey().toUpperCase(Locale.ROOT);
    }

    @Override
    public boolean optionBoolean(String key) {
        return provider.optionBoolean(this, key);
    }

    @Override
    public boolean optionBoolean(String key, boolean def) {
        return provider.optionBoolean(this, key, def);
    }

    @Override
    public int optionInt(String key) {
        return provider.optionInt(this, key);
    }

    @Override
    public int optionInt(String key, int def) {
        return provider.optionInt(this, key, def);
    }

    @Override
    public double optionDouble(String key) {
        return provider.optionDouble(this, key);
    }

    @Override
    public double optionDouble(String key, double def) {
        return provider.optionDouble(this, key, def);
    }

    @Override
    public String optionString(String key) {
        return provider.optionString(this, key);
    }

    @Override
    public String optionString(String key, String def) {
        return provider.optionString(this, key, def);
    }

    @Override
    public List<String> optionStringList(String key) {
        return provider.optionStringList(this, key);
    }

    @Override
    public Map<String, Object> optionMap(String key) {
        return provider.optionMap(this, key);
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public static class CustomSkillBuilder {

        private final NamespacedId id;
        private ItemContext item;
        private final File contentDirectory;
        private Ability xpMultiplierAbility;

        public CustomSkillBuilder(NamespacedId id, File contentDirectory) {
            this.id = id;
            this.contentDirectory = contentDirectory;
            this.item = ItemContext.builder()
                    .material("stone")
                    .group("third_row")
                    .order(6)
                    .build();
        }

        public CustomSkillBuilder item(ItemContext item) {
            this.item = item;
            return this;
        }

        public CustomSkillBuilder xpMultiplierAbility(Ability xpMultiplierAbility) {
            this.xpMultiplierAbility = xpMultiplierAbility;
            return this;
        }

        public CustomSkill build() {
            return new CustomSkill(id, item, contentDirectory, xpMultiplierAbility);
        }
    }

}
