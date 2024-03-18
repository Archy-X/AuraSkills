package dev.aurelium.auraskills.bukkit.hooks;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WorldGuardFlags {

    private final AuraSkills plugin;
    private final Map<String, StateFlag> stateFlags;

    public WorldGuardFlags(AuraSkills plugin) {
        this.plugin = plugin;
        this.stateFlags = new HashMap<>();
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
        for (Skill skill : Skills.values()) {
            String skillName = TextUtil.replace(skill.getId().getKey().toLowerCase(Locale.ROOT), "_", "-");
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
