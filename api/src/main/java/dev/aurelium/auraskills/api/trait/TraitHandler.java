package dev.aurelium.auraskills.api.trait;

import dev.aurelium.auraskills.api.util.NumberUtil;

import java.util.Locale;

public interface TraitHandler {

    Trait[] getTraits();

    default String getMenuDisplay(double value, Trait trait, Locale locale) {
        return NumberUtil.format1(value);
    }

}
