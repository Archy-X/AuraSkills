package dev.aurelium.auraskills.bukkit.menus;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.ability.AbstractAbility;
import dev.aurelium.auraskills.api.mana.ManaAbilities;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.GlobalItems;
import dev.aurelium.auraskills.common.ability.AbilityUtil;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.DataUtil;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import dev.aurelium.slate.builder.MenuBuilder;
import dev.aurelium.slate.function.TemplateSlot;
import dev.aurelium.slate.inv.content.SlotPos;

import java.util.*;

public class AbilitiesMenu {

    private final AuraSkills plugin;

    public AbilitiesMenu(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void build(MenuBuilder menu) {
        var globalItems = new GlobalItems(plugin);
        menu.item("back", globalItems::backToLevelProgression);
        menu.fillItem(globalItems::fill);

        menu.replaceTitle("skill", p -> ((Skill) p.menu().getProperty("skill")).getDisplayName(p.locale(), false));

        menu.properties(m -> Map.of(
                "skill", m.menu().getProperty("skill", Skills.FARMING),
                "previous_menu", "level_progression"));

        menu.template("locked_ability", Ability.class, template -> {
            template.replace("name", p -> p.value().getDisplayName(p.locale(), false));
            template.replace("desc", p -> TextUtil.replace(plugin.getAbilityManager().getBaseDescription(p.value(), plugin.getUser(p.player()), false),
                    "{value}", NumberUtil.format1(p.value().getValue(1)),
                    "{value_2}", NumberUtil.format1(p.value().getSecondaryValue(1)),
                    "{chance_value}", plugin.getAbilityManager().getChanceValue(p.value(), 1),
                    "{guaranteed_value}", plugin.getAbilityManager().getGuaranteedValue(p.value(), 1)));
            template.replace("skill", p -> ((Skill) p.menu().getProperty("skill")).getDisplayName(p.locale(), false));
            template.replace("level", p -> RomanNumber.toRoman(p.value().getUnlock(), plugin));

            template.definedContexts(m -> {
                Skill skill = (Skill) m.menu().getProperty("skill");
                User user = plugin.getUser(m.player());
                Set<Ability> lockedAbilities = new HashSet<>();
                // Add abilities that player has not unlocked yet
                for (Ability ability : skill.getAbilities()) {
                    if (user.getAbilityLevel(ability) <= 0 && ability.isEnabled()) {
                        lockedAbilities.add(ability);
                    }
                }
                return lockedAbilities;
            });

            template.slotPos(abilitySlotPos("locked_ability"));
        });

        menu.template("unlocked_ability", Ability.class, template -> {
            template.replace("name", p -> p.value().getDisplayName(p.locale(), false));

            template.definedContexts(m -> {
                Skill skill = (Skill) m.menu().getProperty("skill");
                User user = plugin.getUser(m.player());
                Set<Ability> unlockedAbilities = new HashSet<>();
                // Add abilities that player has not unlocked yet
                for (Ability ability : skill.getAbilities()) {
                    if (user.getAbilityLevel(ability) >= 1 && ability.isEnabled()) {
                        unlockedAbilities.add(ability);
                    }
                }
                return unlockedAbilities;
            });

            template.slotPos(abilitySlotPos("unlocked_ability"));
        });

        menu.template("locked_mana_ability", ManaAbility.class, template -> {
            template.replace("name", p -> p.value().getDisplayName(p.locale(), false));
            template.replace("desc", p -> TextUtil.replace(plugin.getManaAbilityManager().getBaseDescription(p.value(), plugin.getUser(p.player()), false),
                    "{value}", NumberUtil.format1(p.value().getDisplayValue(1)),
                    "{duration}", NumberUtil.format1(getDuration(p.value()))));
            template.replace("skill", p -> ((Skill) p.menu().getProperty("skill")).getDisplayName(p.locale(), false));
            template.replace("level", p -> RomanNumber.toRoman(p.value().getUnlock(), plugin));

            template.definedContexts(m -> {
                Skill skill = (Skill) m.menu().getProperty("skill");
                ManaAbility manaAbility = skill.getManaAbility();

                Set<ManaAbility> locked = new HashSet<>();
                if (manaAbility != null && manaAbility.isEnabled() && plugin.getUser(m.player()).getManaAbilityLevel(manaAbility) <= 0) {
                    locked.add(manaAbility);
                }
                return locked;
            });

            template.modify(t -> t.value().isEnabled() ? t.item() : null); // Hide item when disabled
        });

        menu.template("unlocked_mana_ability", ManaAbility.class, template -> {
            template.replace("name", p -> p.value().getDisplayName(p.locale(), false));

            template.definedContexts(m -> {
                Skill skill = (Skill) m.menu().getProperty("skill");
                User user = plugin.getUser(m.player());
                Set<ManaAbility> unlocked = new HashSet<>();
                // Add abilities that player has not unlocked yet
                ManaAbility manaAbility = skill.getManaAbility();
                if (manaAbility != null && manaAbility.isEnabled() && user.getManaAbilityLevel(manaAbility) >= 1) {
                    unlocked.add(manaAbility);
                }
                return unlocked;
            });

            template.modify(t -> t.value().isEnabled() ? t.item() : null); // Hide item when disabled
        });

        menu.component("your_level", AbstractAbility.class, component -> {
            component.replace("level", p -> String.valueOf(plugin.getUser(p.player()).getAbstractAbilityLevel(p.value())));
            component.shouldShow(t -> isNotMaxed(plugin.getUser(t.player()), t.value()));
        });

        menu.component("your_level_maxed", AbstractAbility.class, component -> {
            component.replace("level", p -> String.valueOf(plugin.getUser(p.player()).getAbstractAbilityLevel(p.value())));
            component.shouldShow(t -> !isNotMaxed(plugin.getUser(t.player()), t.value()));
        });

        menu.component("unlocked_desc", AbstractAbility.class, component -> {
            component.replace("desc", p -> {
                User user = plugin.getUser(p.player());
                String format = p.menu().getFormat("desc_upgrade_value");
                if (p.value() instanceof Ability ability) {
                    int level = user.getAbilityLevel(ability);
                    String desc = plugin.getAbilityManager().getBaseDescription(ability, user, false);
                    return TextUtil.replace(desc,
                            "{value}", AbilityUtil.getUpgradeValue(ability, level, format),
                            "{value_2}", AbilityUtil.getUpgradeValue2(ability, level, format),
                            "{chance_value}", plugin.getAbilityManager().getChanceValue(ability, level),
                            "{guaranteed_value}", plugin.getAbilityManager().getGuaranteedValue(ability, level));
                } else if (p.value() instanceof ManaAbility manaAbility) {
                    int level = user.getManaAbilityLevel(manaAbility);
                    String desc = plugin.getManaAbilityManager().getBaseDescription(manaAbility, user, false);
                    return TextUtil.replace(desc,
                            "{value}", AbilityUtil.getUpgradeValue(manaAbility, level, format),
                            "{duration}", AbilityUtil.getUpgradeDuration(manaAbility, level, format));
                }
                return null;
            });
            component.replace("skill", p -> p.value().getSkill().getDisplayName(p.locale(), false));
            component.replace("level", p -> RomanNumber.toRoman(getNextUpgradeLevel(p.value(), plugin.getUser(p.player())), plugin));

            component.shouldShow(t -> isNotMaxed(plugin.getUser(t.player()), t.value()));
        });

        menu.component("unlocked_desc_maxed", AbstractAbility.class, component -> {
            component.replace("desc", p -> {
                User user = plugin.getUser(p.player());
                if (p.value() instanceof Ability ability) {
                    String desc = plugin.getAbilityManager().getBaseDescription(ability, user, false);
                    return TextUtil.replace(desc,
                            "{value}", NumberUtil.format1(ability.getValue(user.getAbilityLevel(ability))),
                            "{value_2}", NumberUtil.format1(ability.getSecondaryValue(user.getAbilityLevel(ability))));
                } else if (p.value() instanceof ManaAbility manaAbility) {
                    String desc = plugin.getManaAbilityManager().getBaseDescription(manaAbility, user, false);
                    return TextUtil.replace(desc,
                            "{value}", NumberUtil.format1(manaAbility.getDisplayValue(user.getManaAbilityLevel(manaAbility))),
                            "{duration}", NumberUtil.format1(AbilityUtil.getDuration(manaAbility, user.getManaAbilityLevel(manaAbility))));
                }
                return null;
            });

            component.shouldShow(t -> !isNotMaxed(plugin.getUser(t.player()), t.value()));
        });
    }

    private int getNextUpgradeLevel(AbstractAbility ability, User user) {
        int unlock = ability.getUnlock();
        int levelUp = ability.getLevelUp();
        return unlock + levelUp * user.getAbstractAbilityLevel(ability);
    }

    private boolean isNotMaxed(User user, AbstractAbility ability) {
        int maxLevel = ability.getMaxLevel();
        int unlock = ability.getUnlock();
        int levelUp = ability.getLevelUp();
        int maxAllowedBySkill = (ability.getSkill().getMaxLevel() - unlock) / levelUp + 1;
        if (maxLevel == 0 || maxLevel > maxAllowedBySkill) {
            maxLevel = maxAllowedBySkill;
        }
        return user.getAbstractAbilityLevel(ability) < maxLevel;
    }

    private double getDuration(ManaAbility manaAbility) {
        if (manaAbility == ManaAbilities.LIGHTNING_BLADE) {
            return ManaAbilities.LIGHTNING_BLADE.optionDouble("base_duration", 5.0);
        } else {
            return manaAbility.getValue(1);
        }
    }

    private TemplateSlot<Ability> abilitySlotPos(String itemName) {
        return t -> {
            Skill skill = (Skill) t.menu().getProperty("skill");
            Object obj = t.menu().getItemOption(itemName, "slots");
            if (obj instanceof List<?>) {
                List<String> slots = DataUtil.castStringList(obj);
                // Get the index of the ability
                int index = 0;
                for (Ability skillAbility : skill.getAbilities()) {
                    if (skillAbility.isEnabled() && skill.isEnabled()) {
                        if (skillAbility.equals(t.value())) {
                            break;
                        }
                        index++;
                    }
                }
                if (slots.size() > index) {
                    String slot = slots.get(index);
                    return parseSlot(slot);
                }
            }
            // Default slots
            List<Ability> abilityList = new ArrayList<>();
            for (Ability skillAbility : skill.getAbilities()) {
                if (skillAbility.isEnabled() && skill.isEnabled()) {
                    abilityList.add(skillAbility);
                }
            }
            int index = abilityList.indexOf(t.value());
            return SlotPos.of(1, 2 + index);
        };
    }

    private SlotPos parseSlot(String slotString) {
        String[] split = slotString.split(",", 2);
        if (split.length == 2) {
            return SlotPos.of(NumberUtil.toInt(split[0]), NumberUtil.toInt(split[1]));
        } else {
            int num = NumberUtil.toInt(split[0]);
            int row = num / 9;
            int column = num % 9;
            return SlotPos.of(row, column);
        }
    }

}
