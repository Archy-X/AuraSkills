package dev.aurelium.auraskills.common.message;

import co.aikar.commands.CommandIssuer;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.message.recipient.CommandIssuerRecipient;
import dev.aurelium.auraskills.common.message.recipient.UserRecipient;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class MessageBuilder {

    private final AuraSkillsPlugin plugin;

    private Locale locale;
    private TextComponent component;

    private MessageBuilder(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.component = Component.empty();
    }

    /**
     * Creates a new MessageBuilder
     *
     * @param plugin The plugin instance
     * @return The MessageBuilder
     */
    public static MessageBuilder create(AuraSkillsPlugin plugin) {
        return new MessageBuilder(plugin);
    }

    /**
     * Sends the message to a PlayerData recipient
     *
     * @param user The PlayerData recipient
     */
    public void send(User user) {
        validateComponent(user);
        new UserRecipient(user).sendMessage(component);
    }

    /**
     * Sends the message to a CommandIssuer recipient
     *
     * @param issuer The CommandIssuer recipient
     */
    public void send(CommandIssuer issuer) {
        if (issuer.isPlayer()) {
            User user = plugin.getUserManager().getUser(issuer.getUniqueId());
            validateComponent(user);
        } else {
            validateComponent(null);
        }
        new CommandIssuerRecipient(plugin, issuer).sendMessage(component);
    }

    /**
     * Sets the locale of the message. Required for prefix() and message()
     *
     * @param locale The locale
     * @return The MessageBuilder
     */
    public MessageBuilder locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * Appends a TextComponent to the message
     *
     * @param text The TextComponent
     * @return The MessageBuilder
     */
    public MessageBuilder text(TextComponent text) {
        this.component = component.append(text);
        return this;
    }

    /**
     * Appends a string to the message
     *
     * @param text The string
     * @return The MessageBuilder
     */
    public MessageBuilder text(String text) {
        this.component = component.append(toComponent(text));
        return this;
    }

    /**
     * Appends the plugin command prefix to the message
     *
     * @return The MessageBuilder
     */
    public MessageBuilder prefix() {
        if (locale == null) {
            throw new IllegalStateException("Cannot add prefix because locale is null");
        }
        String prefix = plugin.getPrefix(locale);
        component = component.append(toComponent(prefix));
        return this;
    }

    /**
     * Appends a message from the plugin to the message using a MessageKey.
     * Replacements can be added by adding replacement targets and replacements to the
     * end of the method call. Curly braces are added to the target automatically when replacing.
     *
     * @param key The MessageKey
     * @param rep The replacements, the first string is the target, the second is the replacement
     * @return The MessageBuilder
     */
    public MessageBuilder message(MessageKey key, String... rep) {
        return message(key, false, rep);
    }

    public MessageBuilder rawMessage(MessageKey key, String... rep) {
        return message(key, true, rep);
    }

    public MessageBuilder message(MessageKey key, boolean raw, String... rep) {
        if (locale == null) {
            throw new IllegalStateException("Cannot add message because locale is null");
        }
        String message = raw ? plugin.getMessageProvider().getRaw(key, locale) : plugin.getMsg(key, locale);
        // Ensure replacements is even
        if (rep.length % 2 != 0) {
            throw new IllegalArgumentException("Replacements must be even");
        }
        // Replace replacements
        for (int i = 0; i < rep.length; i+=2) {
            message = TextUtil.replace(message, "{" + rep[i] + "}", rep[i+1]);
        }
        component = component.append(toComponent(message));
        return this;
    }

    private TextComponent toComponent(String text) {
        return LegacyComponentSerializer.legacySection().deserialize(text);
    }

    @Override
    public String toString() {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    private void validateComponent(@Nullable User user) {
        if (component == null) {
            throw new IllegalStateException("Cannot send message because text component is null");
        }
        // Replace PlaceholderAPI
        if (plugin.getHookManager().isRegistered(PlaceholderHook.class) && user != null) {
            String text = toString();
            text = plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(user, text);
            component = toComponent(text);
        }
    }

}
