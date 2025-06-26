package dev.aurelium.auraskills.common.loot;

import dev.aurelium.auraskills.api.loot.LootDropCause;
import dev.aurelium.auraskills.api.loot.LootPool;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;

public abstract class SkillLootProvider {

    protected final AuraSkillsPlugin plugin;
    protected final AbstractLootHandler handler;

    public SkillLootProvider(AuraSkillsPlugin plugin, AbstractLootHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    public abstract LootDropCause getCause(LootPool pool);

    public double getChance(LootPool pool, User user) {
        return handler.getCommonChance(pool, user);
    }

    public boolean isApplicable(LootPool pool, XpSource source) {
        return true;
    }

}
