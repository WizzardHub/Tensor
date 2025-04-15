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
* Generic auto clicker check that will flag most jitter patterns
*/
public class AutoClickerE2 extends SwingCheck {

    public AutoClickerE2(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker E2")
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

        List<Integer> n = new ArrayList<>(sample);
        long clickPattern = IntStream.range(0, n.size() - 1)
                .filter(i -> n.get(i) == 0 && n.get(i + 1) > 2)
                .count();

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, kurtosis: %.2f, variation: %.2f, entropy: %.2f, mad: %.2f, pattern: %d, threshold: %.2f")
                .values(cps, kurtosis, variation, entropy, mad, clickPattern, threshold)
                .build();

        boolean madRange = mad > 0.8 && mad < 1.05;
        boolean entropyRange = entropy > 10.5 && entropy < 12.0;
        if (cps > 8 && madRange && entropyRange && kurtosis < 0 && variation > 0.395 && clickPattern <= 1) {
            if (threshold(2.0 + kurtosis) > 3.5) {
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