package dev.aurelium.auraskills.bukkit.menus.abilities;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class UnlockedManaAbilityItem extends AbstractManaAbilityItem implements TemplateItemProvider<ManaAbility> {
    
    public UnlockedManaAbilityItem(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu menu, PlaceholderData data, ManaAbility manaAbility) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();
        if (placeholder.equals("name")) {
            return manaAbility.getDisplayName(locale);
        }
        return replaceMenuMessage(placeholder, player, menu);
    }

    @Override
    public Set<ManaAbility> getDefinedContexts(Player player, ActiveMenu activeMenu) {
        Skill skill = (Skill) activeMenu.getProperty("skill");
        User user = plugin.getUser(player);
        Set<ManaAbility> unlockedManaAbilities = new HashSet<>();
        // Add abilities that player has not unlocked yet
        ManaAbility manaAbility = skill.getManaAbility();
        if (manaAbility != null && user.getManaAbilityLevel(manaAbility) >= 1) {
            unlockedManaAbilities.add(manaAbility);
        }
        return unlockedManaAbilities;
    }

}
