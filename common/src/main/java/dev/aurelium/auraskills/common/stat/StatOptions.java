package dev.aurelium.auraskills.common.stat;

import dev.aurelium.auraskills.common.util.data.OptionProvider;

import java.util.Map;

public class StatOptions extends OptionProvider {

    public StatOptions(Map<String, Object> optionMap) {
        super(optionMap);
    }

    public boolean enabled() {
        return getBoolean("enabled");
    }

}
