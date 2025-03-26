package dev.wizzardr.Tensor;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import dev.wizzardr.Tensor.check.CheckManager;
import dev.wizzardr.Tensor.data.PlayerDataManager;
import dev.wizzardr.Tensor.listener.TensorPacketListener;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public enum TensorAPI {
    INSTANCE;

    private JavaPlugin plugin;
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

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().getSettings().checkForUpdates(false);

        PacketEvents.getAPI().getEventManager().registerListener(new TensorPacketListener(),
                PacketListenerPriority.NORMAL);

        PacketEvents.getAPI().load();
        PacketEvents.getAPI().init();

        /* Add back players if reload */
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (!playerDataManager.has(player)) {
                playerDataManager.add(player);
            }
        });
    }
}