package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/*
* Specific butterfly like pattern
*/
public class AutoClickerE3 extends SwingCheck {

    public AutoClickerE3(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker E3")
                .markAsExperimental()
                .withSize(250)
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double kurtosis = Statistics.getKurtosis(sample);
        double variation = Statistics.getVariation(sample);
        double entropy = Statistics.getEntropy(sample);
        double mad = Statistics.getMAD(sample);

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, kurtosis: %.2f, variation: %.2f, entropy: %.2f, mad: %.2f, threshold: %.2f")
                .values(cps, kurtosis, variation, entropy, mad, threshold)
                .build();

        boolean variationRange = variation > 0.6 && variation < 0.95;
        if (cps > 8 && mad > 1.35 && entropy < 10.5 && kurtosis < -0.5 && variationRange) {
            if (threshold((mad - 1.35) * 5.0) > 2.0) {
                alert(data);
            }
        }

        // reducing threshold on every iteration
        // avoiding false positives with very long clicking sessions
        multiplyThreshold(0.75);

        debug(data);
        remove(100);
    }
}