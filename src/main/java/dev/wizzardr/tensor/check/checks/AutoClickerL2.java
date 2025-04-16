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
 * Pattern based detection
 */
public class AutoClickerL2 extends SwingCheck {

    public AutoClickerL2(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker L2")
                .withSize(200)
                .asDeltaCheck()
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double recurrenceRate = Statistics.getRecurrenceRate(sample, 0.1);

        int[] distribution = Statistics.getDistribution(sample);
        int slowDistribution = distribution[2] + distribution[3];

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, recurrenceRate: %.2f, distribution: %s, threshold: %.2f")
                .values(cps, recurrenceRate, Arrays.toString(distribution), threshold)
                .build();

        if (cps > 8 && recurrenceRate < 0.55 && distribution[0] > 50 && distribution[1] > 120 && slowDistribution == 0) {
            alert(data);
        }

        debug(data);
        remove(100);
    }
}