package dev.aurelium.auraskills.bukkit.level;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.source.*;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.level.LevelManager;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class BukkitLevelManager extends LevelManager {

    private final AuraSkills plugin;
    private final Set<SourceLeveler> levelers;

    public BukkitLevelManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
        this.levelers = new HashSet<>();
    }

    public void registerLevelers() {
        registerLeveler(new AnvilLeveler(plugin));
        registerLeveler(new BlockLeveler(plugin));
        registerLeveler(new BrewingLeveler(plugin));
        registerLeveler(new DamageLeveler(plugin));
        registerLeveler(new EnchantingLeveler(plugin));
        registerLeveler(new EntityLeveler(plugin));
        registerLeveler(new FishingLeveler(plugin));
        registerLeveler(new GrindstoneLeveler(plugin));
        registerLeveler(new ItemConsumeLeveler(plugin));
    }

    private void registerLeveler(SourceLeveler leveler) {
        this.levelers.add(leveler);
        Bukkit.getPluginManager().registerEvents(leveler, plugin);
    }

    @SuppressWarnings("unchecked")
    public <T extends SourceLeveler> T getLeveler(Class<T> levelerClass) {
        for (SourceLeveler leveler : levelers) {
            if (levelerClass.isInstance(leveler)) {
                return (T) leveler;
            }
        }
        // No leveler found
        throw new IllegalArgumentException("Leveler " + levelerClass.getSimpleName() + " is not registered!");
    }

    @Override
    public void playLevelUpSound(@NotNull User user) {
        Player player = ((BukkitUser) user).getPlayer();
        try {
            player.playSound(player.getLocation(), Sound.valueOf(plugin.configString(Option.LEVELER_SOUND_TYPE))
                    , SoundCategory.valueOf(plugin.configString(Option.LEVELER_SOUND_CATEGORY))
                    , (float) plugin.configDouble(Option.LEVELER_SOUND_VOLUME), (float) plugin.configDouble(Option.LEVELER_SOUND_PITCH));
        } catch (Exception e) {
            Bukkit.getLogger().warning("[AureliumSkills] Error playing level up sound (Check config) Played the default sound instead");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1f, 0.5f);
        }
    }
}
