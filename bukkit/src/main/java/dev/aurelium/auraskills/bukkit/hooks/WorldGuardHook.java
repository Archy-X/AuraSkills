package dev.aurelium.auraskills.bukkit.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.WorldGuardFlags.FlagKey;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.hooks.HookRegistrationException;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

public class WorldGuardHook extends Hook {

    private final AuraSkills plugin;
    private RegionContainer container;
    private List<String> blockedRegions;
    private List<String> blockedCheckBlockReplaceRegions;

    public WorldGuardHook(AuraSkills plugin, ConfigurationNode config) {
        super(plugin, config);
        this.plugin = plugin;
        try {
            loadRegions(config);
        } catch (SerializationException e) {
            throw new HookRegistrationException("Error serializing config list");
        }
    }

    public void loadRegions(ConfigurationNode config) throws SerializationException {
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        blockedRegions = new LinkedList<>();
        blockedRegions.addAll(config.node("blocked_regions").getList(String.class, new ArrayList<>()));
        blockedCheckBlockReplaceRegions = new LinkedList<>();
        blockedCheckBlockReplaceRegions.addAll(config.node("blocked_check_replace_regions").getList(String.class, new ArrayList<>()));
    }

    public boolean isBlocked(Location location, Player player, FlagKey flagKey) {
        if (isInBlockedRegion(location)) {
            return true;
        }
        return blockedByFlag(location, player, flagKey);
    }

    public boolean isBlocked(Location location, Player player, Skill skill) {
        if (isInBlockedRegion(location)) {
            return true;
        }
        if (blockedByFlag(location, player, FlagKey.XP_GAIN)) {
            return true;
        }
        return blockedBySkillFlag(location, player, skill);
    }

    private boolean isInBlockedRegion(Location location) {
        return isInRegionList(location, blockedRegions);
    }

    public boolean isInBlockedCheckRegion(Location location) {
        return isInRegionList(location, blockedCheckBlockReplaceRegions);
    }

    private boolean isInRegionList(Location location, List<String> blockedRegions) {
        if (location.getWorld() == null) {
            return false;
        }
        RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));
        if (regions == null) {
            return false;
        }
        ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.adapt(location).toVector().toBlockPoint());
        for (ProtectedRegion region : set) {
            if (blockedRegions.contains(region.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean blockedByFlag(Location location, Player player, FlagKey flagKey) {
        WorldGuardFlags flags = plugin.getWorldGuardFlags();
        if (flags == null) return false;

        StateFlag flag = flags.getStateFlag(flagKey.toString());
        return queryFlagState(location, player, flag);
    }

    private boolean blockedBySkillFlag(Location location, Player player, Skill skill) {
        WorldGuardFlags flags = plugin.getWorldGuardFlags();
        if (flags == null) return false;

        String flagKey = "xp-gain-" + TextUtil.replace(skill.name().toLowerCase(Locale.ROOT), "_", "-");
        StateFlag flag = flags.getStateFlag(flagKey);
        return queryFlagState(location, player, flag);
    }

    private boolean queryFlagState(Location location, Player player, StateFlag flag) {
        if (flag == null) return false;

        World world = location.getWorld();
        if (world == null) return false;

        RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));
        if (regions == null) {
            return false;
        }
        ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.adapt(location).toVector().toBlockPoint());
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        StateFlag.State state = set.queryState(localPlayer, flag);
        return state == StateFlag.State.DENY;
    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return WorldGuardHook.class;
    }


}
