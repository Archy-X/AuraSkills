package dev.aurelium.auraskills.api.menu;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.function.ItemReplacer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Consumer;

/**
 * An interface used to interact with menus, including building, opening, and extending menus.
 */
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

    /**
     * Opens a menu for a player with the given name.
     *
     * @param player the player to open the menu for
     * @param name the name of the menu as registered in Slate
     */
    void openMenu(Player player, String name);

    /**
     * Opens a menu for a player with the given name and properties.
     *
     * @param player the player to open the menu for
     * @param name the name of the menu as registered in Slate
     * @param properties the properties to pass to the menu
     */
    void openMenu(Player player, String name, Map<String, Object> properties);

    /**
     * Opens a menu for a player with the given name, properties, and page.
     *
     * @param player the player to open the menu for
     * @param name the name of the menu as registered in Slate
     * @param properties the properties to pass to the menu
     * @param page the page to open the menu to
     */
    void openMenu(Player player, String name, Map<String, Object> properties, int page);

    /**
     * Opens the AuraSkills level progression menu for a player. Required properties and the
     * correct page to open to are automatically handled by this method versus the generic
     * openMenu methods.
     *
     * @param player the player to open the menu for
     * @param skill the skill to open to
     */
    void openLevelProgressionMenu(Player player, Skill skill);

    /**
     * Registers a context parser to a context class necessary for creating a template with a
     * custom type parameter.
     *
     * @param key the name of the type in CamelCase
     * @param contextClass the Class instance of the type being registered
     * @param parser a parser that takes in a menuName String and input String to parse the type
     * @param <T> the type class
     */
    <T> void registerContext(String key, Class<T> contextClass, ContextParser<T> parser);

    /**
     * Registers a placeholder replacer that applies to placeholders in all menus. If a placeholder shouldn't
     * be replaced, the {@link ItemReplacer} should return null to not replace, so other global replacers can
     * check that placeholder.
     *
     * @param replacer the placeholder replacer that takes a {@link dev.aurelium.slate.info.PlaceholderInfo} argument
     *                 and returns the replaced String.
     */
    void registerGlobalReplacer(ItemReplacer replacer);

}
