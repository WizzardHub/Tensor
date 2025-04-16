package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;

/*
 * Flags specific constant jitter patterns
 */
public class AutoClickerK extends SwingCheck {

    public AutoClickerK(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker K")
                .withSize(20)
                .clearSamplesWhenFull()
                .excludeDoubleClicks()
                .build());
    }

    ArrayDeque<Double> skewnesses = new ArrayDeque<>();

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double skewness = Statistics.getSkewness(sample);

        if (cps > 8) {
            skewnesses.add(skewness);
            if (skewnesses.size() == 10) {

                double skewnessAVG = Statistics.getAverage(skewnesses);
                double skewnessSTD = Statistics.getStDev(skewnesses);
                double skewnessRR = Statistics.getRecurrenceRate(skewnesses, 0.325);

                double threshold = ((1 - skewnessRR) - 0.25) * 2.5;

                ArrayDeque<Integer> raw = getSample(200);

                double rawBDS = Statistics.getBDS(raw);

                if (skewnessAVG < 0 && skewnessSTD < 0.6 && skewnessRR < 0.525 && rawBDS < 127.5) {
                    threshold(threshold);

                    DebugContainer data = DebugContainer.builder()
                            .formatString("average: %.2f, stDev: %.2f, rr: %.2f, bds: %.2f, threshold: %.2f")
                            .values(skewnessAVG, skewnessSTD, skewnessRR, rawBDS, this.threshold)
                            .build();

                    if (this.threshold > 2.5) {
                        alert(data);
                    }
                } else {
                    threshold(-0.75);
                }

                remove(skewnesses, 5);
            }
        }
    }
}