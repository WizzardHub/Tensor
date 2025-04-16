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
public class AutoClickerL extends SwingCheck {

    public AutoClickerL(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker L")
                .withSize(200)
                .asDeltaCheck()
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double kurtosis = Statistics.getKurtosis(sample);

        int[] distribution = Statistics.getDistribution(sample);
        int slowDistribution = distribution[3] + distribution[4];

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, kurtosis: %.2f, distribution: %s")
                .values(cps, kurtosis, Arrays.toString(distribution))
                .build();

        if (cps > 8 && distribution[0] > 60 && distribution[1] > 130 && slowDistribution == 0) {
            alert(data);
        }

        remove(100);
    }
}