package dev.aurelium.auraskills.common.message.type;


import dev.aurelium.auraskills.common.commands.Command;
import dev.aurelium.auraskills.common.message.MessageKey;

import java.util.Locale;

public enum CommandMessage implements MessageKey {

    PREFIX,
    ANTIAFK_FAILED,
    ANTIAFK_LOGS_HEADER,
    ANTIAFK_LOGS_ENTRY,
    ARMOR_MODIFIER_ADD_ADDED,
    ARMOR_MODIFIER_ADD_ALREADY_EXISTS(Command.ARMOR_MODIFIER_ADD, "already_exists"),
    ARMOR_MODIFIER_ADD_LORE,
    ARMOR_MODIFIER_ADD_LORE_SUBTRACT(Command.ARMOR_MODIFIER_ADD, "lore_subtract"),
    ARMOR_MODIFIER_ADD_LORE_ADD_PERCENT(Command.ARMOR_MODIFIER_ADD, "lore_add_percent"),
    ARMOR_MODIFIER_ADD_LORE_SUBTRACT_PERCENT(Command.ARMOR_MODIFIER_ADD, "lore_subtract_percent"),
    ARMOR_MODIFIER_ADD_LORE_MULTIPLY(Command.ARMOR_MODIFIER_ADD, "lore_multiply"),
    ARMOR_MODIFIER_REMOVE_DOES_NOT_EXIST(Command.ARMOR_MODIFIER_REMOVE, "does_not_exist"),
    ARMOR_MODIFIER_REMOVE_REMOVED,
    ARMOR_MODIFIER_LIST_HEADER,
    ARMOR_MODIFIER_LIST_ENTRY,
    ARMOR_MODIFIER_REMOVEALL_REMOVED,
    ARMOR_TRAIT_ADD_ALREADY_EXISTS(Command.ARMOR_TRAIT_ADD, "already_exists"),
    ARMOR_MULTIPLIER_ADD_ADDED,
    ARMOR_MULTIPLIER_ADD_ALREADY_EXISTS(Command.ARMOR_MULTIPLIER_ADD, "already_exists"),
    ARMOR_MULTIPLIER_ADD_SKILL_LORE(Command.ARMOR_MULTIPLIER_ADD, "skill_lore"),
    ARMOR_MULTIPLIER_ADD_SKILL_LORE_SUBTRACT(Command.ARMOR_MULTIPLIER_ADD, "skill_lore_subtract"),
    ARMOR_MULTIPLIER_ADD_GLOBAL_LORE(Command.ARMOR_MULTIPLIER_ADD, "global_lore"),
    ARMOR_MULTIPLIER_ADD_GLOBAL_LORE_SUBTRACT(Command.ARMOR_MULTIPLIER_ADD, "global_lore_subtract"),
    ARMOR_MULTIPLIER_REMOVE_DOES_NOT_EXIST(Command.ARMOR_MULTIPLIER_REMOVE, "does_not_exist"),
    ARMOR_MULTIPLIER_REMOVE_REMOVED,
    ARMOR_MULTIPLIER_LIST_HEADER,
    ARMOR_MULTIPLIER_LIST_ENTRY,
    ARMOR_MULTIPLIER_REMOVEALL_REMOVED,
    ARMOR_REQUIREMENT_ADD_ADDED,
    ARMOR_REQUIREMENT_ADD_ALREADY_EXISTS(Command.ARMOR_REQUIREMENT_ADD, "already_exists"),
    ARMOR_REQUIREMENT_ADD_LORE,
    ARMOR_REQUIREMENT_REMOVE_DOES_NOT_EXIST(Command.ARMOR_REQUIREMENT_REMOVE, "does_not_exist"),
    ARMOR_REQUIREMENT_REMOVE_REMOVED,
    ARMOR_REQUIREMENT_LIST_HEADER,
    ARMOR_REQUIREMENT_LIST_ENTRY,
    ARMOR_REQUIREMENT_REMOVEALL_REMOVED,
    ARMOR_REQUIREMENT_EQUIP,
    ARMOR_REQUIREMENT_ENTRY,
    BACKUP_LOAD_CONFIRM,
    BACKUP_LOAD_LOADING,
    BACKUP_LOAD_LOADED,
    BACKUP_LOAD_ERROR,
    BACKUP_LOAD_MUST_BE_YAML(Command.BACKUP_LOAD, "must_be_yaml"),
    BACKUP_LOAD_FILE_NOT_FOUND(Command.BACKUP_LOAD, "file_not_found"),
    BACKUP_SAVE_SAVING,
    BACKUP_SAVE_SAVED,
    BACKUP_SAVE_ERROR,
    CLAIMITEMS_NO_ITEMS(Command.CLAIMITEMS, "no_items"),
    ITEM_GIVE_SENDER,
    ITEM_GIVE_RECEIVER,
    ITEM_MODIFIER_ADD_ADDED,
    ITEM_MODIFIER_ADD_ALREADY_EXISTS(Command.ITEM_MODIFIER_ADD, "already_exists"),
    ITEM_MODIFIER_ADD_LORE,
    ITEM_MODIFIER_ADD_LORE_SUBTRACT(Command.ITEM_MODIFIER_ADD, "lore_subtract"),
    ITEM_MODIFIER_ADD_LORE_ADD_PERCENT(Command.ITEM_MODIFIER_ADD, "lore_add_percent"),
    ITEM_MODIFIER_ADD_LORE_SUBTRACT_PERCENT(Command.ITEM_MODIFIER_ADD, "lore_subtract_percent"),
    ITEM_MODIFIER_ADD_LORE_MULTIPLY(Command.ITEM_MODIFIER_ADD, "lore_multiply"),
    ITEM_MODIFIER_REMOVE_DOES_NOT_EXIST(Command.ITEM_MODIFIER_REMOVE, "does_not_exist"),
    ITEM_MODIFIER_REMOVE_REMOVED,
    ITEM_MODIFIER_LIST_HEADER,
    ITEM_MODIFIER_LIST_ENTRY,
    ITEM_MODIFIER_REMOVEALL_REMOVED,
    ITEM_TRAIT_ADD_ALREADY_EXISTS(Command.ITEM_TRAIT_ADD, "already_exists"),
    ITEM_MULTIPLIER_ADD_ADDED,
    ITEM_MULTIPLIER_ADD_ALREADY_EXISTS(Command.ITEM_MULTIPLIER_ADD, "already_exists"),
    ITEM_MULTIPLIER_ADD_SKILL_LORE(Command.ITEM_MULTIPLIER_ADD, "skill_lore"),
    ITEM_MULTIPLIER_ADD_SKILL_LORE_SUBTRACT(Command.ITEM_MULTIPLIER_ADD, "skill_lore_subtract"),
    ITEM_MULTIPLIER_ADD_GLOBAL_LORE(Command.ITEM_MULTIPLIER_ADD, "global_lore"),
    ITEM_MULTIPLIER_ADD_GLOBAL_LORE_SUBTRACT(Command.ITEM_MULTIPLIER_ADD, "global_lore_subtract"),
    ITEM_MULTIPLIER_REMOVE_DOES_NOT_EXIST(Command.ITEM_MULTIPLIER_REMOVE, "does_not_exist"),
    ITEM_MULTIPLIER_REMOVE_REMOVED,
    ITEM_MULTIPLIER_LIST_HEADER,
    ITEM_MULTIPLIER_LIST_ENTRY,
    ITEM_MULTIPLIER_REMOVEALL_REMOVED,
    ITEM_REGISTER_REGISTERED,
    ITEM_REGISTER_ALREADY_REGISTERED(Command.ITEM_REGISTER, "already_registered"),
    ITEM_REGISTER_NO_SPACES(Command.ITEM_REGISTER, "no_spaces"),
    ITEM_REQUIREMENT_ADD_ADDED,
    ITEM_REQUIREMENT_ADD_ALREADY_EXISTS(Command.ITEM_REQUIREMENT_ADD, "already_exists"),
    ITEM_REQUIREMENT_ADD_LORE,
    ITEM_REQUIREMENT_REMOVE_DOES_NOT_EXIST(Command.ITEM_REQUIREMENT_REMOVE, "does_not_exist"),
    ITEM_REQUIREMENT_REMOVE_REMOVED,
    ITEM_REQUIREMENT_LIST_HEADER,
    ITEM_REQUIREMENT_LIST_ENTRY,
    ITEM_REQUIREMENT_REMOVEALL_REMOVED,
    ITEM_REQUIREMENT_USE,
    ITEM_REQUIREMENT_ENTRY,
    ITEM_UNREGISTER_UNREGISTERED,
    ITEM_UNREGISTER_NOT_REGISTERED(Command.ITEM_UNREGISTER, "not_registered"),
    ITEM_IGNORE_ADD_ADDED,
    ITEM_IGNORE_REMOVE_REMOVED,
    JOBS_ADD_ADDED,
    JOBS_ADD_LIMITED,
    JOBS_ADD_EXISTING,
    JOBS_REMOVE_REMOVED,
    JOBS_REMOVE_UNCHANGED,
    JOBS_REMOVEALL_REMOVED,
    LANG_SET,
    LANG_NOT_FOUND(Command.LANG, "not_found"),
    MANA_DISPLAY,
    MANA_DISPLAY_OTHER(Command.MANA, "display_other"),
    MANA_ADD_ADDED,
    MANA_REMOVE_REMOVED,
    MANA_SET_SET,
    MANA_AT_LEAST_ZERO(Command.MANA, "at_least_zero"),
    MANA_CONSOLE_SPECIFY_PLAYER(Command.MANA, "console_specify_player"),
    MODIFIER_ADD_ADDED,
    MODIFIER_ADD_ALREADY_EXISTS(Command.MODIFIER_ADD, "already_exists"),
    MODIFIER_ADDTEMP_ADDED,
    MODIFIER_REMOVE_NOT_FOUND(Command.MODIFIER_REMOVE, "not_found"),
    MODIFIER_REMOVE_REMOVED,
    MODIFIER_LIST_ALL_STATS_HEADER(Command.MODIFIER_LIST, "all_stats_header"),
    MODIFIER_LIST_ALL_STATS_ENTRY(Command.MODIFIER_LIST, "all_stats_entry"),
    MODIFIER_LIST_ONE_STAT_HEADER(Command.MODIFIER_LIST, "one_stat_header"),
    MODIFIER_LIST_ONE_STAT_ENTRY(Command.MODIFIER_LIST, "one_stat_entry"),
    MODIFIER_LIST_PLAYERS_ONLY(Command.MODIFIER_LIST, "players_only"),
    MODIFIER_REMOVEALL_REMOVED_ALL_STATS(Command.MODIFIER_REMOVEALL, "removed_all_stats"),
    MODIFIER_REMOVEALL_REMOVED_ONE_STAT(Command.MODIFIER_REMOVEALL, "removed_one_stat"),
    MODIFIER_REMOVEALL_PLAYERS_ONLY(Command.MODIFIER_REMOVEALL, "players_only"),
    TRAIT_ADD_ALREADY_EXISTS(Command.TRAIT_ADD, "already_exists"),
    TRAIT_ADDTEMP_ADDED,
    TRAIT_LIST_ALL_TRAITS_HEADER(Command.TRAIT_LIST, "all_traits_header"),
    TRAIT_LIST_ONE_TRAIT_HEADER(Command.TRAIT_LIST, "one_trait_header"),
    TRAIT_REMOVEALL_REMOVED_ALL_TRAITS(Command.TRAIT_REMOVEALL, "removed_all_traits"),
    NO_PROFILE("no_profile"),
    MULTIPLIER_LIST,
    MULTIPLIER_PLAYERS_ONLY(Command.MULTIPLIER, "players_only"),
    MULTIPLIER_GLOBAL,
    MULTIPLIER_SKILL_ENTRY(Command.MULTIPLIER, "skill_entry"),
    PRESET_LOAD_CONFIRM,
    PRESET_LOAD_LOADED,
    PRESET_LOAD_CREATED,
    PRESET_LOAD_MODIFIED,
    PRESET_LOAD_REPLACED,
    PRESET_LOAD_DELETED,
    PRESET_LOAD_SKIPPED,
    PROFILE_SKILLS_HEADER,
    PROFILE_SKILLS_ENTRY,
    PROFILE_STATS_HEADER,
    PROFILE_STATS_ENTRY,
    RANK_HEADER,
    RANK_POWER,
    RANK_ENTRY,
    RELOAD_RELOADED,
    SAVE_ALREADY_SAVING(Command.SAVE, "already_saving"),
    SAVE_MYSQL_NOT_ENABLED(Command.SAVE, "mysql_not_enabled"),
    SAVE_SAVED,
    SKILL_RESET_RESET_ALL(Command.SKILL_RESET, "reset_all"),
    SKILL_RESET_RESET_SKILL(Command.SKILL_RESET, "reset_skill"),
    SKILL_SETALL_SET,
    SKILL_SETLEVEL_SET,
    SKILL_AT_LEAST("skill.at_least"),
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
    TOP_AVERAGE_HEADER(Command.TOP, "average_header"),
    TOP_AVERAGE_HEADER_PAGE(Command.TOP, "average_header_page"),
    TOP_AVERAGE_ENTRY(Command.TOP, "average_entry"),
    TRANSFER_SUCCESS(Command.TRANSFER, "success"),
    TRANSFER_ERROR(Command.TRANSFER, "error"),
    UPDATELEADERBOARDS_ALREADY_UPDATING(Command.UPDATELEADERBOARDS, "already_updating"),
    UPDATELEADERBOARDS_UPDATED,
    VERSION,
    XP_ADD_ADDED,
    XP_SET_SET,
    XP_REMOVE_REMOVED,
    MANAABILITY_RESETCOOLDOWN_COOLDOWN_RESET(Command.MANAABILITY_RESETCOOLDOWN, "cooldown_reset"),
    UNKNOWN_SKILL("unknown_skill"),
    UNKNOWN_MANA_ABILITY("unknown_mana_ability"),
    UNKNOWN_STAT("unknown_stat"),
    UNKNOWN_TRAIT("unknown_trait");

    private final String path;

    CommandMessage() {
        this.path = "commands." + this.name().toLowerCase(Locale.ROOT).replace("_", ".");
    }
    
    CommandMessage(Command command, String path) {
        this.path = "commands." + command.name().toLowerCase(Locale.ROOT).replace("_", ".") + "." + path;
    }

    CommandMessage(String path) {
        this.path = "commands." + path;
    }

    @Override
    public String getPath() {
        return path;
    }

}
