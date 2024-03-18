package dev.aurelium.auraskills.api.option;

import java.util.List;
import java.util.Map;

public interface Optioned {

    boolean optionBoolean(String key);

    boolean optionBoolean(String key, boolean def);

    int optionInt(String key);

    int optionInt(String key, int def);

    double optionDouble(String key);

    double optionDouble(String key, double def);

    String optionString(String key);

    String optionString(String key, String def);

    List<String> optionStringList(String key);

    Map<String, Object> optionMap(String key);

}
