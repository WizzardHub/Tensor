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

    public final Set<PacketTypeCommon> LOOK = ImmutableSet.of(
            PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION,
            PacketType.Play.Client.PLAYER_ROTATION
    );

    public final Set<PacketTypeCommon> POSITION = ImmutableSet.of(
            PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION,
            PacketType.Play.Client.PLAYER_POSITION
    );

    public final Set<PacketTypeCommon> PLACE = ImmutableSet.of(
            PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT
    );

    public final Set<PacketTypeCommon> ATTACK = ImmutableSet.of(
            PacketType.Play.Client.INTERACT_ENTITY
    );
}
