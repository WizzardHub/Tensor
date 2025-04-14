package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

public class AutoClickerA extends SwingCheck {

    public AutoClickerA(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker A") // Name your check
                .withSize(20) // The sample size of what your check will have to reach before printing
                .clearSamplesWhenFull() // Clears the samples when the queue is full
                .excludeDoubleClicks() // Voids double-clicks from samples
                .markAsExperimental() // Mark this check as an experimental check ("This still needs tests")
                .build());
    }


    // Below we will display all the statistics that are inside our math class and have a debug pre-set for you.
    @Override
    protected void handle(ArrayDeque<Integer> samples) {
        /* Example

        double cps = getCps();
        double stDev = Statistics.getStDev(samples);
        double modifCount = Statistics.getModifCount(samples);
        double average = Statistics.getAverage(samples);
        double entropy = Statistics.getEntropy(samples);
        double gini = Statistics.getGini(samples);
        double bds = Statistics.getBDS(samples);
        double recurrenceRate = Statistics.getRecurrenceRate(samples);
        double skewness = Statistics.getSkewness(samples);
        double kurtosis = Statistics.getKurtosis(samples);
        double variance = Statistics.getVariance(samples);
        double variation = Statistics.getVariation(samples);

        int[] distribution = Statistics.getDistribution(samples);

        List<Integer> outlier = Statistics.getOutliers(samples);

        debug(DebugContainer.builder()
                .formatString("cps: %.2f, stDev: %.2f, modifCount: %.2f, average: %.2f, entropy: %.2f, gini: %.2f, " +
                        "bds: %.2f, recurrenceRate: %.2f, skewness: %.2f, kurtosis: %.2f, variance: %.2f, " +
                        "variation: %.2f, distribution: %s, outliers: %s")
                .values(cps, stDev, modifCount, average, entropy, gini, bds, recurrenceRate,
                        skewness, kurtosis, variance, variation, Arrays.toString(distribution), outlier.toString())
                .build());
         */
    }
}
