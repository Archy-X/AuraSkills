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

import java.util.*;

public class CustomSkill implements Skill {

    @Inject
    private SkillProvider provider;

    private final NamespacedId id;
    private final ItemContext item;
    private final List<Ability> definedAbilities;
    @Nullable
    private final ManaAbility definedManaAbility;
    @Nullable
    private final Ability xpMultiplierAbility;

    private CustomSkill(NamespacedId id, ItemContext item, List<Ability> definedAbilities, @Nullable ManaAbility definedManaAbility, @Nullable Ability xpMultiplierAbility) {
        this.id = id;
        this.item = item;
        this.definedAbilities = definedAbilities;
        this.definedManaAbility = definedManaAbility;
        this.xpMultiplierAbility = xpMultiplierAbility;
    }

    public static CustomSkillBuilder builder(String name, NamespacedRegistry registry) {
        return new CustomSkillBuilder(NamespacedId.from(registry.getNamespace(), name));
    }

    public ItemContext getItem() {
        return item;
    }

    public List<Ability> getDefinedAbilities() {
        return definedAbilities;
    }

    @Nullable
    public ManaAbility getDefinedManaAbility() {
        return definedManaAbility;
    }

    @Override
    public NamespacedId getId() {
        return id;
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
        private final List<Ability> abilities;
        private ManaAbility manaAbility;
        private Ability xpMultiplierAbility;

        private CustomSkillBuilder(NamespacedId id) {
            this.id = id;
            this.item = ItemContext.builder()
                    .material("stone")
                    .group("third_row")
                    .order(6)
                    .build();
            this.abilities = new ArrayList<>();
        }

        /**
         * Sets the {@link ItemContext} of the skill used in menus
         *
         * @param item the {@link ItemContext}
         * @return the builder
         */
        public CustomSkillBuilder item(ItemContext item) {
            this.item = item;
            return this;
        }

        public CustomSkillBuilder abilities(Ability... abilities) {
            this.abilities.addAll(Arrays.asList(abilities));
            return this;
        }

        public CustomSkillBuilder ability(Ability ability) {
            this.abilities.add(ability);
            return this;
        }

        public CustomSkillBuilder manaAbility(ManaAbility manaAbility) {
            this.manaAbility = manaAbility;
            return this;
        }

        public CustomSkillBuilder xpMultiplierAbility(Ability xpMultiplierAbility) {
            this.xpMultiplierAbility = xpMultiplierAbility;
            return this;
        }

        public CustomSkill build() {
            return new CustomSkill(id, item, abilities, manaAbility, xpMultiplierAbility);
        }
    }

}
