package dev.wizzardr.tensor.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.wizzardr.tensor.Tensor;
import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CommandAlias("tensor")
@CommandPermission("tensor.command")
public class TensorCommand extends BaseCommand {

    @Default
    @Description("Shows the available Tensor commands.")
    public void onDefault(CommandSender sender) {

        ChatColor aqua = ChatColor.AQUA;
        ChatColor white = ChatColor.WHITE;
        ChatColor gray = ChatColor.GRAY;

        sender.sendMessage(String.format("%sAvailable commands",
                Tensor.PREFIX));

        sender.sendMessage(String.format("%s/tensor alerts%s - Show alert.", aqua, gray));
        sender.sendMessage(String.format("%s/tensor debug %s<check>%s - Allows you to debug a specific check.", aqua, white, gray));
        sender.sendMessage(String.format("%s/tensor record %s<player> <name>%s - Records clicks of a player.", aqua, white, gray));
        sender.sendMessage(String.format("%s/tensor replayAll %s<name>%s - Replay all recordings of a folder.", aqua, white, gray));
        sender.sendMessage(String.format("%s/tensor replay %s<name>%s - Replay a specific recording.", aqua, white, gray));
    }

    @Subcommand("alerts")
    @Description("Sends alert information.")
    public void onAlerts(CommandSender sender) {
        PlayerData playerData = getPlayerData(sender);

        playerData.setAlerts(!playerData.isAlerts());

        sender.sendMessage(String.format("%sAlerts have been %s",
                Tensor.PREFIX, playerData.isAlerts() ?
                        ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
    }

    @Subcommand("debug")
    @Description("Sends debug information for a specific check and player.")
    @CommandCompletion("@checks @players")
    @Syntax("<check> [<player>]")
    public void onDebug(CommandSender sender, String check, Player target) {
        if (target == null) {
            target = (Player) sender;
        }

        PlayerData playerData = getPlayerData(sender);
        Map<Player, List<String>> debugsMap = playerData.getDebugs();

        List<String> checks = debugsMap.computeIfAbsent(target, k -> new ArrayList<>());

        if (checks.contains(check)) {
            sender.sendMessage(String.format("%s%sStopped%s debugging %s%s %s(%s)",
                    Tensor.PREFIX, ChatColor.RED, ChatColor.GRAY, ChatColor.WHITE,
                    check, ChatColor.GRAY, target.getName()));
            checks.remove(check);
        } else {
            sender.sendMessage(String.format("%s%sNow%s debugging %s%s %s(%s)",
                    Tensor.PREFIX, ChatColor.GREEN, ChatColor.GRAY, ChatColor.WHITE,
                    check, ChatColor.GRAY, target.getName()));
            checks.add(check);
        }
    }

    private PlayerData getPlayerData(CommandSender sender) {
        Player player = (Player) sender;
        return TensorAPI.INSTANCE.getPlayerDataManager().getPlayerData(player.getUniqueId());
    }

    private PlayerData getPlayerData(Player player) {
        return TensorAPI.INSTANCE.getPlayerDataManager().getPlayerData(player.getUniqueId());
    }
}
