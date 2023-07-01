package dev.aurelium.auraskills.common.message;

import com.archyx.polyglot.Polyglot;
import com.archyx.polyglot.PolyglotProvider;
import com.archyx.polyglot.config.PolyglotConfig;
import com.archyx.polyglot.config.PolyglotConfigBuilder;
import com.archyx.polyglot.lang.MessageManager;
import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.message.type.AbilityMessage;
import dev.aurelium.auraskills.common.message.type.SkillMessage;
import dev.aurelium.auraskills.common.message.type.StatMessage;
import dev.aurelium.auraskills.common.message.type.TraitMessage;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;

/**
 * Interface that provides messages for the plugin.
 */
public class MessageProvider implements PolyglotProvider {

    private final AuraSkillsPlugin plugin;
    private final Polyglot polyglot;
    private final MessageManager manager;

    public MessageProvider(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        PolyglotConfig config = new PolyglotConfigBuilder()
                .messageDirectory("messages")
                .messageFileName("messages_{language}.yml")
                .defaultLanguage("en")
                .build();
        this.polyglot = new Polyglot(this, config);
        this.manager = this.polyglot.getMessageManager();
        init();
    }

    private void init() {
        polyglot.getMessageManager().loadMessages();
    }

    public String get(MessageKey key, Locale locale) {
        return manager.get(locale, convertKey(key));
    }

    public String getSkillDisplayName(Skill skill, Locale locale) {
        if (skill instanceof Skills) {
            return manager.get(locale, convertKey(SkillMessage.valueOf(skill.name() + "_NAME")));
        } else {
            return manager.get(locale, convertKey("skills." + skill.getId().toString() + ".name"));
        }
    }

    public String getSkillDescription(Skill skill, Locale locale) {
        if (skill instanceof Skills) {
            return manager.get(locale, convertKey(SkillMessage.valueOf(skill.name() + "_DESC")));
        } else {
            return manager.get(locale, convertKey("skills." + skill.getId().toString() + ".desc"));
        }
    }

    public String getStatDisplayName(Stat stat, Locale locale) {
        if (stat instanceof Stats) {
            return manager.get(locale, convertKey(StatMessage.valueOf(stat.name() + "_NAME")));
        } else {
            return manager.get(locale, convertKey("stats." + stat.getId().toString() + ".name"));
        }
    }

    public String getStatDescription(Stat stat, Locale locale) {
        if (stat instanceof Stats) {
            return manager.get(locale, convertKey(StatMessage.valueOf(stat.name() + "_DESC")));
        } else {
            return manager.get(locale, convertKey("stats." + stat.getId().toString() + ".desc"));
        }
    }


    public String getStatColor(Stat stat, Locale locale) {
        if (stat instanceof Stats) {
            return manager.get(locale, convertKey(StatMessage.valueOf(stat.name() + "_COLOR")));
        } else {
            return manager.get(locale, convertKey("stats." + stat.getId().toString() + ".color"));
        }
    }

    public String getStatSymbol(Stat stat, Locale locale) {
        if (stat instanceof Stats) {
            return manager.get(locale, convertKey(StatMessage.valueOf(stat.name() + "_SYMBOL")));
        } else {
            return manager.get(locale, convertKey("stats." + stat.getId().toString() + ".symbol"));
        }
    }

    public String getAbilityDisplayName(Ability ability, Locale locale) {
        if (ability instanceof Abilities) {
            return manager.get(locale, convertKey(AbilityMessage.valueOf(ability.name() + "_NAME")));
        } else {
            return manager.get(locale, convertKey("abilities." + ability.getId().toString() + ".name"));
        }
    }

    public String getAbilityDescription(Ability ability, Locale locale) {
        if (ability instanceof Abilities) {
            return manager.get(locale, convertKey(AbilityMessage.valueOf(ability.name() + "_DESC")));
        } else {
            return manager.get(locale, convertKey("abilities." + ability.getId().toString() + ".desc"));
        }
    }

    public String getAbilityInfo(Ability ability, Locale locale) {
        if (ability instanceof Abilities) {
            return manager.get(locale, convertKey(AbilityMessage.valueOf(ability.name() + "_INFO")));
        } else {
            return manager.get(locale, convertKey("abilities." + ability.getId().toString() + ".info"));
        }
    }

    public String getManaAbilityDisplayName(ManaAbility ability, Locale locale) {
        if (ability instanceof ManaAbilities) {
            return manager.get(locale, convertKey(AbilityMessage.valueOf(ability.name() + "_NAME")));
        } else {
            return manager.get(locale, convertKey("mana_abilities." + ability.getId().toString() + ".name"));
        }
    }

    public String getManaAbilityDescription(ManaAbility ability, Locale locale) {
        if (ability instanceof ManaAbilities) {
            return manager.get(locale, convertKey(AbilityMessage.valueOf(ability.name() + "_DESC")));
        } else {
            return manager.get(locale, convertKey("mana_abilities." + ability.getId().toString() + ".desc"));
        }
    }

    public String getTraitDisplayName(Trait trait, Locale locale) {
        if (trait instanceof Traits) {
            return manager.get(locale, convertKey(TraitMessage.valueOf(trait.name() + "_NAME")));
        } else {
            return manager.get(locale, convertKey("traits." + trait.getId().toString() + ".name"));
        }
    }

    public Locale getDefaultLanguage() {
        return manager.getDefaultLanguage();
    }

    private com.archyx.polyglot.lang.MessageKey convertKey(MessageKey key) {
        return com.archyx.polyglot.lang.MessageKey.of(key.getPath());
    }

    private com.archyx.polyglot.lang.MessageKey convertKey(String path) {
        return com.archyx.polyglot.lang.MessageKey.of(path);
    }

    @Override
    public InputStream getResource(String path) {
        return plugin.getResource(path);
    }

    @Override
    public void saveResource(String path, boolean replace) {
        plugin.saveResource(path, replace);
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public void logInfo(String message) {
        plugin.logger().info(message);
    }

    @Override
    public void logWarn(String message) {
        plugin.logger().warn(message);
    }

    @Override
    public void logSevere(String message) {
        plugin.logger().severe(message);
    }
}
