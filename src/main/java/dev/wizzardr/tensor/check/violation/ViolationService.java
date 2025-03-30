package dev.wizzardr.tensor.check.violation;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.wizzardr.tensor.Tensor;
import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.data.PlayerData;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViolationService {

    private static final String VIOLATION_ALERT_FORMAT = Tensor.PREFIX + "%s%s is clicking suspiciously %sx%s";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(
            new ThreadFactoryBuilder()
                    .setPriority(3)
                    .setNameFormat("Tensor Violation Executor Thread")
                    .build()
    );

    public void handleViolation(SwingCheck check, DebugContainer data) {
        executorService.submit(() -> {
            String checkInfo = data.getFormattedOutput();
            String checkName = check.getName();

            PlayerData playerData = check.getPlayerData();

            if (check.isExperimental())
                checkName += "*";

            TextComponent mainComponent = new TextComponent(String.format(VIOLATION_ALERT_FORMAT,
                    playerData.getPlayer().getName(), ChatColor.WHITE, ChatColor.RED, ++playerData.vl));

            TextComponent hoverComponent = new TextComponent(String.format("%s%s's data\n\n%s", ChatColor.BLUE, checkName, checkInfo));
            mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverComponent.getText()).create()));

            ComponentBuilder builder = new ComponentBuilder(mainComponent);

            TensorAPI.INSTANCE.getPlugin().getServer().getOnlinePlayers()
                    .stream().filter(p -> p.hasPermission("tensor.alerts"))
                    .forEach(player -> {
                        player.spigot().sendMessage(builder.create());
                    });
        });
    }

    public void handleDebug(SwingCheck check, DebugContainer data) {
        executorService.submit(() -> {
            String checkInfo = data.getFormattedOutputSingle();
            String checkName = check.getName().replaceAll("\\s","");

            String targetPlayer = check.getPlayerData().getPlayer().getName();

            TensorAPI.INSTANCE.getPlugin().getServer().getOnlinePlayers().stream()
                    .map(p -> TensorAPI.INSTANCE.getPlayerDataManager().getPlayerData(p.getUniqueId()))
                    .filter(playerData -> {
                        for (Map.Entry<Player, List<String>> entry : playerData.getDebugs().entrySet()) {
                            Player player = entry.getKey();
                            List<String> checks = entry.getValue();
                            return targetPlayer.equals(player.getName()) && checks.contains(checkName);
                        }

                        return false;
                    })
                    .forEach(playerData -> playerData.getPlayer().sendMessage(
                            String.format("%s[%s]%s (%s) %s", ChatColor.RED, checkName,
                                    ChatColor.GRAY, targetPlayer, checkInfo)));
        });
    }
}
