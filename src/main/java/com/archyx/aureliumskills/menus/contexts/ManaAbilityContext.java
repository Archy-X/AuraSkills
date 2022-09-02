package com.archyx.aureliumskills.menus.contexts;

import com.archyx.aureliumskills.mana.MAbility;
import com.archyx.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ManaAbilityContext implements ContextProvider<@NotNull MAbility> {

    @Override
    @Nullable
    public MAbility parse(String input) {
        return MAbility.valueOf(input.toUpperCase(Locale.ROOT));
    }
}
