package dev.wizzardr.Tensor.data;

import dev.wizzardr.Tensor.check.CheckData;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;

public class PlayerData {

    private final Player player;
    private final CheckData checkData = new CheckData();
    private final boolean alerts, debugs;

    public PlayerData(final Player player) {
        this.player = player;
        this.alerts = player.hasPermission("tensor.alerts");
        this.debugs = player.hasPermission("tensor.debugs");
        checkData.register(this);
    }

    @Getter private final ArrayDeque<Integer> sample = new ArrayDeque<>();
    public int tick, breakTicks;
    public boolean breaking, ignoreNextClick;

    public void handleClick(int tickDelay) {
        if (tickDelay <= 10 && breakTicks > 3) {
            sample.add(tickDelay);

            if (sample.size() == 2000) {
                sample.poll();
            }

            checkData.getSwingChecks().forEach(c -> c.handle(tickDelay));
        }
    }

    public void handleTick() {
        tick++;
        breakTicks++;
        ignoreNextClick = false;
    }
}
