package dev.aurelium.auraskills.api.item;

public interface PotionData {

    String[] types();

    String[] excludedTypes();

    boolean extended();

    boolean upgraded();

}