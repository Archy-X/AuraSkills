package dev.aurelium.auraskills.bukkit.leveler;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.leveler.LevelManager;
import dev.aurelium.auraskills.common.player.User;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class BukkitLevelManager extends LevelManager {

    private final Set<AbstractLeveler> levelers;

    public BukkitLevelManager(AuraSkillsPlugin plugin) {
        super(plugin);
        this.levelers = new HashSet<>();
    }

    public void registerLeveler(AbstractLeveler leveler) {
        this.levelers.add(leveler);
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
            player.playSound(player.getLocation(), Sound.valueOf(OptionL.getString(Option.LEVELER_SOUND_TYPE))
                    , SoundCategory.valueOf(OptionL.getString(Option.LEVELER_SOUND_CATEGORY))
                    , (float) OptionL.getDouble(Option.LEVELER_SOUND_VOLUME), (float) OptionL.getDouble(Option.LEVELER_SOUND_PITCH));
        } catch (Exception e) {
            Bukkit.getLogger().warning("[AureliumSkills] Error playing level up sound (Check config) Played the default sound instead");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1f, 0.5f);
        }
    }
}
