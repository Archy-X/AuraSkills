package dev.aurelium.auraskills.bukkit.menus.stats;

import com.archyx.slate.item.provider.ListBuilder;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import com.archyx.slate.position.PositionProvider;
import dev.aurelium.auraskills.api.stat.CustomStat;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.bukkit.trait.TraitImpl;
import dev.aurelium.auraskills.bukkit.util.ConfigurateItemParser;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class StatItem extends AbstractItem implements TemplateItemProvider<Stat> {

    public StatItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public Class<Stat> getContext() {
        return Stat.class;
    }

    @Override
    public Set<Stat> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        return new HashSet<>(plugin.getStatManager().getEnabledStats());
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Stat stat) {
        Locale locale = plugin.getUser(player).getLocale();
        User user = plugin.getUser(player);
        return switch (placeholder) {
            case "color" -> stat.getColor(locale);
            case "stat" -> stat.getDisplayName(locale);
            case "stat_desc" -> stat.getDescription(locale);
            case "level" -> new StatDisplayHelper(plugin).getDisplayLevel(stat, user);
            case "traits" -> getTraitEntries(stat, user, locale, activeMenu, data);
            default -> replaceMenuMessage(placeholder, player, activeMenu);
        };
    }

    public String getTraitEntries(Stat stat, User user, Locale locale, ActiveMenu activeMenu, PlaceholderData data) {
        ListBuilder builder = new ListBuilder(data.getListData());

        for (Trait trait : stat.getTraits()) {
            TraitImpl impl = plugin.getTraitManager().getTraitImpl(trait);
            if (impl == null) continue;

            builder.append(activeMenu.getFormat("trait_entry"),
                    "{trait}", trait.getDisplayName(locale),
                    "{color}", stat.getColor(locale),
                    "{level}", impl.getMenuDisplay(user.getEffectiveTraitLevel(trait), trait));
        }
        return builder.build();
    }

    @Override
    public void onInitialize(Player player, ActiveMenu activeMenu, Stat context) {
        if (!(context instanceof CustomStat stat)) {
            return;
        }
        try {
            ConfigurateItemParser parser = new ConfigurateItemParser(plugin);
            ConfigurationNode config = parser.parseItemContext(stat.getItem());

            PositionProvider provider = parser.parsePositionProvider(config, activeMenu);
            if (provider != null) {
                activeMenu.setPositionProvider("stat", context, provider);
            }
        } catch (SerializationException e) {
            plugin.logger().warn("Error parsing ItemContext of CustomStat " + stat.getId());
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack onItemModify(ItemStack baseItem, Player player, ActiveMenu activeMenu, Stat context) {
        // Handle custom stats
        if (baseItem == null && context instanceof CustomStat stat) {
            try {
                ConfigurateItemParser parser = new ConfigurateItemParser(plugin);

                return parser.parseBaseItem(parser.parseItemContext(stat.getItem()));
            } catch (SerializationException | IllegalArgumentException e) {
                plugin.logger().warn("Error parsing ItemContext of CustomStat " + stat.getId());
                e.printStackTrace();
            }
        }
        return baseItem;
    }
}
