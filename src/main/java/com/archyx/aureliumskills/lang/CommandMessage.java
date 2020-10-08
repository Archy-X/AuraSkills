package com.archyx.aureliumskills.lang;

import com.archyx.aureliumskills.commands.Command;

public enum CommandMessage implements MessageKey {

    PREFIX,
    ARMOR_MODIFIER_ADD_ADDED,
    ARMOR_MODIFIER_ADD_ALREADY_EXISTS(Command.MODIFIER_ADD, "already_exists"),
    ARMOR_MODIFIER_ADD_MUST_HOLD_ITEM(Command.MODIFIER_ADD, "must_hold_item"),
    ARMOR_MODIFIER_REMOVE_DOES_NOT_EXIST(Command.ARMOR_MODIFIER_REMOVE, "does_not_exist"),
    ARMOR_MODIFIER_REMOVE_MUST_HOLD_ITEM(Command.ARMOR_MODIFIER_REMOVE, "must_hold_item"),
    ARMOR_MODIFIER_REMOVE_REMOVED,
    ARMOR_MODIFIER_LIST_HEADER,
    ARMOR_MODIFIER_LIST_ENTRY,
    ARMOR_MODIFIER_LIST_MUST_HOLD_ITEM(Command.ARMOR_MODIFIER_LIST, "must_hold_item"),
    ARMOR_MODIFIER_REMOVEALL_MUST_HOLD_ITEM(Command.ARMOR_MODIFIER_REMOVEALL, "must_hold_item"),
    ARMOR_MODIFIER_REMOVEALL_REMOVED,
    ITEM_MODIFIER_ADD_ADDED,
    ITEM_MODIFIER_ADD_ALREADY_EXISTS(Command.MODIFIER_ADD, "already_exists"),
    ITEM_MODIFIER_ADD_MUST_HOLD_ITEM(Command.MODIFIER_ADD, "must_hold_item"),
    ITEM_MODIFIER_REMOVE_DOES_NOT_EXIST(Command.ITEM_MODIFIER_REMOVE, "does_not_exist"),
    ITEM_MODIFIER_REMOVE_MUST_HOLD_ITEM(Command.ITEM_MODIFIER_REMOVE, "must_hold_item"),
    ITEM_MODIFIER_REMOVE_REMOVED,
    ITEM_MODIFIER_LIST_HEADER,
    ITEM_MODIFIER_LIST_ENTRY,
    ITEM_MODIFIER_LIST_MUST_HOLD_ITEM(Command.ITEM_MODIFIER_LIST, "must_hold_item"),
    ITEM_MODIFIER_REMOVEALL_MUST_HOLD_ITEM(Command.ITEM_MODIFIER_REMOVEALL, "must_hold_item"),
    ITEM_MODIFIER_REMOVEALL_REMOVED,
    LANG,
    MODIFIER_ADD_ADDED,
    MODIFIER_ADD_ALREADY_EXISTS(Command.MODIFIER_ADD, "already_exists"),
    MODIFIER_REMOVE_NOT_FOUND(Command.MODIFIER_REMOVE, "not_found"),
    MODIFIER_REMOVE_REMOVE,
    MODIFIER_LIST_ALL_STATS_HEADER(Command.MODIFIER_LIST, "all_stats_header"),
    MODIFIER_LIST_ALL_STATS_ENTRY(Command.MODIFIER_LIST, "all_stats_entry"),
    MODIFIER_LIST_ONE_STAT_HEADER(Command.MODIFIER_LIST, "one_stat_header"),
    MODIFIER_LIST_ONE_STAT_ENTRY(Command.MODIFIER_LIST, "one_stat_entry"),
    MODIFIER_LIST_PLAYERS_ONLY(Command.MODIFIER_LIST, "players_only"),
    MODIFIER_REMOVEALL_REMOVED_ALL_STATS(Command.MODIFIER_REMOVEALL, "removed_all_stats"),
    MODIFIER_REMOVEALL_REMOVED_ONE_STAT(Command.MODIFIER_REMOVEALL, "removed_one_stat"),
    MODIFIER_REMOVEALL_PLAYERS_ONLY(Command.MODIFIER_REMOVEALL, "players_only"),
    MODIFIER_NO_PROFILE("modifier.no_profile"),
    MULTIPLIER,
    RANK_HEADER,
    RANK_POWER,
    RANK_ENTRY,
    RELOAD,
    SAVE_ALREADY_SAVING(Command.SAVE, "already_saving"),
    SAVE_MYSQL_NOT_ENABLED(Command.SAVE, "mysql_not_enabled"),
    SAVE_SAVED,
    SKILL_RESET_RESET_ALL(Command.SKILL_RESET, "reset_all"),
    SKILL_RESET_RESET_SKILL(Command.SKILL_RESET, "reset_skill"),
    SKILL_SETALL_AT_LEAST_ONE(Command.SKILL_SETALL, "at_least_one"),
    SKILL_SETALL_SET,
    SKILL_SETLEVEL_AT_LEAST_ONE(Command.SKILL_SETLEVEL, "at_least_one"),
    SKILL_SETLEVEL_SET,
    TOGGLE_ENABLED,
    TOGGLE_DISABLED,
    TOGGLE_NOT_ENABLED(Command.TOGGLE, "not_enabled"),
    TOP_POWER_ENTRY(Command.TOP, "power_entry"),
    TOP_POWER_HEADER(Command.TOP, "power_header"),
    TOP_POWER_HEADER_PAGE(Command.TOP, "power_header_page"),
    TOP_SKILL_HEADER(Command.TOP, "skill_header"),
    TOP_SKILL_HEADER_PAGE(Command.TOP, "skill_header_page"),
    TOP_SKILL_ENTRY(Command.TOP, "skill_entry"),
    TOP_USAGE,
    UPDATELEADERBOARDS_ALREADY_UPDATING(Command.UPDATELEADERBOARDS, "already_updating"),
    UPDATELEADERBOARDS_UPDATED,
    XP_ADD,
    XP_SET,
    XP_REMOVE,
    UNKNOWN_SKILL("unknown_skill");

    private final String path;

    CommandMessage() {
        this.path = "commands." + this.name().toLowerCase().replace("_", ".");
    }
    
    CommandMessage(Command command, String path) {
        this.path = "commands." + command.name().toLowerCase().replace("_", ".") + "." + path;
    }

    CommandMessage(String path) {
        this.path = "commands." + path;
    }

    public String getPath() {
        return path;
    }

}
