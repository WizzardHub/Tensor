package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;
import java.util.Arrays;

/*
* Flags very randomized auto clickers with a specific pattern
*/
public class AutoClickerN2 extends SwingCheck {

    public AutoClickerN2(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker N2")
                .asDeltaCheck()
                .markAsExperimental()
                .clearSamplesWhenFull()
                .withSize(50)
                .build());
    }

    ArrayDeque<Double> kts = new ArrayDeque<>();

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double kurtosis = Statistics.getKurtosis(sample);

        if (cps > 8 && kts.add(kurtosis) && kts.size() == 10) {

            double kurtosisVAR = Statistics.getVariation(kts);
            double kurtosisOSC = Statistics.getOscillation(kts);

            ArrayDeque<Integer> raw = getSample(500);

            long outliers = Statistics.getOutliers(raw).size();
            long doubleClicks = Statistics.getDoubleClicks(raw);

            double skewness = Statistics.getSkewness(raw);
            double recurrenceRate = Statistics.getRecurrenceRate(raw, 1);
            double bds = Statistics.getBDS(raw);

            int[] distribution = Statistics.getDistribution(raw);

            boolean skewnessCorrect = skewness > 1.0 && skewness < 4.5;
            boolean oscillationCorrect = kurtosisOSC > 8.0 && kurtosisOSC < 15.0;
            boolean patternCorrect = outliers + doubleClicks < 30
                    && distribution[2] + distribution[3] > 150
                    && distribution[1] > 150
                    && distribution[2] > 200;

            DebugContainer data = DebugContainer.builder()
                    .formatString("cps: %.2f, variation: %.2f, oscillation: %.2f, recurrenceRate: %.2f, bds: %.2f, skewness: %.2f, distribution: %s")
                    .values(cps, kurtosisVAR, kurtosisOSC, recurrenceRate, bds, skewness, Arrays.toString(distribution))
                    .build();

            if (kurtosisVAR > 1.0 && recurrenceRate > 0.95 && bds < 400 && oscillationCorrect && patternCorrect && skewnessCorrect) {
                alert(data);
            }

            debug(data);
            remove(kts, 5);
        }
    }
}