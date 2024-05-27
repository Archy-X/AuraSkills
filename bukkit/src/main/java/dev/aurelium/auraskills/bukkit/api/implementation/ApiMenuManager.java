package dev.aurelium.auraskills.bukkit.api.implementation;

import com.google.common.collect.Sets;
import dev.aurelium.auraskills.api.menu.ContextParser;
import dev.aurelium.auraskills.api.menu.MenuManager;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.MenuFileManager;
import dev.aurelium.auraskills.bukkit.menus.util.LevelProgressionOpener;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.context.ContextProvider;
import dev.aurelium.slate.function.ItemReplacer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class ApiMenuManager implements MenuManager {

    private final AuraSkills plugin;
    private final Map<String, List<Consumer<MenuBuilder>>> builders = new HashMap<>();

    public ApiMenuManager(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public void buildMenu(String name, Consumer<MenuBuilder> menu) {
        builders.computeIfAbsent(name, k -> new ArrayList<>()).add(menu);
    }

    @Override
    public void openMenu(Player player, String name) {
        plugin.getSlate().openMenu(player, name);
    }

    @Override
    public void openMenu(Player player, String name, Map<String, Object> properties) {
        plugin.getSlate().openMenu(player, name, properties);
    }

    @Override
    public void openMenu(Player player, String name, Map<String, Object> properties, int page) {
        plugin.getSlate().openMenu(player, name, properties, page);
    }

    @Override
    public void openLevelProgressionMenu(Player player, Skill skill) {
        new LevelProgressionOpener(plugin).open(player, skill);
    }

    @Override
    public <T> void registerContext(String key, Class<T> contextClass, ContextParser<T> parser) {
        plugin.getSlate().getContextManager().registerContext(key, new ContextProvider<T>() {
            @Override
            public Class<T> getType() {
                return contextClass;
            }

            @Override
            public @Nullable T parse(String menuName, String input) {
                return parser.parse(menuName, input);
            }
        });
    }

    @Override
    public void registerGlobalReplacer(ItemReplacer replacer) {
        plugin.getSlate().getGlobalBehavior().globalReplacers().add(replacer);
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

    public void applyBuilders(String name, MenuBuilder builder) {
        List<Consumer<MenuBuilder>> consumers = builders.get(name);
        if (consumers == null) return;

        for (Consumer<MenuBuilder> consumer : consumers) {
            if (consumer == null) return;

            consumer.accept(builder);
        }
    }

}
