package dev.aurelium.auraskills.bukkit.menus.contexts;

import com.archyx.slate.context.ContextProvider;
import dev.aurelium.auraskills.bukkit.menus.sources.SorterItem;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class SortTypeContext implements ContextProvider<SorterItem.SortType> {

    @Nullable
    @Override
    public SorterItem.SortType parse(String menuName, String s) {
        return SorterItem.SortType.valueOf(s.toUpperCase(Locale.ROOT));
    }
}
