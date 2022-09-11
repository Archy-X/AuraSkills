package com.archyx.aureliumskills.menus.contexts;

import com.archyx.aureliumskills.menus.sources.SorterItem;
import com.archyx.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class SortTypeContext implements ContextProvider<SorterItem.SortType> {

    @Nullable
    @Override
    public SorterItem.SortType parse(String menuName, String s) {
        return SorterItem.SortType.valueOf(s.toUpperCase(Locale.ROOT));
    }
}
