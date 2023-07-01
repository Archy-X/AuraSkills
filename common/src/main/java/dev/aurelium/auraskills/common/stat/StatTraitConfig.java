package dev.aurelium.auraskills.common.stat;

import dev.aurelium.auraskills.common.util.data.OptionProvider;

import java.util.Map;

public class StatTraitConfig extends OptionProvider {

    public StatTraitConfig(Map<String, Object> optionMap) {
        super(optionMap);
    }

    public double modifier() {
        return getDouble("modifier");
    }

}
