package com.archyx.aureliumskills.util.misc;

import java.util.List;
import java.util.Map;

public class Parser {

    protected Object getElement(Map<?, ?> map, String key) {
        return DataUtil.getElement(map, key);
    }

    protected String getString(Map<?, ?> map, String key) {
        return DataUtil.getString(map, key);
    }

    protected double getDouble(Map<?, ?> map, String key) {
        return DataUtil.getDouble(map, key);
    }

    protected int getInt(Map<?, ?> map, String key) {
        return DataUtil.getInt(map, key);
    }

    protected boolean getBoolean(Map<?, ?> map, String key) {
        return DataUtil.getBoolean(map, key);
    }

    protected List<String> getStringList(Map<?, ?> map, String key) {
        return DataUtil.getStringList(map, key);
    }

    protected Map<?, ?> getMap(Map<?, ?> map, String key) {
        return DataUtil.getMap(map, key);
    }

    protected List<Map<?, ?>> getMapList(Map<?, ?> map, String key) {
        return DataUtil.getMapList(map, key);
    }

}
