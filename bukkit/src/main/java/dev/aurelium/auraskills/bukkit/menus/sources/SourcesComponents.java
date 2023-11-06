package dev.aurelium.auraskills.bukkit.menus.sources;

import com.archyx.slate.component.ComponentData;
import com.archyx.slate.component.ComponentProvider;
import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.AbstractComponent;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.Locale;

public class SourcesComponents {

    public static class MultipliedXp extends AbstractComponent implements ComponentProvider {

        public MultipliedXp(AuraSkills plugin) {
            super(plugin);
        }

        @Override
        public <T> String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, ComponentData componentData, T context) {
            if (placeholder.equals("source_xp")) {
                Locale locale = plugin.getUser(player).getLocale();
                XpSource source = (XpSource) context;

                Skill skill = (Skill) activeMenu.getProperty("skill");
                double multiplier = getMultiplier(player, skill);

                String unitName = source.getUnitName(locale);
                if (unitName == null) {
                    return TextUtil.replace(activeMenu.getFormat("source_xp"),
                            "{xp}", NumberUtil.format2(source.getXp() * multiplier));
                } else {
                    return TextUtil.replace(activeMenu.getFormat("source_xp_rate"),
                            "{xp}", NumberUtil.format2(source.getXp() * multiplier),
                            "{unit}", unitName);
                }
            }
            return replaceMenuMessage(placeholder, player, activeMenu);
        }

        @Override
        public <T> boolean shouldShow(Player player, ActiveMenu activeMenu, T context) {
            return getMultiplier(player, (Skill) activeMenu.getProperty("skill")) > 1.0;
        }

        private double getMultiplier(Player player, Skill skill) {
            User user = plugin.getUser(player);
            double permissionMultiplier = 1 + plugin.getLevelManager().getPermissionMultiplier(user, skill);
            return plugin.getLevelManager().getAbilityMultiplier(user, skill) * permissionMultiplier;
        }

    }

}
