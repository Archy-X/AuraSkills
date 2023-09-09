package dev.aurelium.auraskills.bukkit.mana;

import com.archyx.aureliumskills.util.math.NumberUtil;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.message.type.ManaAbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class ReadiedManaAbility extends ManaAbilityProvider {

    private final String[] materials;
    private final Action[] actions;

    public ReadiedManaAbility(AuraSkills plugin, ManaAbility manaAbility, ManaAbilityMessage activateMessage, @Nullable ManaAbilityMessage stopMessage, String[] materials, Action[] actions) {
        super(plugin, manaAbility, activateMessage, stopMessage);
        this.materials = materials;
        this.actions = actions;
    }

    @Override
    protected boolean isReady(User user) {
        ManaAbilityData data = user.getManaAbilityData(manaAbility);
        return data.isReady() && !data.isActivated();
    }

    protected boolean isHoldingMaterial(Player player) {
        return materialMatches(player.getInventory().getItemInMainHand().getType().toString());
    }

    protected boolean materialMatches(String checked) {
        for (String material : materials) {
            if (checked.contains(material)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isExcludedBlock(Block block) {
        return false;
    }

    public String[] getMaterials() {
        return materials;
    }

    @EventHandler
    public void onReady(PlayerInteractEvent event) {
        if (isDisabled()) return;

        // Check action is valid
        boolean valid = false;
        for (Action action : actions) {
            if (event.getAction() == action) {
                valid = true;
                break;
            }
        }
        if (!valid) return;
        // Check block exclusions
        Block block = event.getClickedBlock();
        if (block != null) {
            if (isExcludedBlock(block)) return;
        }
        // Match sure material matches
        Player player = event.getPlayer();
        if (!isHoldingMaterial(player)) {
            return;
        }
        if (!isAllowReady(player, event)) {
            return;
        }
        User user = plugin.getUser(player);
        ManaAbilityData data = user.getManaAbilityData(manaAbility);

        Locale locale = user.getLocale();
        if (user.getManaAbilityLevel(manaAbility) <= 0) {
            return;
        }
        // Check if already activated
        if (data.isActivated()) {
            return;
        }
        // Checks if already ready
        if (data.isReady()) {
            return;
        }
        if (data.getCooldown() == 0) { // Ready
            data.setReady(true);
            plugin.getAbilityManager().sendMessage(player, plugin.getMsg(ManaAbilityMessage.valueOf(manaAbility.name() + "_RAISE"), locale));
            scheduleUnready(player, locale, data);
        } else { // Cannot ready, send cooldown error
            if (data.getErrorTimer() == 0) {
                plugin.getAbilityManager().sendMessage(player, plugin.getMsg(ManaAbilityMessage.NOT_READY, locale).replace("{cooldown}",
                        NumberUtil.format0((double) data.getCooldown() / 20)));
                data.setErrorTimer(2);
            }
        }
    }

    private void scheduleUnready(Player player, Locale locale, ManaAbilityData data) {
        int READY_DURATION = 80;
        plugin.getScheduler().scheduleSync(() -> {
            if (!data.isActivated()) {
                if (data.isReady()) {
                    data.setReady(false);
                    plugin.getAbilityManager().sendMessage(player, plugin.getMsg(ManaAbilityMessage.valueOf(manaAbility.name() + "_LOWER"), locale));
                }
            }
        }, READY_DURATION * 50, TimeUnit.MILLISECONDS);
    }

    private boolean isAllowReady(Player player, PlayerInteractEvent event) {
        // Check if requires sneak
        if (manaAbility.optionBoolean("require_sneak", false)) {
            if (!player.isSneaking()) return false;
        }
        // Check if the offhand item is being placed
        if (isBlockPlace(event, player, manaAbility)) {
            return false;
        }
        // Check disabled worlds
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return false;
        }
        // Check permission
        return player.hasPermission("auraskills.skill." + manaAbility.getSkill().name().toLowerCase(Locale.ROOT));
    }

    private boolean isBlockPlace(PlayerInteractEvent event, Player player, ManaAbility manaAbility) {
        if (manaAbility.optionBoolean("check_offhand", true)) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (player.isSneaking() && manaAbility.optionBoolean("sneak_offhand_bypass", true)) {
                    return false;
                }
                ItemStack item = player.getInventory().getItemInOffHand();
                if (item.getType() == Material.AIR) return false;
                return item.getType().isBlock();
            }
        }
        return false;
    }
}
