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
public class AutoClickerE extends SwingCheck {

    public AutoClickerE(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker E")
                .withSize(250)
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> samples) {

        double cps = getCps();
        double kurtosis = Statistics.getKurtosis(samples);
        double variation = Statistics.getVariation(samples);
        double entropy = Statistics.getEntropy(samples);
        double mad = Statistics.getMAD(samples);

        List<Integer> n = new ArrayList<>(samples);
        long clickPattern = IntStream.range(0, n.size() - 1)
                .filter(i -> n.get(i) == 0 && n.get(i + 1) <= 1)
                .count();

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, kurtosis: %.2f, variation: %.2f, entropy: %.2f, mad: %.2f, pattern: %d, threshold: %.2f")
                .values(cps, kurtosis, variation, entropy, mad, clickPattern, threshold)
                .build();

        boolean madRange = mad > 0.6 && mad < 1.0;
        if (cps > 8 && madRange && kurtosis < 0 && variation < 0.375 && entropy > 11.0 && clickPattern <= 1) {
            if (threshold(2.0 + kurtosis) > 2.5) {
                alert(data);
            }
        }

        // reducing threshold on every iteration
        // avoiding false positives with very long clicking sessions
        multiplyThreshold(0.8);

        debug(data);
        remove(100);
    }
}