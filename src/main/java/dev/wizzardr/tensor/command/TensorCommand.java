package dev.wizzardr.tensor.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import dev.wizzardr.tensor.Tensor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
}
