package dev.wizzardr.tensor.data;

import dev.wizzardr.tensor.Tensor;
import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.check.CheckData;
import dev.wizzardr.tensor.model.TensorRecordData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

public class PlayerData {

    @Getter public final Player player;
    protected final UUID uuid;
    private final CheckData checkData = new CheckData();
    @Getter @Setter private boolean alerts;
    @Getter @Setter TensorRecordData recordData =
            new TensorRecordData(false, null);

    @Getter private final Map<Player, List<String>> debugs = new HashMap<>();

    /*
    * This constructor's only purpose is for the replay system
    */
    public PlayerData() {
        this.player = null;
        this.uuid = null;
        checkData.register(this);
    }

    public PlayerData(final UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        if (player != null) {
            this.alerts = player.hasPermission("tensor.alerts");
        }
        checkData.register(this);
    }

    @Getter private final ArrayDeque<Integer> sample = new ArrayDeque<>(), recordSample = new ArrayDeque<>();

    public int tick, breakTicks, releaseItemTicks;
    public boolean breaking;

    public int vl;

    public void handleClick(int tickDelay) {
        tick = 0;

        /*
        * ignoring the next click delay after using an item,
        * this improves accuracy by ignoring "noise"
        * usually done with block hitting
        */
        if (releaseItemTicks == 1) {
            return;
        }

        if (tickDelay <= 10 && breakTicks > 3) {
            sample.add(tickDelay);

            if (recordData.isStatus()) {
                recordSample.add(tickDelay);
                if (recordSample.size() == 50) {
                    handleRecordSave();
                    recordSample.clear();
                }
            }

            if (sample.size() == 2000) {
                sample.poll();
            }

            checkData.getSwingChecks().forEach(c -> c.handle(tickDelay));
        }
    }

    private void handleRecordSave() {
        Path replayPath = Path.of(
                TensorAPI.INSTANCE.getPlugin().getDataFolder().toString(),
                "replays");

        try {
            Files.createDirectories(replayPath);
            String recordName = recordData.getName();
            Path filePath = replayPath.resolve(recordName + ".txt");

            var linesToWrite = recordSample.stream()
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

    public void handleTick() {
        tick++;
        breakTicks++;
        releaseItemTicks++;
    }

    public boolean isBreaking() {
        return breaking || breakTicks <= 5;
    }
}
