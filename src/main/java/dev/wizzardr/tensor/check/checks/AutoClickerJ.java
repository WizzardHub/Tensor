package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.CheckCategory;
import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;

/*
* Flags consistent auto-clickers that have an average kurtosis
*/
public class AutoClickerJ extends SwingCheck {

    public AutoClickerJ(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker J")
                .withCategory(CheckCategory.CLICK_PATTERN)
                .withSize(50)
                .clearSamplesWhenFull()
                .build());
    }

    ArrayDeque<Double> deltas = new ArrayDeque<>();
    double entropy, difference;

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double entropy = Statistics.getEntropy(sample);

        /* Removing the last value from the queue */
        sample.removeLast();

        double currentEntropy = Statistics.getEntropy(sample);

        if (cps > 8 && this.entropy != 0) {
            double difference = Math.abs(entropy - this.entropy);

            if (difference != 0) {
                double delta = Math.abs(difference - this.difference);

                if (deltas.add(delta) && deltas.size() == 5) {
                    double stDevD = Statistics.getStDev(deltas);
                    ArrayDeque<Integer> raw = getSample(300);

                    double rawKT = Statistics.getKurtosis(raw);
                    double rawEN = Statistics.getEntropy(raw);

                    /* High entropy and low standard deviation will produce higher threshold */
                    double threshold = (rawEN / (rawEN + 10.0)) * Math.max(0, 1 - (stDevD / 0.20)) * 3.0;

                    boolean kurtosisRange = rawKT > -0.25 && rawKT < 2.0;
                    if (!kurtosisRange) {
                        threshold = 0;
                    }

                    DebugContainer data = DebugContainer.builder()
                            .formatString("cps: %.2f, stDev: %.2f, kurtosis: %.2f, entropy: %.2f, threshold: %.2f")
                            .values(cps, stDevD, rawKT, rawEN, this.threshold)
                            .build();

                    if (threshold(threshold - 0.5) > 3.0) {
                        alert(data);
                    }

                    remove(deltas, 3);
                }
            }

            this.difference = difference;
        }

        this.entropy = currentEntropy;
    }
}