package dev.aurelium.auraskills.api.trait;

import dev.aurelium.auraskills.api.util.NumberUtil;

public interface TraitHandler {

    Trait[] getTraits();

    default String getMenuDisplay(double value, Trait trait) {
        return NumberUtil.format1(value);
    }

}
