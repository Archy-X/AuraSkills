package com.archyx.aureliumskills.data;

import com.archyx.aureliumskills.abilities.Ability;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AbilityData {

    private final Ability ability;
    private final Map<String, Object> data;

    public AbilityData(Ability ability) {
        this.ability = ability;
        this.data = new HashMap<>();
    }

    public Ability getAbility() {
        return ability;
    }

    @Nullable
    public Object getData(String key) {
        return this.data.get(key);
    }

    public void setData(String key, Object value) {
        this.data.put(key, value);
    }

    public int getInt(String key) {
        return (int) data.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) data.get(key);
    }

    public double getDouble(String key) {
        return (double) data.get(key);
    }

}
