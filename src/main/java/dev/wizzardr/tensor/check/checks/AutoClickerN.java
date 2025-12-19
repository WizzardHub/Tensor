package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.CheckCategory;
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
public class AutoClickerN extends SwingCheck {

    public AutoClickerN(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker N")
                .withCategory(CheckCategory.CLICK_PATTERN)
                .asDeltaCheck()
                .markAsExperimental()
                .withSize(100)
                .build());
    }

    ArrayDeque<Double> kts = new ArrayDeque<>();

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double kurtosis = Statistics.getKurtosis(sample);

        if (cps > 8 && kts.add(kurtosis) && kts.size() == 10) {

            double kurtosisSTD = Statistics.getStDev(kts);
            double kurtosisOSC = Statistics.getOscillation(kts);

            ArrayDeque<Integer> raw = getSample(500);

            long outliers = Statistics.getOutliers(raw).size();
            long doubleClicks = Statistics.getDoubleClicks(raw);

            int[] distribution = Statistics.getDistribution(raw);

            double bds = Statistics.getBDS(raw);

            boolean correctSTD = kurtosisSTD > 2.0 && kurtosisSTD < 10.0;
            boolean correctDistribution = distribution[1] > 250
                    && (double) distribution[1] / distribution[2] > 1.5
                    && distribution[3] + outliers > 20;

            if (doubleClicks < 30 && outliers < 30 && kurtosisOSC > 5.0 && bds < 425 && correctSTD && correctDistribution) {
                threshold();

                DebugContainer data = DebugContainer.builder()
                        .formatString("cps: %.2f, oscillation: %.2f, stDev: %.2f, bds: %.2f, distribution: %s, threshold: %.2f")
                        .values(cps, kurtosisOSC, kurtosisSTD, bds, Arrays.toString(distribution), this.threshold)
                        .build();

                if (this.threshold > 1.5) {
                    alert(data);
                }
            } else {
                threshold(-0.5);
            }

            remove(kts, 5);
        }

        remove(50);
    }
}