package dev.wizzardr.tensor.check.factory;

import dev.wizzardr.tensor.Tensor;
import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;
import dev.wizzardr.tensor.util.DequeUtil;
import lombok.Getter;
import org.atteo.classindex.IndexSubclasses;
import org.bukkit.ChatColor;

import java.util.ArrayDeque;

import java.lang.String;

@IndexSubclasses
public abstract class SwingCheck {

    protected final PlayerData playerData;

    protected String name;
    protected double vl = -1.0;
    protected double minVl;
    protected double threshold;

    @Getter protected int size;
    @Getter protected boolean experimental, includeDoubleClicks, clearSamples, delta;

    private final ArrayDeque<Integer> sample = new ArrayDeque<>();

    protected SwingCheck(PlayerData playerData, SwingCheckData swingCheckData) {
        this.playerData = playerData;

        this.name = swingCheckData.getName();
        this.size = swingCheckData.getSize();

        /* Optional */
        this.delta = swingCheckData.isDelta();
        this.clearSamples = swingCheckData.isClearSample();
        this.includeDoubleClicks = swingCheckData.isIncludeDoubleClicks();
        this.experimental = swingCheckData.isExperimental();
    }

    private int lastTickDelay = 0;

    public void handle(int tickDelay) {
        if (!includeDoubleClicks && tickDelay == 0)
            return;

        sample.add(delta ? Math.abs(tickDelay - lastTickDelay) : tickDelay);

        if (sample.size() == size) {
            handle(sample);
            if (clearSamples) {
                sample.clear();
            }
        }

        lastTickDelay = tickDelay;
    }

    protected void alert(String data) {

        // todo : hover with data
        // todo : use a dictionary for data or make a class
        if (++vl <= 0)
            return;

        TensorAPI.INSTANCE.getPlugin().getServer().getOnlinePlayers()
                .stream().filter(p -> p.hasPermission("tensor.alerts"))
                .forEach(p -> p.sendMessage(Tensor.PREFIX +
                        String.format("%s%s%s used %s%s %s(%.2f)", ChatColor.GRAY, this.playerData.getPlayer().getName(),
                                ChatColor.WHITE, ChatColor.BLUE, this.name, ChatColor.GRAY, this.vl)));
    }

    protected void debug(String data) {
        TensorAPI.INSTANCE.getPlugin().getServer().getOnlinePlayers()
                .stream().filter(p -> p.hasPermission("tensor.debugs"))
                .forEach(p -> p.sendMessage(ChatColor.RED + String.format("[%s] ", name) + ChatColor.GRAY + data));
    }

    protected double getCps() {
        return Statistics.getCps(delta ? sample : DequeUtil.resize(playerData.getSample(), size));
    }

    protected double threshold(double value) {
        threshold = Math.max(0, threshold + value);
        return threshold;
    }

    protected double threshold() {
        return threshold(1);
    }

    protected void decreaseVl(double decrease) {
        vl = Math.max(minVl, vl - decrease);
    }

    protected abstract void handle(ArrayDeque<Integer> samples);
}
