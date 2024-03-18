package dev.aurelium.auraskills.bukkit.menus.abilities;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class UnlockedAbilityItem extends AbstractAbilityItem {

    public UnlockedAbilityItem(AuraSkills plugin) {
        super(plugin, "unlocked_ability");
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderData data, Ability ability) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        if (placeholder.equals("name")) {
            return ability.getDisplayName(locale);
        }
        return replaceMenuMessage(placeholder, player, menu);
    }

    @Override
    public Set<Ability> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        User user = plugin.getUser(player);
        Set<Ability> unlockedAbilities = new HashSet<>();
        // Add abilities that player has not unlocked yet
        for (Ability ability : skill.getAbilities()) {
            if (user.getAbilityLevel(ability) >= 1) {
                unlockedAbilities.add(ability);
            }
        }
        return unlockedAbilities;
    }
}
