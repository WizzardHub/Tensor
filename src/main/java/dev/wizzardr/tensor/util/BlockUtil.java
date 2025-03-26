package dev.wizzardr.tensor.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class BlockUtil {
    public boolean hasTargetedBlock(Player player) {
        return player.getTargetBlock(null, 5).getType().isSolid();
    }
}
