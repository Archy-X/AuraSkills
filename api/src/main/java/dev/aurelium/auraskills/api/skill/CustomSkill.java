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
    private final DefinedValues definedValues;
    @Nullable
    private final Ability xpMultiplierAbility;

    private CustomSkill(NamespacedId id, DefinedValues definedValues, @Nullable Ability xpMultiplierAbility) {
        this.id = id;
        this.definedValues = definedValues;
        this.xpMultiplierAbility = xpMultiplierAbility;
    }

    public static CustomSkillBuilder builder(String name, NamespacedRegistry registry) {
        return new CustomSkillBuilder(NamespacedId.from(registry.getNamespace(), name));
    }

    public DefinedValues getDefinedValues() {
        return definedValues;
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
        return definedValues.getDisplayName() != null ? definedValues.getDisplayName() : provider.getDisplayName(this, locale);
    }

    @Override
    public String getDescription(Locale locale) {
        return definedValues.getDescription() != null ? definedValues.getDescription() : provider.getDescription(this, locale);
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
        private final DefinedValues definedValues = new DefinedValues();
        private Ability xpMultiplierAbility;

        private CustomSkillBuilder(NamespacedId id) {
            this.id = id;
            definedValues.setItem(ItemContext.builder()
                    .material("stone")
                    .group("third_row")
                    .order(6)
                    .build());
        }

        /**
         * Sets the {@link ItemContext} of the skill used in menus
         *
         * @param item the {@link ItemContext}
         * @return the builder
         */
        public CustomSkillBuilder item(ItemContext item) {
            definedValues.setItem(item);
            return this;
        }

        public CustomSkillBuilder abilities(Ability... abilities) {
            definedValues.getAbilities().addAll(Arrays.asList(abilities));
            return this;
        }

        public CustomSkillBuilder ability(Ability ability) {
            definedValues.getAbilities().add(ability);
            return this;
        }

        public CustomSkillBuilder manaAbility(ManaAbility manaAbility) {
            definedValues.setManaAbility(manaAbility);
            return this;
        }

        public CustomSkillBuilder displayName(String displayName) {
            definedValues.setDisplayName(displayName);
            return this;
        }

        public CustomSkillBuilder description(String description) {
            definedValues.setDescription(description);
            return this;
        }

        public CustomSkillBuilder xpMultiplierAbility(Ability xpMultiplierAbility) {
            this.xpMultiplierAbility = xpMultiplierAbility;
            return this;
        }

        public CustomSkill build() {
            return new CustomSkill(id, definedValues, xpMultiplierAbility);
        }
    }

    public static class DefinedValues {

        private ItemContext item;
        private final List<Ability> abilities = new ArrayList<>();
        private ManaAbility manaAbility;
        private String displayName;
        private String description;

        public ItemContext getItem() {
            return item;
        }

        public void setItem(ItemContext item) {
            this.item = item;
        }

        public List<Ability> getAbilities() {
            return abilities;
        }

        public ManaAbility getManaAbility() {
            return manaAbility;
        }

        public void setManaAbility(ManaAbility manaAbility) {
            this.manaAbility = manaAbility;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
