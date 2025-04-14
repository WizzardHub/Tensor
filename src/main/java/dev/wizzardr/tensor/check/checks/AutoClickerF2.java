package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;

/*
 * This is a niche check, it flags some auto clickers
 * that have consistent randomization
 */
public class AutoClickerF2 extends SwingCheck {

    public AutoClickerF2(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker F2")
                .withSize(40)
                .clearSamplesWhenFull()
                .markAsExperimental()
                .build());
    }

    ArrayDeque<Double> skewnesses = new ArrayDeque<>();

    @Override
    protected void handle(ArrayDeque<Integer> samples) {

        double cps = getCps();
        double skewness = Statistics.getSkewness(samples);

        if (cps > 8 && skewnesses.add(skewness) && skewnesses.size() == 10) {

            double averageSK = Statistics.getAverage(skewnesses);
            double varianceSK = Statistics.getVariance(skewnesses);

            DebugContainer data = DebugContainer.builder()
                    .formatString("cps: %.2f, averageSK: %.2f, varianceSK: %.2f, threshold: %.2f")
                    .values(cps, averageSK, varianceSK, threshold)
                    .build();

            if (averageSK > 0.5 && varianceSK < 0.05) {
                if (threshold(Math.min(0.5, ((0.05 - varianceSK) * 15.0) - 0.25)) > 1.0) {
                    alert(data);
                }
            } else {
                multiplyThreshold(0.8);
            }

            debug(data);
            skewnesses.clear();
        }
    }
}