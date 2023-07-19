package dev.aurelium.auraskills.bukkit.leveler;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.leveler.LevelManager;
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
    private final Set<AbstractLeveler> levelers;

    public BukkitLevelManager(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
        this.levelers = new HashSet<>();
    }

    public void registerLevelers() {
        registerLeveler(new BlockLeveler(plugin));
        registerLeveler(new EntityLeveler(plugin));
    }

    private void registerLeveler(AbstractLeveler leveler) {
        this.levelers.add(leveler);
        Bukkit.getPluginManager().registerEvents(leveler, plugin);
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractLeveler> T getLeveler(Class<T> levelerClass) {
        for (AbstractLeveler leveler : levelers) {
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
