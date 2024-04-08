package dev.aurelium.auraskills.api.menu;

import dev.aurelium.slate.builder.MenuBuilder;

import java.util.function.Consumer;

public interface MenuManager {

    /**
     * Provides access to a {@link MenuBuilder} for building a Slate menu.
     * If the name is not an AuraSkills default menu, a new MenuBuilder is created and
     * registered to Slate. Otherwise, the menu builder for an existing menu will
     * be provided to extend an existing menu.
     *
     * @param name the name of the menu
     * @param menu a consumer for the menu builder
     */
    void buildMenu(String name, Consumer<MenuBuilder> menu);

}
