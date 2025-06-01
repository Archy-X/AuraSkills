package dev.aurelium.auraskills.common.message;

import com.archyx.polyglot.lang.MessageUpdate;

public enum MessageUpdates implements MessageUpdate {

    COMMANDS_MANA_ADD(36, "commands.mana.add", "Key commands.mana.add has been converted to a mapping. " +
            "The message has been reset and moved to commands.mana.add.added"),
    COMMANDS_MANA_REMOVE(36, "commands.mana.remove", "Key commands.mana.remove has been converted to a mapping. " +
            "The message has been reset and moved to commands.mana.remove.removed"),
    COMMANDS_MANA_SET(36, "commands.mana.set", "Key commands.mana.set has been converted to a mapping. " +
            "The message has been reset and moved to commands.mana.set.set"),
    COMMANDS_ITEM_IGNORE_ADD(36, "commands.item.ignore.add", "Key commands.item.ignore.add has been converted to a mapping. " +
            "The message has been reset and moved to commands.item.ignore.add.added"),
    COMMANDS_ITEM_IGNORE_REMOVE(36, "commands.item.ignore.add", "Key commands.item.ignore.remove has been converted to a mapping. " +
            "The message has been reset and moved to commands.item.ignore.remove.removed"),
    COMMANDS_RELOAD(36, "commands.reload", "Key commands.reload has been converted to a mapping. " +
            "The message has been reset and moved to commands.reload.reloaded"),
    COMMANDS_XP_ADD(36, "commands.xp.add", "Key commands.xp.add has been converted to a mapping. " +
            "The message has been reset and moved to commands.xp.add.added"),
    COMMANDS_XP_REMOVE(36, "commands.xp.remove", "Key commands.xp.remove has been converted to a mapping. " +
            "The message has been reset and moved to commands.xp.remove.removed"),
    COMMANDS_XP_SET(36, "commands.xp.set", "Key commands.xp.set has been converted to a mapping. " +
            "The message has been reset and moved to commands.xp.set.set"),
    COMMANDS_PROFILE_SKILLS(36, "commands.profile.skills", "Key commands.profile.skills has been converted to a mapping. " +
            "The message has been reset and moved to commands.profile.skills.header"),
    COMMANDS_PROFILE_STATS(36, "commands.profile.stats", "Key commands.profile.stats has been converted to a mapping. " +
            "The message has been reset and moved to commands.profile.stats.header"),
    COMMANDS_VERSION(37, "commands.version", "Key commands.version has been converted to a mapping. " +
            "The message has been reset and moved to commands.version.version"),
    COMMANDS_ARMOR_MODIFIER_ADD_ADDED(38, "commands.armor.modifier.add.added", "Key commands.armor.modifier.add.added was reset because the {operation} placeholder was added."),
    COMMANDS_ITEM_MODIFIER_ADD_ADDED(38, "commands.item.modifier.add.added", "Key commands.item.modifier.add.added was reset because the {operation} placeholder was added.");

    private final int version;
    private final String path;
    private final String message;

    MessageUpdates(int version, String path, String message) {
        this.version = version;
        this.path = path;
        this.message = message;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
