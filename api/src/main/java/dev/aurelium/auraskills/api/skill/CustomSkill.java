package dev.aurelium.auraskills.api.skill;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.annotation.Inject;
import dev.aurelium.auraskills.api.item.ItemContext;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.XpSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CustomSkill implements Skill {

    @Inject
    private SkillProvider provider;

    private final NamespacedId id;
    private final Defined defined;
    @Nullable
    private final Ability xpMultiplierAbility;

    private CustomSkill(NamespacedId id, Defined defined, @Nullable Ability xpMultiplierAbility) {
        this.id = id;
        this.defined = defined;
        this.xpMultiplierAbility = xpMultiplierAbility;
    }

    /**
     * Gets a new {@link CustomSkillBuilder} used to create a custom skill.
     *
     * @param id the {@link NamespacedId} identifying the skill
     * @return a new builder
     */
    public static CustomSkillBuilder builder(NamespacedId id) {
        return new CustomSkillBuilder(id);
    }

    public Defined getDefined() {
        return defined;
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
        return defined.displayName != null ? defined.displayName : provider.getDisplayName(this, locale, true);
    }

    @Override
    public String getDisplayName(Locale locale, boolean formatted) {
        return defined.displayName != null ? defined.displayName : provider.getDisplayName(this, locale, formatted);
    }

    @Override
    public String getDescription(Locale locale) {
        return defined.description != null ? defined.description : provider.getDescription(this, locale, true);
    }

    @Override
    public String getDescription(Locale locale, boolean formatted) {
        return defined.description != null ? defined.description : provider.getDescription(this, locale, formatted);
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
        private final Defined defined = new Defined();
        private Ability xpMultiplierAbility;

        private CustomSkillBuilder(NamespacedId id) {
            this.id = id;
            defined.setItem(ItemContext.builder()
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
            defined.setItem(item);
            return this;
        }

        public CustomSkillBuilder abilities(Ability... abilities) {
            defined.getAbilities().addAll(Arrays.asList(abilities));
            return this;
        }

        public CustomSkillBuilder ability(Ability ability) {
            defined.getAbilities().add(ability);
            return this;
        }

        public CustomSkillBuilder manaAbility(ManaAbility manaAbility) {
            defined.setManaAbility(manaAbility);
            return this;
        }

        public CustomSkillBuilder displayName(String displayName) {
            defined.setDisplayName(displayName);
            return this;
        }

        public CustomSkillBuilder description(String description) {
            defined.setDescription(description);
            return this;
        }

        public CustomSkillBuilder xpMultiplierAbility(Ability xpMultiplierAbility) {
            this.xpMultiplierAbility = xpMultiplierAbility;
            return this;
        }

        public CustomSkill build() {
            return new CustomSkill(id, defined, xpMultiplierAbility);
        }
    }

    public static class Defined {

        private ItemContext item;
        private final List<Ability> abilities = new ArrayList<>();
        private ManaAbility manaAbility;
        private String displayName;
        private String description;

        private Defined() {

        }

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
