package dev.aurelium.auraskills.api.option;

import java.util.List;

public interface OptionedProvider<T> {

    boolean optionBoolean(T type, String key);

    boolean optionBoolean(T type, String key, boolean def);

    int optionInt(T type, String key);

    int optionInt(T type, String key, int def);

    double optionDouble(T type, String key);

    double optionDouble(T type, String key, double def);

    String optionString(T type, String key);

    String optionString(T type, String key, String def);

    List<String> optionStringList(T type, String key);

}
