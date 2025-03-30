package dev.wizzardr.tensor.command;

import co.aikar.commands.BaseCommand;
import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.data.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class TensorBaseCommand extends BaseCommand {
    PlayerData getPlayerData(CommandSender sender) {
        Player player = (Player) sender;
        return TensorAPI.INSTANCE.getPlayerDataManager().getPlayerData(player.getUniqueId());
    }

    PlayerData getPlayerData(Player player) {
        return TensorAPI.INSTANCE.getPlayerDataManager().getPlayerData(player.getUniqueId());
    }
}
