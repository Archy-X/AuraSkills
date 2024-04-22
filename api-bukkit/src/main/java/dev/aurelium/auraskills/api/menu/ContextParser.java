package dev.aurelium.auraskills.api.menu;

@FunctionalInterface
public interface ContextParser<T> {

    /**
     * Parses a context type from the menu name and input string.
     *
     * @param menuName the name of the menu as registered in the plugin
     * @param input the input string, which is usually the name of a map under the contexts map of a template
     * @return the parsed context type
     */
    T parse(String menuName, String input);

}
