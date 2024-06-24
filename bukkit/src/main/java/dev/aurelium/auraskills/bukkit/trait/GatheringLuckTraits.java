package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.AuraSkillsBukkit;
import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.event.loot.LootDropEvent.Cause;
import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.XpSource;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.util.SlateMenuHelper;
import dev.aurelium.auraskills.bukkit.util.ItemUtils;
import dev.aurelium.auraskills.common.message.type.MenuMessage;
import dev.aurelium.auraskills.common.source.SourceTag;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class GatheringLuckTraits extends TraitImpl {

    private final Random random = new Random();
    private final SlateMenuHelper menuHelper;

    GatheringLuckTraits(AuraSkills plugin) {
        super(plugin, Traits.FARMING_LUCK, Traits.FORAGING_LUCK, Traits.MINING_LUCK, Traits.FISHING_LUCK, Traits.EXCAVATION_LUCK);
        this.menuHelper = new SlateMenuHelper(plugin.getSlate());
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    public void apply(Trait trait, Block block, Player player, User user, XpSource source, Set<ItemStack> drops) {
        if (!trait.isEnabled()) return;
        // Get the skill corresponding to the block trait
        Skill skill = getSkill(trait);
        if (skill == null) return;

        // Skip if the applicable tag doesn't have the source
        SourceTag tag = getTag(skill);
        if (tag != null && !plugin.getSkillManager().hasTag(source, tag)) {
            return;
        }

        if (failsChecks(player, skill)) return;

        for (ItemStack item : drops) {
            int numExtra = rollExtraDrops(user, trait);
            if (numExtra == 0) continue;

            ItemStack droppedItem = item.clone();
            droppedItem.setAmount(numExtra);

            Location location = block.getLocation().add(0.5, 0.5, 0.5);

            boolean toInventory = plugin.getLootTableManager().toInventory(player.getInventory().getItemInMainHand());
            Cause cause = getCause(skill);
            LootDropEvent event = new LootDropEvent(player, user.toApi(), droppedItem, location, cause, toInventory);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                ItemUtils.giveBlockLoot(player, event);
            }
        }
    }

    @Override
    protected void reload(Player player, Trait trait) {
        User user = plugin.getUser(player);

        Ability ability = getAbility(trait);
        if (ability == null) return;
        if (!ability.isEnabled()) return;

        String modifierName = trait.name().toLowerCase(Locale.ROOT) + "_ability";
        // Get the luck trait value from the ability value
        double value = ability.getValue(user.getAbilityLevel(ability));

        if (value != 0) {
            user.addTraitModifier(new TraitModifier(modifierName, trait, value), false);
        } else {
            user.removeTraitModifier(modifierName);
        }
    }

    @EventHandler
    public void onLevelUp(SkillLevelUpEvent event) {
        Trait trait = getTrait(event.getSkill());
        if (trait == null) return;

        Player player = event.getPlayer();

        reload(player, trait);
    }

    @Override
    public String getMenuDisplay(double value, Trait trait, Locale locale) {
        int guaranteed = getGuaranteedExtra(value);
        double chance = value - guaranteed * 100;
        String desc;
        if (guaranteed >= 1 && chance > 0) { // ({added}, {chance})
            desc = TextUtil.replace(menuHelper.getFormat("stats", "luck_trait_desc_both"),
                    "{added}", getAddedMsg(guaranteed, locale),
                    "{chance}", getChanceMsg(chance, guaranteed, locale));
        } else if (guaranteed >= 1 && chance == 0) { // ({added})
            desc = TextUtil.replace(menuHelper.getFormat("stats", "luck_trait_desc_added"),
                    "{added}", getAddedMsg(guaranteed, locale));
        } else if (chance > 0) {
            desc = TextUtil.replace(menuHelper.getFormat("stats", "luck_trait_desc_chance"),
                    "{chance}", getChanceMsg(chance, guaranteed, locale));
        } else {
            desc = "";
        }
        return NumberUtil.format1(value) + desc;
    }

    private String getAddedMsg(int guaranteed, Locale locale) {
        MenuMessage key = guaranteed == 1 ? MenuMessage.ADDED_DROP : MenuMessage.ADDED_DROPS;
        return TextUtil.replace(plugin.getMsg(key, locale), "{value}", String.valueOf(guaranteed));
    }

    private String getChanceMsg(double chance, int guaranteed, Locale locale) {
        MenuMessage key = (guaranteed + 1) == 1 ? MenuMessage.CHANCE_DROP : MenuMessage.CHANCE_DROPS;
        return TextUtil.replace(plugin.getMsg(key, locale),
                "{chance}", NumberUtil.format1(chance),
                "{value}", String.valueOf(guaranteed + 1));
    }

    public Set<ItemStack> getUniqueDrops(Block block, Player player) {
        Set<ItemStack> unique = new HashSet<>();
        for (ItemStack item : block.getDrops(player.getInventory().getItemInMainHand(), player)) {
            // Check if a similar item already exists
            boolean alreadyAdded = false;
            for (ItemStack existing : unique) {
                if (existing.isSimilar(item)) {
                    alreadyAdded = true;
                    break;
                }
            }

            if (!alreadyAdded) {
                unique.add(item);
            }
        }
        return unique;
    }

    private boolean failsChecks(Player player, Skill skill) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }
        if (!plugin.getUser(player).hasSkillPermission(skill)) {
            return true;
        }
        return AuraSkillsBukkit.get().getLocationManager().isPluginDisabled(player.getLocation(), player);
    }

    private int getGuaranteedExtra(double value) {
        return (int) (value / 100);
    }

    public int rollExtraDrops(User user, Trait trait) {
        double value = user.getEffectiveTraitLevel(trait);
        if (value == 0.0) return 0;
        // Every 100 = extra guaranteed drop
        int guaranteed = getGuaranteedExtra(value);
        double chance = (value - guaranteed * 100) / 100;
        int extra = random.nextDouble() < chance ? 1 : 0;
        return guaranteed + extra;
    }

    @Nullable
    public Skill getSkill(Trait trait) {
        if (trait instanceof Traits defTrait) {
            return switch (defTrait) {
                case FARMING_LUCK -> Skills.FARMING;
                case FORAGING_LUCK -> Skills.FORAGING;
                case MINING_LUCK -> Skills.MINING;
                case EXCAVATION_LUCK -> Skills.EXCAVATION;
                case FISHING_LUCK -> Skills.FISHING;
                default -> null;
            };
        }
        return null;
    }

    @Nullable
    public Trait getTrait(Skill skill) {
        if (skill instanceof Skills skills) {
            return switch (skills) {
                case FARMING -> Traits.FARMING_LUCK;
                case FORAGING -> Traits.FORAGING_LUCK;
                case MINING -> Traits.MINING_LUCK;
                case EXCAVATION -> Traits.EXCAVATION_LUCK;
                case FISHING -> Traits.FISHING_LUCK;
                default -> null;
            };
        }
        return null;
    }

    @Nullable
    public Ability getAbility(Trait trait) {
        if (trait instanceof Traits traits) {
            return switch (traits) {
                case FARMING_LUCK -> Abilities.BOUNTIFUL_HARVEST;
                case FORAGING_LUCK -> Abilities.LUMBERJACK;
                case MINING_LUCK -> Abilities.LUCKY_MINER;
                case EXCAVATION_LUCK -> Abilities.BIGGER_SCOOP;
                case FISHING_LUCK -> Abilities.LUCKY_CATCH;
                default -> null;
            };
        }
        return null;
    }

    @Nullable
    public SourceTag getTag(Skill skill) {
        if (skill instanceof Skills skills) {
            return switch (skills) {
                case FARMING -> SourceTag.FARMING_LUCK_APPLICABLE;
                case FORAGING -> SourceTag.FORAGING_LUCK_APPLICABLE;
                case MINING -> SourceTag.MINING_LUCK_APPLICABLE;
                case EXCAVATION -> SourceTag.EXCAVATION_LUCK_APPLICABLE;
                default -> null;
            };
        }
        return null;
    }

    @NotNull
    private Cause getCause(Skill skill) {
        if (skill instanceof Skills skills) {
            return switch (skills) {
                case FARMING -> Cause.FARMING_LUCK;
                case FORAGING -> Cause.FORAGING_LUCK;
                case MINING -> Cause.MINING_LUCK;
                case FISHING -> Cause.FISHING_LUCK;
                case EXCAVATION -> Cause.EXCAVATION_LUCK;
                default -> Cause.UNKNOWN;
            };
        }
        return Cause.UNKNOWN;
    }

}
