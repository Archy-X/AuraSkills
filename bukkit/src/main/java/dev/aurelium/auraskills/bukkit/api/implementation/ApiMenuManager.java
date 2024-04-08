package dev.aurelium.auraskills.bukkit.api.implementation;

import com.archyx.slate.builder.MenuBuilder;
import com.google.common.collect.Sets;
import dev.aurelium.auraskills.api.menu.MenuManager;
import dev.aurelium.auraskills.bukkit.menus.MenuFileManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ApiMenuManager implements MenuManager {

    private final Map<String, Consumer<MenuBuilder>> builders = new HashMap<>();

    @Override
    public void buildMenu(String name, Consumer<MenuBuilder> menu) {
        builders.put(name, menu);
    }

    public Set<String> getNonDefaultMenuNames() {
        Set<String> nonDefault = new HashSet<>();
        for (String menuName : builders.keySet()) {
            if (!Sets.newHashSet(MenuFileManager.MENU_NAMES).contains(menuName)) {
                nonDefault.add(menuName);
            }
        }
        return nonDefault;
    }

    public void applyBuilder(String name, MenuBuilder builder) {
        Consumer<MenuBuilder> consumer = builders.get(name);
        if (consumer == null) return;

        consumer.accept(builder);
    }

}
