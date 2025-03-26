package dev.wizzardr.Tensor.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.UserConnectEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import dev.wizzardr.Tensor.TensorAPI;
import dev.wizzardr.Tensor.data.PlayerData;
import dev.wizzardr.Tensor.data.PlayerDataManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TensorPacketListener implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPlayer() == null)
            return;

        Player player = event.getPlayer();
        PlayerDataManager playerDataManager = TensorAPI.INSTANCE.getPlayerDataManager();

        PlayerData playerData = playerDataManager.getPlayerData(player);

        if (playerData == null)
            return;

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION
                || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION
                || event.getPacketType() == PacketType.Play.Client.PLAYER_ROTATION
                || event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING)
        {
            playerData.handleTick();
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
            WrapperPlayClientPlayerDigging packet = new WrapperPlayClientPlayerDigging(event);
            DiggingAction action = packet.getAction();

            switch (action) {
                case RELEASE_USE_ITEM:
                    playerData.ignoreNextClick = true;
                    break;
                case START_DIGGING:
                    Vector3i blockPosition = packet.getBlockPosition();
                    Block block = player.getWorld().getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
                    playerData.breaking = block.getType().isSolid();
                    if (!block.getType().isSolid()) {
                        playerData.breakTicks = 3;
                    }
                    break;
                case FINISHED_DIGGING:
                    playerData.breakTicks = 0;
                    playerData.breaking = false;
                    break;
                case CANCELLED_DIGGING:
                    playerData.breaking = false;
                    break;
            }
        }
    }

    @Override
    public void onUserConnect(UserConnectEvent event) {
        Player player = (Player) event.getUser();
        TensorAPI.INSTANCE.getPlayerDataManager().add(player);
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        Player player = (Player) event.getUser();
        TensorAPI.INSTANCE.getPlayerDataManager().remove(player);
    }
}
