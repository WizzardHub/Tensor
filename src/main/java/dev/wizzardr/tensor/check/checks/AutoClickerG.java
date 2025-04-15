package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;
import java.util.List;

/*
 * Generic, will flag some jitter patterns
 */
public class AutoClickerG extends SwingCheck {

    public AutoClickerG(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker G")
                .withSize(500)
                .asDeltaCheck()
                .markAsExperimental()
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> samples) {

        double cps = getCps();
        double kurtosis = Statistics.getKurtosis(samples);
        double skewness = Statistics.getSkewness(samples);
        double bds = Statistics.getBDS(samples);

        List<Integer> outliers = Statistics.getOutliers(samples);
        int[] distribution = Statistics.getDistribution(samples);

        int outlierCount = outliers.size();

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, kurtosis: %.2f, skewness: %.2f, bds: %.2f")
                .values(cps, kurtosis, skewness, bds)
                .build();

        boolean requiredDistribution = distribution[0] > getSize() / 2.0;
        if (cps > 8 && kurtosis < 0 && skewness > 0 && outlierCount < 20 && bds < 350 && requiredDistribution) {
            alert(data);
        }

        debug(data);
        remove(250);
    }
}