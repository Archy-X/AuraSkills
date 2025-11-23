package dev.aurelium.auraskills.api.source;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.AuraSkillsBukkit;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.user.SkillsUser;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.Nullable;

public class LevelerContext {

    private final AuraSkillsApi api;
    private final @Nullable SourceType sourceType;

    public LevelerContext(AuraSkillsApi api) {
        this(api, null);
    }

    public LevelerContext(AuraSkillsApi api, @Nullable SourceType sourceType) {
        this.api = api;
        this.sourceType = sourceType;
    }

    public boolean disabled() {
        if (sourceType != null) {
            return !sourceType.isEnabled();
        }
        return false;
    }

    public boolean failsChecks(Cancellable event, Player player, Location location, Skill skill) {
        return isDisabled(skill) || isCancelled(event, skill) || blockLocation(player, location, skill) || blockPlayer(player, skill);
    }

    public boolean failsChecks(Player player, Location location, Skill skill) {
        return isDisabled(skill) || blockLocation(player, location, skill) || blockPlayer(player, skill);
    }

    public boolean isDisabled(Skill skill) {
        return !skill.isEnabled();
    }

    public boolean isCancelled(Cancellable event, Skill skill) {
        return skill.optionBoolean("check_cancelled", true) && event.isCancelled();
    }

    public boolean blockLocation(Player player, Location location, Skill skill) {
        return AuraSkillsBukkit.get().getLocationManager().isXpGainBlocked(location, player, skill);
    }

    public boolean blockPlayer(Player player, Skill skill) {
        SkillsUser skillsUser = AuraSkillsApi.get().getUser(player.getUniqueId());
        if (!skillsUser.hasSkillPermission(skill)) {
            return true;
        }
        // Check creative mode disable
        return api.getMainConfig().isDisabledInCreative() && player.getGameMode().equals(GameMode.CREATIVE);
    }

}
