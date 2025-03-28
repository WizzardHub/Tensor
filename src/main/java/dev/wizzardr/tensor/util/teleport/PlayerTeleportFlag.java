package dev.wizzardr.tensor.util.teleport;

import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public enum PlayerTeleportFlag {
    X(0x01),
    Y(0x02),
    Z(0x04),
    Y_ROT(0x08),
    X_ROT(0x10);

    private final byte bit;
    
    PlayerTeleportFlag(int bit) {
        this((byte) bit);
    }
    
    
    public static Set<PlayerTeleportFlag> getFlags(byte mask) {
        Set<PlayerTeleportFlag> flags = new HashSet<>();

        for (PlayerTeleportFlag value : PlayerTeleportFlag.values()) {
            if ((mask & value.bit) != 0)
                flags.add(value);
        }

        return flags;
    }

}
