package dev.wizzardr.tensor.data;

import dev.wizzardr.tensor.check.CheckData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.UUID;

public class PlayerData {

    @Getter public final Player player;
    protected final UUID uuid;
    private final CheckData checkData = new CheckData();
    private boolean alerts, debugs;

    public PlayerData(final UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
        this.debugs = player.hasPermission("tensor.debugs");
        this.alerts = player.hasPermission("tensor.alerts");
        checkData.register(this);
    }

    @Getter private final ArrayDeque<Integer> sample = new ArrayDeque<>();

    public int tick, breakTicks, releaseItemTicks;
    public boolean breaking;

    public void handleClick(int tickDelay) {
        tick = 0;

        /*
        * ignoring the next click delay after using an item,
        * this improves accuracy by ignoring "noise"
        * usually done with block hitting
        */
        if (releaseItemTicks == 1) {
            return;
        }

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
        releaseItemTicks++;
    }

    public boolean isBreaking() {
        return breaking || breakTicks <= 5;
    }
}
