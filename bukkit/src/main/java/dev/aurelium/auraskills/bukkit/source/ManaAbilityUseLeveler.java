package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.event.AuraSkillsEventHandler;
import dev.aurelium.auraskills.api.event.AuraSkillsListener;
import dev.aurelium.auraskills.api.event.mana.ManaAbilityActivateEvent;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.type.ManaAbilityUseXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.api.implementation.ApiSkillsUser;
import dev.aurelium.auraskills.common.source.SourceType;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ManaAbilityUseLeveler extends SourceLeveler implements AuraSkillsListener {

    public ManaAbilityUseLeveler(AuraSkills plugin) {
        super(plugin, SourceType.MANA_ABILITY_USE);
    }

    @AuraSkillsEventHandler
    public void onManaAbilityActivate(ManaAbilityActivateEvent event) {
        Player player = ((BukkitUser) ((ApiSkillsUser) event.getUser()).getUser()).getPlayer();

        var sourcePair = getSource(event.getManaAbility());
        if (sourcePair == null) return;

        ManaAbilityUseXpSource source = sourcePair.first();
        Skill skill = sourcePair.second();

        if (failsChecks(event, player, player.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(plugin.getUser(player), skill, source.getXp() * event.getManaUsed());
    }

    @Nullable
    private Pair<ManaAbilityUseXpSource, Skill> getSource(ManaAbility manaAbility) {
        for (Map.Entry<ManaAbilityUseXpSource, Skill> entry : plugin.getSkillManager().getSourcesOfType(ManaAbilityUseXpSource.class).entrySet()) {
            var source = entry.getKey();
            ManaAbility[] manaAbilities = source.getManaAbilities();
            if (manaAbilities == null) { // Source with no mana abilities specified always matches
                return Pair.fromEntry(entry);
            }

            for (ManaAbility search : manaAbilities) {
                if (manaAbility.equals(search)) {
                    return Pair.fromEntry(entry);
                }
            }
        }
        return null;
    }

}
