package dev.wizzardr.tensor.command;

import co.aikar.commands.annotation.*;
import dev.wizzardr.tensor.Tensor;
import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.model.TensorRecordData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Stream;

@CommandAlias("tensor")
@CommandPermission("tensor.command")
public class TensorCommand extends TensorBaseCommand {

    public static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

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
    public void onDebug(CommandSender sender, String check, @Optional String target) {
        if (target == null) {
            target = "*";
        }

        PlayerData playerData = getPlayerData(sender);
        Map<String, List<String>> debugsMap = playerData.getDebugs();

        List<String> checks = debugsMap.computeIfAbsent(target, k -> new ArrayList<>());

        if (checks.contains(check)) {
            sender.sendMessage(String.format("%s%sStopped%s debugging %s%s %s(%s)",
                    Tensor.PREFIX, ChatColor.RED, ChatColor.GRAY, ChatColor.WHITE,
                    check, ChatColor.GRAY, target));
            checks.remove(check);
        } else {
            sender.sendMessage(String.format("%s%sNow%s debugging %s%s %s(%s)",
                    Tensor.PREFIX, ChatColor.GREEN, ChatColor.GRAY, ChatColor.WHITE,
                    check, ChatColor.GRAY, target));
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
        // set the replay name in order to write it in the logs folder
        playerData.setRecordData(new TensorRecordData(false, replayPath));

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

    @Subcommand("replayall")
    @Description("Replays all recordings in a specified folder and its subfolders.")
    @Syntax("<folderPath>")
    public void onReplayAll(CommandSender sender, String folderPath) {
        Path root = Path.of(TensorAPI.INSTANCE.getPlugin().getDataFolder().toString(), "replays");
        Path path = root.resolve(folderPath);

        if (!Files.exists(path) || !Files.isDirectory(path)) {
            sender.sendMessage(String.format("%s%sReplay folder not found %s%s", Tensor.PREFIX, ChatColor.RED, ChatColor.WHITE, folderPath));
            return;
        }

        EXECUTOR_SERVICE.submit(() -> {
            try (var filesStream = Files.walk(path)) {
                var recordFiles = filesStream
                        .filter(Files::isRegularFile)
                        .filter(file -> file.toString().endsWith(".txt"))
                        .toList();

                if (recordFiles.isEmpty()) {
                    sender.sendMessage(String.format("%s%sNo recordings found in %s%s", Tensor.PREFIX, ChatColor.RED, ChatColor.WHITE, folderPath));
                    return;
                }

                sender.sendMessage(String.format("%sReplaying %s%d %srecordings...", Tensor.PREFIX, ChatColor.YELLOW, recordFiles.size(), ChatColor.GRAY));

                for (var filePath : recordFiles) {
                    String recordName = filePath.getFileName().toString().replace(".txt", "");

                    processReplayInternal(sender, filePath, recordName);
                }

                sender.sendMessage(Tensor.PREFIX + ChatColor.GREEN + "All replays completed!");

            } catch (IOException e) {
                sender.sendMessage(Tensor.PREFIX + ChatColor.RED + "Error: " + e.getMessage());
            }
        });
    }

    private void processReplayInternal(CommandSender sender, Path path, String name) {
        try {
            PlayerData replayData = new PlayerData();
            replayData.setRecordData(new TensorRecordData(false, name));

            List<Integer> clicks;
            try (Stream<String> lines = Files.lines(path)) {
                clicks = lines.map(Integer::parseInt).toList();
            }

            for (Integer clickDelay : clicks) {
                replayData.handleClick(clickDelay);
            }

        } catch (Exception e) {
            sender.sendMessage(Tensor.PREFIX + ChatColor.RED + "Failed to process: " + name);
        }
    }
}