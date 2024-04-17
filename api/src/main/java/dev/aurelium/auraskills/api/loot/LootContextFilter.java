package dev.aurelium.auraskills.api.loot;

@FunctionalInterface
public interface LootContextFilter {

    boolean passesFilter(Loot loot);

}
