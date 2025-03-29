package dev.wizzardr.tensor;

import co.aikar.commands.PaperCommandManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import dev.wizzardr.tensor.check.CheckManager;
import dev.wizzardr.tensor.check.violation.ViolationService;
import dev.wizzardr.tensor.command.TensorCommand;
import dev.wizzardr.tensor.data.PlayerDataManager;
import dev.wizzardr.tensor.listener.PlayerListener;
import dev.wizzardr.tensor.listener.TensorPacketListener;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public enum TensorAPI {
    INSTANCE;

    private JavaPlugin plugin;
    private final ViolationService violationService = new ViolationService();
    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final CheckManager checkManager = new CheckManager();

    public void onDisable(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.playerDataManager.dispose();

        PacketEvents.getAPI().getEventManager().unregisterAllListeners();
        PacketEvents.getAPI().terminate();
    }

    public void onEnable(final JavaPlugin plugin) {
        this.plugin = plugin;
        handleCommandRegistration();

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().getSettings().checkForUpdates(false);

        PacketEvents.getAPI().getEventManager().registerListener(new TensorPacketListener(),
                PacketListenerPriority.NORMAL);

        PacketEvents.getAPI().load();
        PacketEvents.getAPI().init();

        /* Add back players if reload */
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (!playerDataManager.has(player.getUniqueId())) {
                playerDataManager.add(player.getUniqueId());
            }
        });

        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
    }

    private void handleCommandRegistration() {
        PaperCommandManager manager = new PaperCommandManager(this.plugin);
        manager.registerCommand(new TensorCommand());
    }
}