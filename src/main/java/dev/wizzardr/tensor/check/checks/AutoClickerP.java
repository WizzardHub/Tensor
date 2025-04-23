package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;
import java.util.Arrays;


public class AutoClickerP extends SwingCheck {

    public AutoClickerP(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker P")
                .withSize(400)
                .asDeltaCheck()
                .excludeDoubleClicks()
                .markAsExperimental()
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double stDev = Statistics.getStDev(sample);

        ArrayDeque<Integer> raw = getSample(400);

        long doubleClicks = Statistics.getDoubleClicks(raw);
        double rawRR = Statistics.getRecurrenceRate(raw, 1);

        int[] distribution = Statistics.getDistribution(sample);

        int difference = Math.abs(distribution[0] - distribution[1]);
        double deltaSTD = Math.abs(0.5 - stDev);

        boolean correctDistribution = Math.max(distribution[0], distribution[1]) > 150
                && difference < 50;

        if (cps > 8 && deltaSTD < 0.05 && doubleClicks < 40 && correctDistribution) {

            threshold(Math.pow(rawRR, 10));

            DebugContainer data = DebugContainer.builder()
                    .formatString("cps: %.2f, stDev: %.2f, recurrenceRate: %.2f, doubleClicks: %s, difference: %s, distribution: %s, threshold: %.2f")
                    .values(cps, stDev, rawRR, doubleClicks, difference, Arrays.toString(distribution), threshold)
                    .build();

            alert(data);
        } else {
            threshold(-0.1);
        }

        remove(200);
    }
}