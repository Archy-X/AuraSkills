package com.archyx.aureliumskills.rewards.parser;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.rewards.Reward;
import com.archyx.aureliumskills.util.misc.DataUtil;

import java.util.Map;

public abstract class RewardParser {

    protected final AureliumSkills plugin;

    public RewardParser(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public abstract Reward parse(Map<?, ?> map);

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

}
