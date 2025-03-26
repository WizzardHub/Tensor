package dev.wizzardr.tensor.util;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.google.common.collect.ImmutableSet;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class PacketCollection {

    public final Set<PacketTypeCommon> MOVEMENT_PACKETS = ImmutableSet.of(
            PacketType.Play.Client.PLAYER_FLYING,
            PacketType.Play.Client.PLAYER_POSITION,
            PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION,
            PacketType.Play.Client.PLAYER_ROTATION
    );
}
