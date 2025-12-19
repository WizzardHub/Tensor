package dev.wizzardr.tensor.service;

import dev.wizzardr.tensor.Tensor;
import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.data.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class ViolationService {

    private static final String ALERTS_PERMISSION = "tensor.alerts";

    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "tensor-io-pool");
        thread.setDaemon(true);
        return thread;
    });

    public void handleViolation(SwingCheck check, DebugContainer data) {
        PlayerData playerData = check.getPlayerData();
        Player bukkitPlayer = playerData.getPlayer();

        String name = (bukkitPlayer != null) ? bukkitPlayer.getName() :
                (playerData.getRecordData().getName() != null ?
                        ChatColor.YELLOW + "[Replay] " + ChatColor.WHITE + playerData.getRecordData().getName() : "Replay");

        int violationLevel = ++playerData.vl;

        if (bukkitPlayer == null) {
            logViolationToFile(playerData, check.getName(), data.getFormattedOutputSingle());
        }

        Component alert = createAlertComponent(name, check, violationLevel, data.getFormattedOutput());

        broadcastToStaff(alert);
    }

    public void handleDebug(SwingCheck check, DebugContainer data) {
        String checkName = check.getName().replace(" ", "");
        String playerName = check.getPlayerData().getPlayer() != null
                ? check.getPlayerData().getPlayer().getName()
                : (check.getPlayerData().getRecordData().getName() != null ? check.getPlayerData().getRecordData().getName() : "Replay");

        String info = data.getFormattedOutputSingle();

        ioExecutor.execute(() -> {
            Component debugMsg = createDebugComponent(checkName, playerName, info);

            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> {
                        PlayerData d = TensorAPI.INSTANCE.getPlayerDataManager().getPlayerData(p.getUniqueId());
                        return d != null && isDebugging(d, playerName, checkName);
                    })
                    .forEach(p -> TensorAPI.INSTANCE.getBukkitAudiences().player(p).sendMessage(debugMsg));
        });
    }

    private Component createAlertComponent(String name, SwingCheck check, int vl, String debug) {
        return Component.text()
                .append(Component.text(Tensor.PREFIX))
                .append(Component.text(name, NamedTextColor.WHITE))
                .append(Component.space())
                .append(Component.text(check.getCategory().getDisplayName(), NamedTextColor.AQUA))
                .append(Component.space())
                .append(Component.text("x" + vl, NamedTextColor.RED))
                .hoverEvent(Component.text(debug, NamedTextColor.WHITE))
                .build();
    }

    private Component createDebugComponent(String check, String player, String info) {
        return Component.text()
                .append(Component.text("[" + check + "]", NamedTextColor.RED))
                .append(Component.space())
                .append(Component.text("(" + player + ")", NamedTextColor.GRAY))
                .append(Component.space())
                .append(Component.text(info))
                .build();
    }

    private void broadcastToStaff(Component message) {
        TensorAPI.INSTANCE.getBukkitAudiences()
                .filter(p -> p.hasPermission(ALERTS_PERMISSION) && p.isOp())
                .sendMessage(message);

    }

    private void logViolationToFile(PlayerData data, String checkName, String content) {
        ioExecutor.execute(() -> {
            try {
                String recordName = data.getRecordData().getName() != null ? data.getRecordData().getName() : "unknown";

                Path file = TensorAPI.INSTANCE.getPlugin().getDataFolder().toPath()
                        .resolve("logs")
                        .resolve(String.format("%s-%s.txt", recordName, checkName.trim()));

                Files.createDirectories(file.getParent());
                Files.writeString(file, ChatColor.stripColor(content) + System.lineSeparator(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            } catch (Exception e) {
                TensorAPI.INSTANCE.getPlugin().getLogger().log(Level.SEVERE, "IO Error writing logs", e);
            }
        });
    }

    private boolean isDebugging(PlayerData data, String target, String check) {
        return data.getDebugs().entrySet().stream()
                .anyMatch(entry -> (entry.getKey().equals(target) || entry.getKey().equals("*"))
                        && entry.getValue().contains(check));
    }
}