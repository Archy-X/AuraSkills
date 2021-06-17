package com.archyx.aureliumskills.loot.parser;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.Loot;
import com.archyx.aureliumskills.util.misc.Parser;
import com.archyx.aureliumskills.util.text.TextUtil;

import java.util.Map;

public abstract class LootParser extends Parser {

    protected final AureliumSkills plugin;

    public LootParser(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public abstract Loot parse(Map<?, ?> map);

    protected int getWeight(Map<?, ?> map) {
        if (map.containsKey("weight")) {
            return getInt(map, "weight");
        } else {
            return 10;
        }
    }

    protected String getMessage(Map<?, ?> map) {
        if (map.containsKey("message")) {
            return TextUtil.replace(getString(map, "message"), "&", "§");
        } else {
            return "";
        }
    }

}
