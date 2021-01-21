package com.archyx.aureliumskills.lang;

public enum MessageUpdates {

    LEVELER_FORMAT(9, "leveler", "The leveler section was changed to the new default messages due to significant format changes. If you had changed these messages before, you will need to change them again to follow the new format."),
    ABSORPTION(10, "mana_abilities.absorption", "The absorption messages were changed to match the mechanics in the new update");

    private final int version;
    private final String section;
    private final String message;

    MessageUpdates(int version, String section, String message) {
        this.version = version;
        this.section = section;
        this.message = message;
    }

    public int getVersion() {
        return version;
    }

    public String getSection() {
        return section;
    }

    public String getMessage() {
        return message;
    }

}
