package dev.wizzardr.tensor.service;

import dev.wizzardr.tensor.Tensor;
import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.data.PlayerData;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class ViolationService {

    private static final String VIOLATION_ALERT_FORMAT =
            Tensor.PREFIX +
            ChatColor.GRAY + "%s" +
            ChatColor.WHITE + " is clicking suspiciously " +
            ChatColor.RED + "x%d";

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public void handleViolation(SwingCheck check, DebugContainer data) {
        PlayerData playerData = check.getPlayerData();
        Player bukkitPlayer = playerData.getPlayer();

        String playerName = (bukkitPlayer != null) ? bukkitPlayer.getName() : "Recorder";
        String checkName = check.getName();
        String checkInfo = data.getFormattedOutput();

        int violationLevel = ++playerData.vl;

        if (check.isExperimental()) {
            checkName += "*";
        }

        String alertMessageString = String.format(
                VIOLATION_ALERT_FORMAT,
                playerName,
                violationLevel
        );

        if (bukkitPlayer == null) {
            EXECUTOR_SERVICE.submit(() -> {
                try {
                    Path logFile = Paths.get(TensorAPI.INSTANCE.getPlugin().getDataFolder().toString(),
                            "logs", String.format("%s-%s.txt", playerData.getRecordData().getName(), check.getName().strip()));
                    Files.createDirectories(logFile.getParent());
                    Files.write(logFile,
                            ChatColor.stripColor(data.getFormattedOutputSingle() + "\n").getBytes(),
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                } catch (Exception e) {
                    TensorAPI.INSTANCE.getPlugin()
                            .getLogger()
                            .log(Level.SEVERE, e.toString());
                }
            });
        }

        String hoverTextString = ChatColor.AQUA + checkName + "'s data\n\n" + ChatColor.WHITE + checkInfo;

        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();

        Component mainComponent = serializer.deserialize(alertMessageString);
        Component hoverComponent = serializer.deserialize(hoverTextString);

        Component component = mainComponent.hoverEvent(HoverEvent.showText(hoverComponent));

        Bukkit.getServer().getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("tensor.alerts"))
                .forEach(p -> {
                    Audience playerAudience = TensorAPI.INSTANCE.getBukkitAudiences().player(p);
                    playerAudience.sendMessage(component);
                });
    }


    public void handleDebug(SwingCheck check, DebugContainer data) {

        PlayerData playerData = check.getPlayerData();
        Player bukkitPlayer = playerData.getPlayer();

        String checkInfo = data.getFormattedOutputSingle();
        String checkName = check.getName().replaceAll("\\s","");
        String playerName = (bukkitPlayer != null) ? bukkitPlayer.getName() : "Recorder";

        TensorAPI.INSTANCE.getPlugin().getServer().getOnlinePlayers().stream()
                .map(p -> TensorAPI.INSTANCE.getPlayerDataManager().getPlayerData(p.getUniqueId()))
                .filter(p -> {
                    for (Map.Entry<String, List<String>> entry : p.getDebugs().entrySet()) {
                        String player = entry.getKey();
                        List<String> checks = entry.getValue();
                        return (playerName.equals(player) || player.equals("*")) && checks.contains(checkName);
                    }

                    return false;
                })
                .forEach(p -> p.getPlayer().sendMessage(
                        String.format("%s[%s]%s (%s) %s", ChatColor.RED, checkName,
                                ChatColor.GRAY, playerName, checkInfo)));
    }
}
