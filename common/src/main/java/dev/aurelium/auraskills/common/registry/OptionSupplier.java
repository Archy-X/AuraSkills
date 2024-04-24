package dev.aurelium.auraskills.common.registry;

import dev.aurelium.auraskills.api.option.OptionedProvider;
import dev.aurelium.auraskills.common.util.data.OptionProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OptionSupplier<T> implements OptionedProvider<T> {

    public abstract OptionProvider getOptions(T type);

    public abstract boolean isLoaded(T type);

    @Override
    public boolean optionBoolean(T type, String key) {
        return getOptions(type).getBoolean(key);
    }

    @Override
    public boolean optionBoolean(T type, String key, boolean def) {
        return isLoaded(type) ? getOptions(type).getBoolean(key, def) : def;
    }

    @Override
    public int optionInt(T type, String key) {
        return getOptions(type).getInt(key);
    }

    @Override
    public int optionInt(T type, String key, int def) {
        return isLoaded(type) ? getOptions(type).getInt(key, def) : def;
    }

    @Override
    public double optionDouble(T type, String key) {
        return getOptions(type).getDouble(key);
    }

    @Override
    public double optionDouble(T type, String key, double def) {
        return isLoaded(type) ? getOptions(type).getDouble(key, def) : def;
    }

    @Override
    public String optionString(T type, String key) {
        return getOptions(type).getString(key);
    }

    @Override
    public String optionString(T type, String key, String def) {
        return isLoaded(type) ? getOptions(type).getString(key, def) : def;
    }

    @Override
    public List<String> optionStringList(T type, String key) {
        return isLoaded(type) ? getOptions(type).getStringList(key) : new ArrayList<>();
    }

    @Override
    public Map<String, Object> optionMap(T type, String key) {
        return isLoaded(type) ? getOptions(type).getMap(key) : new HashMap<>();
    }
}
