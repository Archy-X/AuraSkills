package dev.aurelium.auraskills.common.antiafk;

public interface CheckType {

    Class<? extends Check> getCheckClass();

    String name();

}
