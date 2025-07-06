package dev.aurelium.auraskills.common.util.text;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Replacer {

    private final Map<String, Supplier<String>> replacements;

    public Replacer() {
        this.replacements = new ConcurrentHashMap<>();
    }

    public Replacer map(String from, Supplier<String> to) {
        replacements.put(from, to);
        return this;
    }

    public Map<String, Supplier<String>> getReplacements() {
        return replacements;
    }

}
