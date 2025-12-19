package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.CheckCategory;
import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;

/*
 * Generic very consistent, almost no randomization auto clicker.
 */
public class AutoClickerO extends SwingCheck {

    public AutoClickerO(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker O")
                .withCategory(CheckCategory.CLICK_PATTERN)
                .withSize(60)
                .asDeltaCheck()
                .build());
    }

    ArrayDeque<Double> entropies = new ArrayDeque<>();
    ArrayDeque<Double> sigmas = new ArrayDeque<>();

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double entropy = Statistics.getEntropy(sample);

        if (cps > 8 && entropies.add(entropy) && entropies.size() == 5) {

            double entropySTD = Statistics.getStDev(entropies);

            sigmas.add(entropySTD);
            if (sigmas.size() == 10) {

                double sigmaRR = Statistics.getRecurrenceRate(sigmas, 0.0025);

                DebugContainer data = DebugContainer.builder()
                        .formatString("recurrenceRate: %.2f")
                        .values(sigmaRR)
                        .build();

                if (sigmaRR > 0.20) {
                    alert(data);
                }

                debug(data);
                sigmas.poll();
            }

            entropies.poll();
        }

        remove(30);
    }
}