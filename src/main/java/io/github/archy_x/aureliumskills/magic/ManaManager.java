package io.github.archy_x.aureliumskills.magic;

import io.github.archy_x.aureliumskills.Options;
import io.github.archy_x.aureliumskills.skills.SkillLoader;
import io.github.archy_x.aureliumskills.stats.PlayerStat;
import io.github.archy_x.aureliumskills.stats.Stat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManaManager implements Listener {

    private Map<UUID, Integer> mana;
    private Plugin plugin;

    public ManaManager(Plugin plugin) {
        this.plugin = plugin;
        mana = new HashMap<>();
    }

    /**
     * Start regenerating Mana
     */
    public void startRegen() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID id : mana.keySet()) {
                    if (SkillLoader.playerStats.containsKey(id)) {
                        PlayerStat stat = SkillLoader.playerStats.get(id);
                        int originalMana = mana.get(id);
                        int maxMana = 20 + 2 * stat.getStatLevel(Stat.WISDOM);
                        if (originalMana < maxMana) {
                            int regen = (int) (stat.getStatLevel(Stat.REGENERATION) * Options.manaModifier);
                            if (originalMana + regen <= maxMana) {
                                mana.put(id, originalMana + regen);
                            } else {
                                mana.put(id, maxMana);
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 20L);
    }

    public int getMana(UUID id) {
        if (mana.containsKey(id)) {
            return mana.get(id);
        }
        else {
            mana.put(id, 20);
            return 20;
        }
    }

    public int getMaxMana(UUID id) {
        if (SkillLoader.playerStats.containsKey(id)) {
            return 20 + (2 * SkillLoader.playerStats.get(id).getStatLevel(Stat.WISDOM));
        }
        else {
            SkillLoader.playerStats.put(id, new PlayerStat(id));
            return 20;
        }
    }

    public void setMana(UUID id, int amount) {
        mana.put(id, amount);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        if (!mana.containsKey(id)) {
            if (SkillLoader.playerStats.containsKey(id)) {
                mana.put(event.getPlayer().getUniqueId(), 20 + 2 * SkillLoader.playerStats.get(id).getStatLevel(Stat.WISDOM));
            }
        }
    }
}
