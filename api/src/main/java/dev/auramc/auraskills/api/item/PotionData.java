package dev.auramc.auraskills.api.item;

public interface PotionData {

    String[] getTypes();

    String[] getExcludedTypes();

    boolean extended();

    boolean upgraded();

}