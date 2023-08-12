package dev.aurelium.auraskills.bukkit.menus.stats;

import com.archyx.slate.item.provider.PlaceholderData;
import com.archyx.slate.item.provider.TemplateItemProvider;
import com.archyx.slate.menu.ActiveMenu;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.common.AbstractItem;
import dev.aurelium.auraskills.bukkit.trait.TraitImpl;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
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
        return new HashSet<>(plugin.getStatManager().getStatValues());
    }

    @Override
    public String onPlaceholderReplace(String placeholder, Player player, ActiveMenu activeMenu, PlaceholderData data, Stat stat) {
        Locale locale = plugin.getUser(player).getLocale();
        User user = plugin.getUser(player);
        switch (placeholder) {
            case "color":
                return stat.getColor(locale);
            case "stat":
                return stat.getDisplayName(locale);
            case "stat_desc":
                return stat.getDescription(locale);
            case "skills":
                return getSkillsLeveledBy(stat, locale);
            case "your_level":
                return TextUtil.replace(plugin.getMsg(MenuMessage.YOUR_LEVEL, locale),
                        "{color}", stat.getColor(locale),
                        "{level}", NumberUtil.format1(user.getStatLevel(stat)));
            case "descriptors":
                return getStatDescriptors(stat, user, locale);
        }
        return placeholder;
    }

    private String getSkillsLeveledBy(Stat stat, Locale locale) {
        List<Skill> skillsLeveledBy = plugin.getRewardManager().getSkillsLeveledBy(stat);
        StringBuilder skillList = new StringBuilder();
        for (Skill skill : skillsLeveledBy) {
            skillList.append(skill.getDisplayName(locale)).append(", ");
        }
        if (skillList.length() > 1) {
            skillList.delete(skillList.length() - 2, skillList.length());
        }
        if (skillsLeveledBy.size() > 0) {
            return TextUtil.replace(plugin.getMsg(MenuMessage.SKILLS, locale),
                    "{skills}", skillList.toString());
        } else {
            return "";
        }
    }

    public String getStatDescriptors(Stat stat, User user, Locale locale) {
        StringBuilder sb = new StringBuilder();
        for (Trait trait : stat.getTraits()) {
            TraitImpl impl = plugin.getTraitManager().getTraitImpl(trait);
            if (impl == null) continue;

            sb.append(stat.getColor(locale))
                    .append(trait.getDisplayName(locale))
                    .append(": ")
                    .append(impl.getMenuDisplay(user.getEffectiveTraitLevel(trait), trait))
                    .append("\n");
        }
        if (!sb.isEmpty()) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }

}
