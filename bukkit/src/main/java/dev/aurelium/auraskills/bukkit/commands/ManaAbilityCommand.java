package dev.aurelium.auraskills.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurelium.auraskills.api.event.mana.ManaAbilityRefreshEvent;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.mana.ManaAbilityData;
import dev.aurelium.auraskills.common.message.MessageBuilder;
import dev.aurelium.auraskills.common.message.type.CommandMessage;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

@CommandAlias("%skills_alias")
@Subcommand("manaability")
public class ManaAbilityCommand extends BaseCommand {

    private final AuraSkills plugin;

    public ManaAbilityCommand(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Subcommand("resetcooldown")
    @CommandCompletion("@players @mana_abilities false|true")
    @CommandPermission("auraskills.command.manaability.resetcooldown")
    @Description("Resets a specific mana ability cooldown for a player with optionally filling up the player mana")
    public void onResetCooldown(CommandSender sender, @Flags("other") Player player, ManaAbility ability, @Default("false") boolean fillMana) {
        User user = plugin.getUser(player);
        Locale locale = user.getLocale();

        if (ability.isEnabled()) {
            ManaAbilityData data = user.getManaAbilityData(ability);

            if (data.getCooldown() > 0 && !data.isActivated()) {
                data.setCooldown(0);
                ManaAbilityRefreshEvent event = new ManaAbilityRefreshEvent(player, user.toApi(), ability);
                Bukkit.getPluginManager().callEvent(event);
            }

            if (fillMana) {
                user.setMana(user.getMaxMana());
            }

            sender.sendMessage(MessageBuilder.create(plugin).locale(locale)
                    .prefix()
                    .message(CommandMessage.MANA_ABILITY_COOLDOWN_RESET, "player", player.getName(), "ability", ability.getDisplayName(locale))
                    .toString());
        } else {
            sender.sendMessage(plugin.getPrefix(locale) + plugin.getMsg(CommandMessage.UNKNOWN_MANA_ABILITY, locale));
        }
    }
}
