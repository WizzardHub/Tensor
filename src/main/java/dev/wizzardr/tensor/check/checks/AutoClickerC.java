package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;

/*
 * Generic very consistent, almost no randomization auto clicker.
 */
public class AutoClickerC extends SwingCheck {

    public AutoClickerC(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker C")
                .withSize(100)
                .asDeltaCheck()
                .clearSamplesWhenFull()
                .build());
    }

    ArrayDeque<Double> variations = new ArrayDeque<>();

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double variation = Statistics.getVariation(sample);

        if (variations.add(variation) && variations.size() == 5) {

            double average = Statistics.getAverage(variations);
            double stDev = Statistics.getStDev(variations);

            DebugContainer data = DebugContainer.builder()
                    .formatString("cps: %.2f, average: %.2f, stDev: %s")
                    .values(cps, average, stDev)
                    .build();

            // Magic value used to determine a certain threshold
            double threshold = 0.0175 * (average + 0.15);

            if (cps > 8 && stDev < threshold) {
                if (threshold() > 1.5) {
                    alert(data);
                }
            } else {
                threshold(-0.35);
            }

            variations.poll();
            debug(data);
        }
    }
}