package dev.wizzardr.tensor.listener;

import dev.wizzardr.tensor.TensorAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        TensorAPI.INSTANCE.getPlayerDataManager().add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        TensorAPI.INSTANCE.getPlayerDataManager().add(event.getPlayer().getUniqueId());
    }
}
