package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;
import java.util.Arrays;

/*
 * It is almost impossible for human to consistently click 17+ cps
 * with no double clicks, especially when the clicks are being
 * rounded into ticks
 */
public class AutoClickerP extends SwingCheck {

    public AutoClickerP(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker P")
                .withSize(250)
                .asDeltaCheck()
                .excludeDoubleClicks()
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double stDev = Statistics.getStDev(sample);

        ArrayDeque<Integer> raw = getSample(250);

        double rawBDS = Statistics.getBDS(raw);

        int[] distribution = Statistics.getDistribution(sample);

        int difference = Math.abs(distribution[0] - distribution[1]);
        double deltaSTD = Math.abs(0.5 - stDev);

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, stDev: %.2f, bds: %.2f, difference: %s, distribution: %s")
                .values(cps, stDev, rawBDS, difference, Arrays.toString(distribution))
                .build();

        boolean correctDistribution = Math.max(distribution[0], distribution[1]) > 100
                && difference < 25;

        if (cps > 8 && deltaSTD < 0.05 && correctDistribution) {
            alert(data);
        }

        debug(data);
        remove(100);
    }
}