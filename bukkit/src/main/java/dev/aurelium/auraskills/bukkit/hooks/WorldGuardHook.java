package dev.aurelium.auraskills.bukkit.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.hooks.Hook;
import dev.aurelium.auraskills.common.util.data.OptionProvider;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WorldGuardHook extends Hook {

    private RegionContainer container;
    private List<String> blockedRegions;
    private List<String> blockedCheckBlockReplaceRegions;
    private final Map<String, StateFlag> stateFlags;

    public WorldGuardHook(AuraSkillsPlugin plugin) {
        super(plugin);
        this.stateFlags = new HashMap<>();
    }

    public void loadRegions(OptionProvider options) {
        try {
            container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            blockedRegions = new LinkedList<>();
            blockedRegions.addAll(options.getStringList("blocked_regions"));
            blockedCheckBlockReplaceRegions = new LinkedList<>();
            blockedCheckBlockReplaceRegions.addAll(options.getStringList("blocked_check_block_replace_regions"));
            Bukkit.getLogger().info("[AureliumSkills] WorldGuard Support Enabled!");
        } catch (Exception e) {
            Bukkit.getLogger().warning("[AureliumSkills] WorldGuard support failed to load, disabling World Guard support!");
        }
    }

    public boolean isInBlockedRegion(Location location) {
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

    public boolean blockedByFlag(Location location, Player player, FlagKey flagKey) {
        StateFlag flag = getStateFlag(flagKey.toString());
        return queryFlagState(location, player, flag);
    }

    public boolean blockedBySkillFlag(Location location, Player player, Skill skill) {
        String flagKey = "xp-gain-" + TextUtil.replace(skill.name().toLowerCase(Locale.ROOT), "_", "-");
        StateFlag flag = getStateFlag(flagKey);
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

    public boolean isInBlockedCheckRegion(Location location) {
        if (location.getWorld() == null) {
            return false;
        }
        RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));
        if (regions == null) {
            return false;
        }
        ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.adapt(location).toVector().toBlockPoint());
        for (ProtectedRegion region : set) {
            if (blockedCheckBlockReplaceRegions.contains(region.getId())) {
                return true;
            }
        }
        return false;
    }


    @Nullable
    public StateFlag getStateFlag(String flagKey) {
        return stateFlags.get(flagKey);
    }

    public void register() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        for (FlagKey flagKey : FlagKey.values()) {
            Object def = flagKey.getDefault();
            // State flags
            if (def instanceof Boolean) {
                String flagName = "auraskills-" + TextUtil.replace(flagKey.toString().toLowerCase(Locale.ROOT), "_", "-");
                try {
                    StateFlag stateFlag = new StateFlag(flagName, (boolean) def);
                    registry.register(stateFlag);
                    stateFlags.put(flagKey.toString(), stateFlag);
                } catch (FlagConflictException e) {
                    Flag<?> existing = registry.get(flagName);
                    if (existing instanceof StateFlag) {
                        stateFlags.put(flagKey.toString(), (StateFlag) existing);
                    } else {
                        Bukkit.getLogger().warning("Could not register flag " + flagName);
                        e.printStackTrace();
                    }
                }
            }
        }
        registerSkillXpGainFlags(registry);
    }

    private void registerSkillXpGainFlags(FlagRegistry registry) {
        for (Skill skill : plugin.getSkillManager().getSkillValues()) {
            String skillName = dev.aurelium.auraskills.common.util.text.TextUtil.replace(skill.toString().toLowerCase(Locale.ROOT), "_", "-");
            String flagName = "auraskills-xp-gain-" + skillName;
            String keyName = "xp-gain-" + skillName;
            try {
                StateFlag stateFlag = new StateFlag(flagName, true);
                registry.register(stateFlag);
                stateFlags.put(keyName, stateFlag);
            } catch (FlagConflictException e) {
                Flag<?> existing = registry.get(flagName);
                if (existing instanceof StateFlag) {
                    stateFlags.put(keyName, (StateFlag) existing);
                } else {
                    plugin.logger().warn("Could not register flag " + flagName);
                    e.printStackTrace();
                }
            }
        }
    }

    public enum FlagKey {

        XP_GAIN(true),
        CUSTOM_LOOT(true);

        private final Object def;

        FlagKey(Object def) {
            this.def = def;
        }

        public Object getDefault() {
            return def;
        }

        @Override
        public String toString() {
            return TextUtil.replace(name().toLowerCase(Locale.ROOT), "_", "-");
        }
    }

}
