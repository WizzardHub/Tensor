package dev.wizzardr.tensor.service;

import dev.wizzardr.tensor.Tensor;
import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.data.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

@Builder
@AllArgsConstructor
public class RecordService {

    private PlayerData playerData;

    public void handleRecordSave() {
        Path replayPath = Path.of(
                TensorAPI.INSTANCE.getPlugin().getDataFolder().toString(),
                "replays");

        try {
            Files.createDirectories(replayPath);
            String recordName = playerData.getRecordData().getName();
            Path filePath = replayPath.resolve(recordName + ".txt");

            var linesToWrite = playerData.getRecordSample().stream()
                    .map(String::valueOf)
                    .toList();

            Files.write(filePath, linesToWrite, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            long totalLineCount;
            try (Stream<String> lines = Files.lines(filePath)) {
                totalLineCount = lines.count();
            }

            TensorAPI.INSTANCE.getPlugin().getServer().getOnlinePlayers().stream()
                    .filter(p -> p.hasPermission("tensor.record.status"))
                    .forEach(player ->
                            player.sendMessage(String.format("%s%s%s%s is being recorded for %s%s%s (%s)",
                                    Tensor.PREFIX, ChatColor.WHITE, player.getName(), ChatColor.GRAY,
                                    ChatColor.YELLOW, recordName, ChatColor.GRAY, totalLineCount)));
        } catch (IOException e) {
            Bukkit.getLogger().severe("Error writing to file: " + e.getMessage());
        }
    }
}
