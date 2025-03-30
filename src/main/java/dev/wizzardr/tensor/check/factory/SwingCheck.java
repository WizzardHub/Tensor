package dev.wizzardr.tensor.check.factory;

import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;
import dev.wizzardr.tensor.service.ViolationService;
import dev.wizzardr.tensor.util.DequeUtil;
import lombok.Getter;
import org.atteo.classindex.IndexSubclasses;

import java.util.ArrayDeque;

@IndexSubclasses
public abstract class SwingCheck {

    @Getter protected final PlayerData playerData;

    @Getter protected String name;
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

    protected void alert(DebugContainer data) {
        if (++vl <= 0)
            return;

        ViolationService violationService = TensorAPI.INSTANCE.getViolationService();
        violationService.handleViolation(this, data);
    }

    protected void debug(DebugContainer data) {
        ViolationService violationService = TensorAPI.INSTANCE.getViolationService();
        violationService.handleDebug(this, data);
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
