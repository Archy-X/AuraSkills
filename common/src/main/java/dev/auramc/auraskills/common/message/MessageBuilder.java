package dev.auramc.auraskills.common.message;

import co.aikar.commands.CommandIssuer;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.message.recipient.CommandIssuerRecipient;
import dev.auramc.auraskills.common.message.recipient.PlayerDataRecipient;
import dev.auramc.auraskills.common.message.recipient.Recipient;
import dev.auramc.auraskills.common.util.text.TextUtil;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Locale;

public class MessageBuilder {

    private final AuraSkillsPlugin plugin;

    private Locale locale;
    private Recipient recipient;
    private TextComponent component;

    private MessageBuilder(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
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
     * Sends the message to the recipient
     */
    public void send() {
        if (recipient == null || component == null) {
            throw new IllegalStateException("Recipient or text is null");
        }
        recipient.sendMessage(component);
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
     * Sets the recipient of the message
     *
     * @param recipient The recipient
     * @return The MessageBuilder
     */
    public MessageBuilder to(Recipient recipient) {
        this.recipient = recipient;
        return this;
    }

    /**
     * Sets the recipient of the message using a CommandIssuer
     *
     * @param issuer The CommandIssuer
     * @return The MessageBuilder
     */
    public MessageBuilder to(CommandIssuer issuer) {
        this.recipient = new CommandIssuerRecipient(plugin, issuer);
        return this;
    }

    /**
     * Sets the recipient of the message using a PlayerData
     *
     * @param playerData The PlayerData
     * @return The MessageBuilder
     */
    public MessageBuilder to(PlayerData playerData) {
        this.recipient = new PlayerDataRecipient(playerData);
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
        if (locale == null) {
            throw new IllegalStateException("Cannot add message because locale is null");
        }
        String message = plugin.getMsg(key, locale);
        // Ensure replacements is even
        if (rep.length % 2 != 0) {
            throw new IllegalArgumentException("Replacements must be even");
        }
        // Replace replacements
        for (int i = 0; i < rep.length; i+=2) {
            message = TextUtil.replace(message, "{" + i + "}", rep[i+1]);
        }
        component = component.append(toComponent(message));
        return this;
    }

    private TextComponent toComponent(String text) {
        return LegacyComponentSerializer.legacySection().deserialize(text);
    }

}
