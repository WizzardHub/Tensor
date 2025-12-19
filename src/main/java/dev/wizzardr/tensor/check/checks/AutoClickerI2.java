package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.CheckCategory;
import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;

/*
 * It is almost impossible for human to consistently click 17+ cps
 * with no double clicks, especially when the clicks are being
 * rounded into ticks
 */
public class AutoClickerI2 extends SwingCheck {

    public AutoClickerI2(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker I2")
                .withCategory(CheckCategory.CLICK_PATTERN)
                .withSize(20)
                .clearSamplesWhenFull()
                .excludeDoubleClicks()
                .build());
    }

    ArrayDeque<Double> stDevs = new ArrayDeque<>();
    ArrayDeque<Double> diffs = new ArrayDeque<>();
    double average, delta;

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double stDev = Statistics.getStDev(sample);

        if (cps > 8) {
            stDevs.add(stDev);
            if (stDevs.size() == 10) {

                double averageSTD = Statistics.getAverage(stDevs);
                double delta = Math.abs(averageSTD - this.average);
                this.average = averageSTD;

                double diff = Math.abs(delta - this.delta);
                this.delta = delta;

                if (delta < 0.1) {
                    diffs.add(diff);
                    if (diffs.size() == 5) {

                        double diffsSTD = Statistics.getStDev(diffs);
                        double diffsAVG = Statistics.getAverage(diffs);

                        ArrayDeque<Integer> raw = getSample(300);

                        double rawKT = Statistics.getKurtosis(raw);
                        double rawBDS = Statistics.getBDS(raw);

                        if (diffsSTD < 0.01 && rawBDS < 100.0) {

                            double threshold = threshold(((diffsAVG / 0.05) * (0.01 / diffsSTD)) - 0.25);

                            DebugContainer data = DebugContainer.builder()
                                    .formatString("cps: %.2f, std: %.2f, avg: %.2f, kurtosis: %.2f, bds: %.2f, threshold: %.2f")
                                    .values(cps, diffsSTD, diffsAVG, rawKT, rawBDS, threshold)
                                    .build();

                            if (threshold > 2.0) {
                                alert(data);
                            }
                        } else {
                            threshold(-0.25);
                        }

                        diffs.poll();
                    }
                } else {
                    threshold(-0.25);
                }

                remove(stDevs, 5);
            }
        }
    }
}