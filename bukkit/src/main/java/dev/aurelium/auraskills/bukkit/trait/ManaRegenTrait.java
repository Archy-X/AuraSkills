package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.event.mana.ManaRegenerateEvent;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ManaRegenTrait extends TraitImpl {

    ManaRegenTrait(AuraSkills plugin) {
        super(plugin, Traits.MANA_REGEN);
        startRegen();
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return Traits.MANA_REGEN.optionDouble("base");
    }

    @Override
    public String getMenuDisplay(double value, Trait trait, Locale locale) {
        return NumberUtil.format1(value) + "/s";
    }

    public void startRegen() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                if (!Traits.MANA_REGEN.isEnabled()) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    User user = plugin.getUser(player);
                    double originalMana = user.getMana();
                    double maxMana = user.getMaxMana();
                    if (originalMana < maxMana) {
                        if (!user.getAbilityData(ManaAbilities.ABSORPTION).getBoolean("activated")) {
                            double regen = user.getEffectiveTraitLevel(Traits.MANA_REGEN);
                            double finalRegen = Math.min(originalMana + regen, maxMana) - originalMana;
                            ManaRegenerateEvent event = new ManaRegenerateEvent(player, user.toApi(), finalRegen);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                user.setMana(originalMana + event.getAmount());
                            }
                        }
                    }
                }
            }
        };
        plugin.getScheduler().timerSync(task, 0, 1, TimeUnit.SECONDS);
    }

}
