package dev.aurelium.auraskills.bukkit.source;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.AnvilXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.util.VersionUtils;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.Nullable;

public class AnvilLeveler extends SourceLeveler {

    public AnvilLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.ANVIL);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAnvilCombine(InventoryClickEvent event) {
        if (disabled()) return;
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (failsClickChecks(event)) return;

        if (!inventory.getType().equals(InventoryType.ANVIL)) {
            return;
        }
        if (event.getSlot() != 2) {
            return;
        }

        User user = plugin.getUser(player);

        ItemStack leftItem = inventory.getItem(0);
        ItemStack rightItem = inventory.getItem(1);
        if (leftItem == null || rightItem == null) return;

        SkillSource<AnvilXpSource> skillSource = getSource(leftItem, rightItem);
        if (skillSource == null) return;

        AnvilXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        Location location = inventory.getLocation() != null ? inventory.getLocation() : player.getLocation();

        if (failsChecks(event, player, location, skill)) return;

        AnvilInventory anvil = (AnvilInventory) inventory;
        double multiplier = getRepairCostMultiplier(source, anvil, skill, event);

        plugin.getLevelManager().addXp(user, skill, source, multiplier * source.getXp());
    }

    @SuppressWarnings("removal")
    private double getRepairCostMultiplier(AnvilXpSource source, AnvilInventory anvil, Skill skill, InventoryClickEvent event) {
        int repairCost = 1;
        if (VersionUtils.isAtLeastVersion(21, 1)) {
            if (event.getView() instanceof AnvilView view) {
                repairCost = view.getRepairCost();
            }
        } else {
            repairCost = anvil.getRepairCost();
        }
        // Get the repair cost multiplier from placeholder
        double multiplier = 1;
        String multiplierString = source.getMultiplier();
        if (multiplierString != null) {
            multiplierString = TextUtil.replace(multiplierString, "{repair_cost}", String.valueOf(repairCost));
            Expression expression = new Expression(multiplierString);
            try {
                multiplier = expression.evaluate().getNumberValue().doubleValue();
            } catch (EvaluationException | ParseException e) {
                plugin.logger().warn("Invalid multiplier for anvil source " + source.getId() + " in skill " + skill.getId());
                e.printStackTrace();
            }
        }
        return multiplier;
    }

    @Nullable
    private SkillSource<AnvilXpSource> getSource(ItemStack leftItem, ItemStack rightItem) {
        for (SkillSource<AnvilXpSource> entry : plugin.getSkillManager().getSourcesOfType(AnvilXpSource.class)) {
            AnvilXpSource source = entry.source();
            boolean leftMatches = plugin.getItemRegistry().passesFilter(leftItem, source.getLeftItem());
            boolean rightMatches = plugin.getItemRegistry().passesFilter(rightItem, source.getRightItem());
            if (leftMatches && rightMatches) {
                return entry;
            }
        }
        return null;
    }

}
