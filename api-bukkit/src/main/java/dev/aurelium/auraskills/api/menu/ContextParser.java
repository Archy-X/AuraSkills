package dev.aurelium.auraskills.api.menu;

@FunctionalInterface
public interface ContextParser<T> {

    T parse(String menuName, String input);

}
