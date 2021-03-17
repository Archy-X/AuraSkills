package com.archyx.aureliumskills.rewards;

public class RewardException extends IllegalArgumentException {

    private final String fileName;
    private final String path;

    public RewardException(String fileName, String path, String message) {
        super(message);
        this.fileName = fileName;
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }


}
