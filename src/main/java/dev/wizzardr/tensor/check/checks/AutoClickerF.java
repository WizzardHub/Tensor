package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;

/*
 * This is a niche check, it flags some auto clickers
 * that have very consistent randomization
 */
public class AutoClickerF extends SwingCheck {

    public AutoClickerF(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker F")
                .withSize(40)
                .clearSamplesWhenFull()
                .build());
    }

    ArrayDeque<Double> skewnesses = new ArrayDeque<>();

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double skewness = Statistics.getSkewness(sample);

        if (cps > 8 && skewnesses.add(skewness) && skewnesses.size() == 10) {

            double averageSK = Statistics.getAverage(skewnesses);
            double varianceSK = Statistics.getVariance(skewnesses);

            DebugContainer data = DebugContainer.builder()
                    .formatString("cps: %.2f, averageSK: %.2f, varianceSK: %.2f, threshold: %.2f")
                    .values(cps, averageSK, varianceSK, threshold)
                    .build();

            if (averageSK < 0 && varianceSK < 0.1) {
                if (threshold(-averageSK) > 1.25) {
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