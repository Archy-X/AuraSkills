package dev.aurelium.auraskills.bukkit.menus;

import com.google.common.collect.ImmutableList;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.bukkit.menus.shared.SkillItem;
import dev.aurelium.auraskills.bukkit.menus.shared.SkillLevelItem;
import dev.aurelium.auraskills.common.ability.AbilityUtil;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.reward.SkillReward;
import dev.aurelium.auraskills.common.reward.type.MoneyReward;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.action.trigger.ClickTrigger;
import dev.aurelium.slate.builder.BuiltMenu;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.info.ComponentPlaceholderInfo;
import dev.aurelium.slate.info.MenuInfo;
import dev.aurelium.slate.info.TemplatePlaceholderInfo;
import dev.aurelium.slate.menu.LoadedMenu;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LevelProgressionMenu {

    private final AuraSkills plugin;

    public LevelProgressionMenu(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void build(MenuBuilder menu) {
        menu.defaultOptions(Map.of(
                "use_level_as_amount", false,
                "over_max_stack_amount", 1,
                "items_per_page", 24,
                "start_level", 1,
                "track", SkillLevelItem.getDefaultTrack()));

        menu.replace("skill", p -> ((Skill) p.menu().getProperty("skill")).getDisplayName(p.locale(), false));
        menu.replace("skill_key", p -> ((Skill) p.menu().getProperty("skill")).getId().getKey());

        menu.replaceTitle("page", p -> String.valueOf(p.menu().getCurrentPage() + 1));

        menu.properties(m -> {
            int itemsPerPage = 24;
            LoadedMenu loadedMenu = plugin.getSlate().getLoadedMenu("level_progression");
            if (loadedMenu != null) {
                itemsPerPage = (int) loadedMenu.options().getOrDefault("items_per_page", 24);
            }
            return Map.of(
                    "skill", m.menu().getProperty("skill", Skills.FARMING),
                    "items_per_page", itemsPerPage,
                    "previous_menu", "skills");
        });

        menu.pages(m -> {
            var skill = (Skill) m.menu().getProperty("skill");
            int itemsPerPage = 24;
            LoadedMenu levelProgressionMenu = plugin.getSlate().getLoadedMenu("level_progression");
            if (levelProgressionMenu != null) {
                Object itemsPerPageObj = levelProgressionMenu.options().get("items_per_page");
                if (itemsPerPageObj != null) {
                    itemsPerPage = (int) itemsPerPageObj;
                }
            }
            int startLevel = m.menu().getOption(Integer.class, "start_level", 1);
            return (skill.getMaxLevel() - startLevel) / itemsPerPage + 1;
        });

        var globalItems = new GlobalItems(plugin);
        menu.item("back", globalItems::back);
        menu.item("previous_page", globalItems::previousPage);
        menu.item("next_page", globalItems::nextPage);
        menu.item("close", globalItems::close);

        var skillItem = new SkillItem(plugin);
        skillItem.buildComponents(menu);

        menu.item("rank", item -> {
            item.replace("rank", p -> {
                var skill = (Skill) p.menu().getProperty("skill");
                return String.valueOf(getLbRank(skill, p.player()));
            });
            item.replace("total", p -> {
                var skill = (Skill) p.menu().getProperty("skill");
                return String.valueOf(getLbSize(skill));
            });
            item.replace("percent", p -> {
                var skill = (Skill) p.menu().getProperty("skill");
                double percent = getLbPercent(skill, p.player());
                return percent > 1 ? String.valueOf(Math.round(percent)) : NumberUtil.format2(percent);
            });
            item.replace("skill", p -> ((Skill) p.menu().getProperty("skill")).getDisplayName(p.locale(), false));

            item.onClick(c -> {
                var properties = c.menu().getProperties(); // Retain current properties
                properties.put("previous_menu", "level_progression");
                plugin.getSlate().openMenu(c.player(), "leaderboard", properties);
            });
        });

        menu.item("sources", item -> {
            item.onClick(c -> {
                BuiltMenu sourcesMenu = plugin.getSlate().getBuiltMenu("sources");
                MenuInfo info = new MenuInfo(plugin.getSlate(), c.player(), c.menu());
                plugin.getSlate().openMenu(c.player(), "sources", sourcesMenu.propertyProvider().get(info));
            });
        });

        menu.item("abilities", item -> {
            item.onClick(c -> {
                BuiltMenu abilitiesMenu = plugin.getSlate().getBuiltMenu("abilities");
                MenuInfo info = new MenuInfo(plugin.getSlate(), c.player(), c.menu());
                plugin.getSlate().openMenu(c.player(), "abilities", abilitiesMenu.propertyProvider().get(info));
            });

            item.modify(i -> {
                var skill = (Skill) i.menu().getProperty("skill");
                // Only show if the skill has an enabled ability
                boolean hasEnabledAbility = false;
                for (Ability ability : skill.getAbilities()) {
                    if (ability.isEnabled()) {
                        hasEnabledAbility = true;
                        break;
                    }
                }
                return hasEnabledAbility ? i.item() : null;
            });
        });

        menu.item("job", item -> {
            // Hide if jobs are disabled
            item.modify(i -> {
                Skill skill = (Skill) i.menu().getProperty("skill");
                if (plugin.config().jobSelectionEnabled() && plugin.getUser(i.player()).canSelectJob(skill)) {
                    return i.item();
                }
                return null;
            });

            item.onClick(ClickTrigger.LEFT, c -> {
                User user = plugin.getUserManager().getUser(c.player());
                Skill skill = (Skill) c.menu().getProperty("skill");

                if (isOnJobSelectCooldown(user)) return;
                // Select as job
                if (!user.getJobs().contains(skill) && user.getJobs().size() < user.getJobLimit()) {
                    user.addJob(skill);
                    c.menu().reload();
                    c.menu().setCooldown("job", 10);
                }
            });

            item.onClick(ClickTrigger.RIGHT, c -> {
                User user = plugin.getUserManager().getUser(c.player());
                Skill skill = (Skill) c.menu().getProperty("skill");
                // Quit job
                if (user.getJobs().contains(skill)) {
                    user.removeJob(skill);
                    c.menu().reload();
                    c.menu().setCooldown("job", 10);
                }
            });
        });

        menu.component("job_select", null, component -> {
            component.shouldShow(t -> {
                User user = plugin.getUser(t.player());
                Skill skill = (Skill) t.menu().getProperty("skill");
                // User isn't working job and can add this job
                return !user.getJobs().contains(skill) && user.getJobs().size() < user.getJobLimit() && !isOnJobSelectCooldown(user);
            });
        });

        menu.component("job_active", null, component -> component.shouldShow(t -> {
            User user = plugin.getUser(t.player());
            Skill skill = (Skill) t.menu().getProperty("skill");
            // User is working this job
            return user.getJobs().contains(skill);
        }));

        menu.component("job_limit", null, component -> component.shouldShow(t -> {
            User user = plugin.getUser(t.player());
            Skill skill = (Skill) t.menu().getProperty("skill");
            // User isn't working this job and cannot add this job
            return !user.getJobs().contains(skill) && user.getJobs().size() >= user.getJobLimit() && !isOnJobSelectCooldown(user);
        }));

        menu.component("job_cooldown", null, component -> {
            component.replace("time", p -> {
                long lastTime = plugin.getUser(p.player()).getLastJobSelectTime();
                long cooldown = (plugin.configInt(Option.JOBS_SELECTION_COOLDOWN_SEC) * 1000L) - (System.currentTimeMillis() - lastTime);
                Duration duration = Duration.ofMillis(cooldown);

                long days = duration.toDays();
                long hours = duration.toHoursPart();
                long minutes = duration.toMinutesPart();
                long seconds = duration.toSecondsPart();

                if (days > 0) {
                    return String.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds);
                } else {
                    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
                }
            });
            component.shouldShow(t -> {
                User user = plugin.getUser(t.player());
                Skill skill = t.menu().property("skill");
                return !user.getJobs().contains(skill) && isOnJobSelectCooldown(user);
            });
        });

        menu.template("skill", Skill.class, template -> {
            // Sets text replacements and modifier
            skillItem.baseSkillItem(template);

            template.definedContexts(m -> Set.of((Skill) m.menu().getProperty("skill")));
        });

        var levelItem = new SkillLevelItem(plugin);

        menu.onOpen(m -> levelItem.loadTrack()); // Load track option

        menu.template("unlocked", Integer.class, template -> {
            template.replace("level", p -> String.valueOf(p.value()));

            template.definedContexts(m -> {
                int level = plugin.getUser(m.player()).getSkillLevel((Skill) m.menu().getProperty("skill"));
                int itemsPerPage = levelItem.itemsPerPage(m.menu());

                Set<Integer> levels = new HashSet<>();
                for (int i = 0; i < itemsPerPage; i++) {
                    if (levelItem.startLevel(m.menu()) + m.menu().getCurrentPage() * itemsPerPage + i <= level) {
                        levels.add(levelItem.startLevel(m.menu()) + i + m.menu().getCurrentPage() * itemsPerPage);
                    } else {
                        break;
                    }
                }
                return levels;
            });

            template.slotPos(levelItem::slotPos);
            template.modify(levelItem::modify);
        });

        menu.template("in_progress", Integer.class, template -> {
            template.replace("level", p -> String.valueOf(p.value()));
            template.replace("percent", p -> NumberUtil.format2(currentXp(p) / xpToNext(p) * 100));
            template.replace("current_xp", p -> NumberUtil.format2(currentXp(p)));
            template.replace("level_xp", p -> String.valueOf((int) xpToNext(p)));
            template.replace("bar", p -> SkillItem.getBar(plugin, currentXp(p), xpToNext(p)));

            template.definedContexts(m -> {
                int currentPage = m.menu().getCurrentPage();
                int level = plugin.getUser(m.player()).getSkillLevel((Skill) m.menu().getProperty("skill"));
                int offset = levelItem.startLevel(m.menu()) - 1;
                int itemsPerPage = levelItem.itemsPerPage(m.menu());
                if (level >= offset + currentPage * itemsPerPage && level < (currentPage + 1) * itemsPerPage + offset) {
                    return Set.of(level + 1);
                }
                return new HashSet<>();
            });

            template.slotPos(levelItem::slotPos);
            template.modify(levelItem::modify);
        });

        menu.template("locked", Integer.class, template -> {
            template.replace("level", p -> String.valueOf(p.value()));

            template.definedContexts(m -> {
                int level = plugin.getUser(m.player()).getSkillLevel((Skill) m.menu().getProperty("skill"));
                int itemsPerPage = levelItem.itemsPerPage(m.menu());

                Set<Integer> levels = new HashSet<>();
                for (int i = itemsPerPage - 1; i >= 0; i--) {
                    if (levelItem.startLevel(m.menu()) - 1 + m.menu().getCurrentPage() * itemsPerPage + i > level) {
                        levels.add(levelItem.startLevel(m.menu()) + i + m.menu().getCurrentPage() * itemsPerPage);
                    } else {
                        break;
                    }
                }
                return levels;
            });

            template.slotPos(levelItem::slotPos);
            template.modify(levelItem::modify);
        });

        menu.component("rewards", Integer.class, component -> {
            component.replace("entries", this::getRewardEntries);

            component.shouldShow(t -> !getRewardsList((Skill) t.menu().getProperty("skill"), t.value()).isEmpty());
        });

        menu.component("ability_unlock", Integer.class, component -> {
            component.replace("name", p -> {
                Ability ability = unlockedAbility(p);
                return ability.getDisplayName(p.locale(), false);
            });
            component.replace("desc", p -> {
                Ability ability = unlockedAbility(p);
                return TextUtil.replace(plugin.getAbilityManager().getBaseDescription(ability, plugin.getUser(p.player()), false),
                        "{value}", NumberUtil.format1(ability.getValue(1)),
                        "{value_2}", NumberUtil.format1(ability.getSecondaryValue(1)),
                        "{chance_value}", plugin.getAbilityManager().getChanceValue(ability, 1),
                        "{guaranteed_value}", plugin.getAbilityManager().getGuaranteedValue(ability, 1));
            });

            component.shouldShow(t -> !getUnlocked((Skill) t.menu().getProperty("skill"), t.value()).isEmpty());

            component.instances(t -> getUnlocked((Skill) t.menu().getProperty("skill"), t.value()).size());
        });

        menu.component("ability_level", Integer.class, component -> {
            component.replace("name", p -> {
                Ability ability = leveledUpAbility(p);
                return ability.getDisplayName(p.locale(), false);
            });
            component.replace("desc", p -> {
                Ability ability = leveledUpAbility(p);
                int level = ((p.value() - ability.getUnlock()) / ability.getLevelUp()) + 1;
                return TextUtil.replace(plugin.getAbilityManager().getBaseDescription(ability, plugin.getUser(p.player()), false),
                        "{value}", NumberUtil.format1(ability.getValue(level)),
                        "{value_2}", NumberUtil.format1(ability.getSecondaryValue(level)),
                        "{chance_value}", plugin.getAbilityManager().getChanceValue(ability, level),
                        "{guaranteed_value}", plugin.getAbilityManager().getGuaranteedValue(ability, level));
            });
            component.replace("level", p -> {
                Ability ability = leveledUpAbility(p);
                int abilityLevel = ((p.value() - ability.getUnlock()) / ability.getLevelUp()) + 1;
                return RomanNumber.toRoman(abilityLevel, plugin);
            });

            component.shouldShow(t -> !getLeveledUp((Skill) t.menu().getProperty("skill"), t.value()).isEmpty());

            component.instances(t -> getLeveledUp((Skill) t.menu().getProperty("skill"), t.value()).size());
        });

        menu.component("mana_ability_unlock", Integer.class, component -> {
            component.replace("name", p -> {
                ManaAbility manaAbility = ((Skill) p.menu().getProperty("skill")).getManaAbility();
                return manaAbility != null ? manaAbility.getDisplayName(p.locale(), false) : null;
            });
            component.replace("desc", p -> {
                ManaAbility manaAbility = ((Skill) p.menu().getProperty("skill")).getManaAbility();
                if (manaAbility == null) return null;
                return TextUtil.replace(plugin.getManaAbilityManager().getBaseDescription(manaAbility, plugin.getUser(p.player()), false),
                        "{value}", NumberUtil.format1(manaAbility.getDisplayValue(1)),
                        "{duration}", NumberUtil.format1(getDuration(manaAbility, 1)),
                        "{haste_level}", String.valueOf(ManaAbilities.SPEED_MINE.optionInt("haste_level", 10)));
            });

            component.shouldShow(t -> {
                ManaAbility manaAbility = ((Skill) t.menu().getProperty("skill")).getManaAbility();
                if (manaAbility != null && manaAbility.isEnabled()) {
                    return manaAbility.getUnlock() == t.value();
                } else {
                    return false;
                }
            });
        });

        menu.component("mana_ability_level", Integer.class, component -> {
            component.replace("name", p -> {
                ManaAbility manaAbility = ((Skill) p.menu().getProperty("skill")).getManaAbility();
                return manaAbility != null ? manaAbility.getDisplayName(p.locale(), false) : null;
            });
            component.replace("desc", p -> {
                ManaAbility manaAbility = ((Skill) p.menu().getProperty("skill")).getManaAbility();
                if (manaAbility == null) return null;
                int manaAbilityLevel = ((p.value() - manaAbility.getUnlock()) / manaAbility.getLevelUp()) + 1;

                return TextUtil.replace(plugin.getManaAbilityManager().getBaseDescription(manaAbility, plugin.getUser(p.player()), false),
                        "{value}", NumberUtil.format1(manaAbility.getDisplayValue(manaAbilityLevel)),
                        "{duration}", NumberUtil.format1(getDuration(manaAbility, manaAbilityLevel)),
                        "{haste_level}", String.valueOf(ManaAbilities.SPEED_MINE.optionInt("haste_level", 10)));
            });
            component.replace("level", p -> {
                ManaAbility manaAbility = ((Skill) p.menu().getProperty("skill")).getManaAbility();
                if (manaAbility == null) return null;
                int manaAbilityLevel = ((p.value() - manaAbility.getUnlock()) / manaAbility.getLevelUp()) + 1;

                return RomanNumber.toRoman(manaAbilityLevel, plugin);
            });

            component.shouldShow(t -> {
                Skill skill = ((Skill) t.menu().getProperty("skill"));
                ManaAbility manaAbility = skill.getManaAbility();
                if (manaAbility != null && manaAbility.isEnabled()) {
                    return plugin.getManaAbilityManager().getManaAbilityAtLevel(skill, t.value()) != null && manaAbility.getUnlock() != t.value();
                } else {
                    return false;
                }
            });
        });
    }

    private boolean isOnJobSelectCooldown(User user) {
        int selectionCooldown = plugin.configInt(Option.JOBS_SELECTION_COOLDOWN_SEC);
        if (selectionCooldown > 0) { // Cooldown is enabled
            long lastTime = user.getLastJobSelectTime();
            return lastTime + (selectionCooldown * 1000L) > System.currentTimeMillis();
        }
        return false;
    }

    private double getDuration(ManaAbility manaAbility, int level) {
        return AbilityUtil.getDuration(manaAbility, level);
    }

    private Ability leveledUpAbility(ComponentPlaceholderInfo<Integer> p) {
        return getLeveledUp((Skill) p.menu().getProperty("skill"), p.value()).get(p.component().instance());
    }

    private List<Ability> getLeveledUp(Skill skill, int level) {
        return plugin.getAbilityManager().getAbilities(skill, level).stream()
                .filter(a -> a.getUnlock() != level)
                .filter(Ability::isEnabled)
                .toList();
    }

    private Ability unlockedAbility(ComponentPlaceholderInfo<Integer> p) {
        return getUnlocked((Skill) p.menu().getProperty("skill"), p.value()).get(p.component().instance());
    }

    private List<Ability> getUnlocked(Skill skill, int level) {
        return plugin.getAbilityManager().getAbilities(skill, level).stream()
                .filter(a -> a.getUnlock() == level)
                .filter(Ability::isEnabled)
                .toList();
    }

    private ImmutableList<SkillReward> getRewardsList(Skill skill, int level) {
        return plugin.getRewardManager().getRewardTable(skill).getRewards(level);
    }

    private String getRewardEntries(TemplatePlaceholderInfo<Integer> info) {
        var skill = (Skill) info.menu().getProperty("skill");
        int level = info.value();

        ImmutableList<SkillReward> rewards = getRewardsList(skill, level);
        StringBuilder message = new StringBuilder();
        double totalMoney = 0;
        for (SkillReward reward : rewards) {
            message.append(reward.getMenuMessage(plugin.getUser(info.player()), info.locale(), skill, level));
            if (reward instanceof MoneyReward) {
                totalMoney += ((MoneyReward) reward).getAmount(level);
            }
        }
        if (totalMoney > 0) {
            message.append(TextUtil.replace(info.menu().getFormat("money_reward_entry"), "{amount}", NumberUtil.format2(totalMoney)));
        }
        return message.toString();
    }

    private double currentXp(TemplatePlaceholderInfo<Integer> p) {
        var skill = (Skill) p.menu().getProperty("skill");
        return plugin.getUser(p.player()).getSkillXp(skill);
    }

    private double xpToNext(TemplatePlaceholderInfo<Integer> p) {
        var skill = (Skill) p.menu().getProperty("skill");
        return plugin.getXpRequirements().getXpRequired(skill, p.value());
    }

    private double getLbPercent(Skill skill, Player player) {
        int rank = getLbRank(skill, player);
        int size = getLbSize(skill);
        if (size == 0) {
            size = 1;
        }
        return (double) rank / (double) size * 100;
    }

    private int getLbRank(Skill skill, Player player) {
        return plugin.getLeaderboardManager().getSkillRank(skill, player.getUniqueId());
    }

    private int getLbSize(Skill skill) {
        return plugin.getLeaderboardManager().getLeaderboard(skill).size();
    }

}
