package dev.aurelium.auraskills.bukkit.menus.contexts;

import com.archyx.slate.context.ContextProvider;
import dev.aurelium.auraskills.bukkit.menus.SourcesMenu.SortType;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class SortTypeContext implements ContextProvider<SortType> {

    @Override
    public Class<SortType> getType() {
        return SortType.class;
    }

    @Nullable
    @Override
    public SortType parse(String menuName, String s) {
        return SortType.valueOf(s.toUpperCase(Locale.ROOT));
    }
}
