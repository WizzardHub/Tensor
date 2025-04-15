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

        this.minVl = -1.0;

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
            } else {
                sample.poll();
            }
        }

        // Decreasing the VL by 1.0 each 5000 clicks
        // decreaseVl(size / 5000.0);

        lastTickDelay = tickDelay;
    }

    /*
    * Returns the raw sample from PlayerData
    * generally used on a check that has delta sample transformation
    */
    protected ArrayDeque<Integer> getSample() {
        return DequeUtil.resize(playerData.getSample(), size);
    }

    protected ArrayDeque<Integer> getSample(int size) {
        return DequeUtil.resize(playerData.getSample(), size);
    }

    protected void alert(DebugContainer data) {

        if (++vl <= 0) {
            return;
        }

        ViolationService violationService = TensorAPI.INSTANCE.getViolationService();
        violationService.handleViolation(this, data);
    }

    protected void debug(DebugContainer data) {
        ViolationService violationService = TensorAPI.INSTANCE.getViolationService();
        violationService.handleDebug(this, data);
    }

    protected void remove(int amount) {
        for (int i = 0; i < amount; i++) {
            sample.poll();
        }
    }

    protected <T extends Number> void remove(ArrayDeque<T> data, int amount) {
        for (int i = 0; i < amount; i++) {
            data.poll();
        }
    }

    protected double getCps() {
        return Statistics.getCps(delta ? DequeUtil.resize(playerData.getSample(), size) : sample);
    }

    protected double threshold(double value) {
        threshold = Math.max(0, threshold + value);
        return threshold;
    }

    protected void multiplyThreshold(double value) {
        threshold = Math.max(0, threshold * value);
    }

    protected double threshold() {
        return threshold(1);
    }

    protected void decreaseVl(double decrease) {
        vl = Math.max(minVl, vl - decrease);
    }

    protected abstract void handle(ArrayDeque<Integer> samples);
}
