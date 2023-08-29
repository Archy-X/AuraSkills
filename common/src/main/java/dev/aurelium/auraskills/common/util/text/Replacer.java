package dev.aurelium.auraskills.common.util.text;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Replacer {

    private final Map<String, Supplier<String>> replacements;

    public Replacer() {
        this.replacements = new HashMap<>();
    }

    public Replacer map(String from, Supplier<String> to) {
        replacements.put(from, to);
        return this;
    }

    public Map<String, Supplier<String>> getReplacements() {
        return replacements;
    }
}
