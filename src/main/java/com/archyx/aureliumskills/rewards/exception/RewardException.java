package com.archyx.aureliumskills.rewards.exception;

public class RewardException extends IllegalArgumentException {

    private final String fileName;
    private final String section;
    private final int index;

    public RewardException(String fileName, String section, int index, String message) {
        super(message);
        this.fileName = fileName;
        this.section = section;
        this.index = index;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSection() {
        return section;
    }

    public int getIndex() {
        return index;
    }

}
