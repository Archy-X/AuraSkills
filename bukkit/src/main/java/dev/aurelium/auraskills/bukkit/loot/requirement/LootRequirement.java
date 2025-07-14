package dev.aurelium.auraskills.bukkit.loot.requirement;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.aurelium.auraskills.api.config.ConfigNode;
import dev.aurelium.auraskills.api.loot.LootRequirements;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LootRequirement {

    public static boolean passes(LootRequirements requirements, User user, AuraSkills plugin) {
        List<ConfigNode> requirementNodes = requirements.getRequirements();
        // If there are no requirements we pass it by default.
        if (requirements == null || requirementNodes == null || requirementNodes.isEmpty()) {
            return true;
        }

        for (ConfigNode config : requirementNodes) {
            if (config.hasChild("type")) {
                if (checkType(config, user, plugin) == false) {
                    return false;
                }
            }
        }

        return true;
    }

    protected static boolean checkType(ConfigNode config, User user, AuraSkills plugin) {
        if (config.hasChild("type")) {
            switch (config.node("type").getString()) {
                case "skill_level":
                    return checkSkillLevel(config, user);
                case "stat_level":
                    return checkStatLevel(config, user);
                case "permission":
                    return checkPermission(config, user);
                case "world":
                    return checkWorld(config, user);
                case "biome":
                    return checkBiome(config, user);
                case "region":
                    return checkRegion(config, user, plugin);
                case "enchantment":
                    return checkEnchantment(config, user);
            }
        }

        return true;
    }

    protected static boolean checkSkillLevel(ConfigNode config, User user) {
        if (config.hasChild("skill") && config.hasChild("level")) {
            Skill skill = Skills.valueOf(config.node("skill").getString().toUpperCase(Locale.ROOT));
            int level = user.getSkillLevel(skill);
            if (level < config.node("level").getInt()) {
                return false;
            }
        }
        return true;
    }

    protected static boolean checkStatLevel(ConfigNode config, User user) {
        if (config.hasChild("stat") && config.hasChild("level")) {
            Stat stat = Stats.valueOf(config.node("stat").getString().toUpperCase(Locale.ROOT));
            double level = user.getStatLevel(stat);
            if (level < config.node("level").getInt()) {
                return false;
            }
        }
        return true;
    }

    protected static boolean checkPermission(ConfigNode config, User user) {
        if (config.hasChild("permission")) {
            if (!Bukkit.getPlayer(user.getUuid()).hasPermission(config.node("permission").getString())) {
                return false;
            }
        }
        return true;
    }

    protected static boolean checkWorld(ConfigNode config, User user) {
        if (config.hasChild("world")) {
            ConfigNode worldRequirement = config.node("world");
            String playerWorldName = Bukkit.getPlayer(user.getUuid()).getLocation().getWorld().getName().toUpperCase(Locale.ROOT);

            if (worldRequirement.isList()) {
                List<String> worldRequirementList = worldRequirement.getList(String.class)
                                                                    .stream()
                                                                    .map(s -> s.toUpperCase(Locale.ROOT))
                                                                    .collect(Collectors.toList());
                if (!worldRequirementList.contains(playerWorldName)) {
                    return false;
                }
            } else {
                if (!worldRequirement.getString().toUpperCase(Locale.ROOT).equals(playerWorldName)) {
                    return false;
                }
            }
        }

        return true;
    }

    protected static boolean checkBiome(ConfigNode config, User user) {
        if (config.hasChild("biome")) {
            ConfigNode biomeRequirement = config.node("biome");
            String playerBiomeName = Bukkit.getPlayer(user.getUuid()).getLocation().getBlock().getBiome().toString().toUpperCase(Locale.ROOT);

            if (biomeRequirement.isList()) {
                List<String> biomeRequirementList = biomeRequirement.getList(String.class)
                                                                    .stream()
                                                                    .map(s -> s.toUpperCase(Locale.ROOT))
                                                                    .collect(Collectors.toList());
                if (!biomeRequirementList.contains(playerBiomeName)) {
                    return false;
                }
            } else {
                if (!biomeRequirement.getString().toUpperCase(Locale.ROOT).equals(playerBiomeName)) {
                    return false;
                }
            }
        }

        return true;
    }

    protected static boolean checkRegion(ConfigNode config, User user, AuraSkills plugin) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("WorldGuard") && config.hasChild("region")) {
            Player player = Bukkit.getPlayer(user.getUuid());
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));

            if (regions != null) {
                ConfigNode regionRequirement = config.node("region");
                List<String> regionRequirementList = new ArrayList<String>();
                String regionRequirementID = regionRequirement.getString().toUpperCase(Locale.ROOT);
                ApplicableRegionSet regionSet = regions.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));

                if (regionRequirement.isList()) {
                    regionRequirementList = regionRequirement.getList(String.class)
                                                             .stream()
                                                             .map(s -> s.toUpperCase(Locale.ROOT))
                                                             .collect(Collectors.toList());
                }

                if (regionSet.isVirtual() || regionSet.getRegions().isEmpty()) {
                    return false;
                }

                for (ProtectedRegion region : regionSet) {
                    if (regionRequirement.isList()) {
                        if (!regionRequirementList.contains(region.getId().toUpperCase(Locale.ROOT))) {
                            return false;
                        }
                    } else {
                        if (!regionRequirementID.equals(region.getId().toUpperCase(Locale.ROOT))) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    protected static boolean checkEnchantment(ConfigNode config, User user) {
        if (config.hasChild("enchantment")) {
            Player player = Bukkit.getPlayer(user.getUuid());
            ItemStack heldItem = player.getInventory().getItemInMainHand();

            Enchantment enchantment = null;

            String[] splitEntry = config.node("enchantment").getString("").split(" ");
            String enchantmentName = splitEntry[0].toLowerCase(Locale.ROOT);
            int enchantmentLevel = splitEntry.length > 1 ? Integer.parseInt(splitEntry[1]) : 0;

            NamespacedKey enchantmentKey = NamespacedKey.minecraft(enchantmentName);
            if (enchantmentKey != null) {
                enchantment = Registry.ENCHANTMENT.get(enchantmentKey);
            }

            if (enchantment == null) {
                return true;
            }

            if (heldItem != null && heldItem.hasItemMeta() && heldItem.getItemMeta().hasEnchants() && heldItem.getItemMeta().hasEnchant(enchantment)) {
                if (enchantmentLevel == 0 || heldItem.getEnchantmentLevel(enchantment) == enchantmentLevel) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

}
