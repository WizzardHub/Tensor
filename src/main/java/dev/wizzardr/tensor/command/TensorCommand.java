package dev.wizzardr.tensor.command;

import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import dev.wizzardr.tensor.Tensor;
import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.model.TensorRecordData;
import dev.wizzardr.tensor.util.math.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CommandAlias("tensor")
@CommandPermission("tensor.command")
public class TensorCommand extends TensorBaseCommand {

    @Default
    @Description("Shows the available Tensor commands.")
    public void onDefault(CommandSender sender) {

        ChatColor aqua = ChatColor.AQUA;
        ChatColor white = ChatColor.WHITE;
        ChatColor gray = ChatColor.GRAY;

        sender.sendMessage(String.format("%sAvailable commands",
                Tensor.PREFIX));

        sender.sendMessage(String.format("%s/tensor alerts%s - Shows alerts.", aqua, gray));
        sender.sendMessage(String.format("%s/tensor debug %s<check> <player>%s - Allows you to debug a specific check.", aqua, white, gray));
        sender.sendMessage(String.format("%s/tensor record %s<player> <name>%s - Records clicks of a player.", aqua, white, gray));
        sender.sendMessage(String.format("%s/tensor recordstats%s - Displays all the recorded datasets (sample size, avg cps).", aqua, gray));
        sender.sendMessage(String.format("%s/tensor replayAll %s<name>%s - Replay all recordings of a path.", aqua, white, gray));
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

    @Subcommand("record")
    @Description("Starts or stops recording for a specific player.")
    @CommandCompletion("@players @nothing")
    @Syntax("<player> [<recordName>]")
    public void onRecord(CommandSender sender, Player targetPlayer, @Optional String[] args) {

        PlayerData targetData = getPlayerData(targetPlayer);
        TensorRecordData currentRecordData = targetData.getRecordData();

        boolean isCurrentlyRecording = currentRecordData != null && currentRecordData.isStatus();
        boolean shouldStartRecording = !isCurrentlyRecording;

        if (shouldStartRecording && args.length == 1) {
            sender.sendMessage(Tensor.PREFIX + ChatColor.RED + "You must provide a name to start recording.");
            return;
        }

        String recordName = args.length == 1 ? currentRecordData.getName() : args[1];

        targetData.setRecordData(new TensorRecordData(shouldStartRecording, shouldStartRecording ? recordName : null)); // Or pass existing name if stopping

        if (shouldStartRecording) {
            sender.sendMessage(String.format("%s%sNow%s recording %s%s %s(%s)",
                    Tensor.PREFIX, ChatColor.GREEN, ChatColor.GRAY, ChatColor.WHITE,
                    targetPlayer.getName(), ChatColor.GRAY, recordName));
        } else {
            sender.sendMessage(String.format("%s%sStopped%s recording %s%s %s(%s)",
                    Tensor.PREFIX, ChatColor.RED, ChatColor.GRAY, ChatColor.WHITE,
                    targetPlayer.getName(), ChatColor.GRAY, recordName));
        }
    }

    @Subcommand("replay")
    @Description("Replays a specific recording by name.")
    @Syntax("<replayPath>")
    public void onReplay(CommandSender sender, String replayPath) {
        Path root = Path.of(TensorAPI.INSTANCE.getPlugin().getDataFolder().toString(),
                "replays");

        Path path = root.resolve(replayPath + ".txt");

        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            sender.sendMessage(String.format("%s%sReplay file not found %s%s", Tensor.PREFIX, ChatColor.RED, ChatColor.WHITE, replayPath));
            return;
        }

        PlayerData playerData = new PlayerData();

        try {
            List<Integer> clicks;
            try (Stream<String> lines = Files.lines(path)) {
                clicks = lines.map(Integer::parseInt).toList();
            }

            clicks.forEach(playerData::handleClick);

            sender.sendMessage(String.format("%sReplay of %s%s%s clicks is done !", Tensor.PREFIX, ChatColor.YELLOW, clicks.size(), ChatColor.GRAY));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subcommand("recordstats")
    @Description("Displays a list of all recorded CPS data with colorful formatting.")
    public void onRecordStats(CommandSender sender) {
        var pluginDataFolder = TensorAPI.INSTANCE.getPlugin().getDataFolder().toString();
        var replayPath = Path.of(pluginDataFolder, "replays");

        if (!Files.exists(replayPath)) {
            sender.sendMessage(Tensor.PREFIX + ChatColor.RED + "No recordings found.");
            return;
        }

        try (var filesStream = Files.list(replayPath)) {
            var recordFiles = filesStream
                    .filter(path -> path.toString().endsWith(".txt"))
                    .toList();

            if (recordFiles.isEmpty()) {
                sender.sendMessage(Tensor.PREFIX + ChatColor.RED + "No recordings found.");
                return;
            }

            // Send header once
            sender.sendMessage(String.format("%s%sRecord Statistics:", Tensor.PREFIX, ChatColor.GRAY));

            for (var filePath : recordFiles) {
                var fileName = filePath.getFileName().toString();
                var recordName = fileName.replaceFirst("\\.txt$", "");

                try {
                    var tickDelays = MathUtil.readTickDelays(filePath);
                    if (tickDelays.isEmpty()) continue;

                    var averageTickDelay = tickDelays.stream()
                            .mapToInt(Integer::intValue)
                            .average()
                            .orElse(0.0);
                    var averageCps = averageTickDelay > 0 ? 20.0 / averageTickDelay : 0;

                    sender.sendMessage(String.format(
                            "- %s%s %s(%s%d %ssamples, %s%.2f %scps)",
                            ChatColor.YELLOW, recordName,
                            ChatColor.GRAY, ChatColor.GREEN, tickDelays.size(), ChatColor.GRAY,
                            ChatColor.YELLOW, averageCps, ChatColor.GRAY));
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Error processing file " + recordName + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            sender.sendMessage(Tensor.PREFIX + ChatColor.RED + "Error reading records: " + e.getMessage());
            Bukkit.getLogger().severe("Error listing record files: " + e.getMessage());
        }
    }
}