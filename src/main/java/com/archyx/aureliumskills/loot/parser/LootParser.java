package com.archyx.aureliumskills.loot.parser;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.loot.LootEntry;
import com.archyx.aureliumskills.util.misc.Parser;

import java.util.Map;

public abstract class LootParser extends Parser {

    protected final AureliumSkills plugin;

    public LootParser(AureliumSkills plugin) {
        this.plugin = plugin;
    }

    public abstract LootEntry parse(Map<?, ?> map);

}
