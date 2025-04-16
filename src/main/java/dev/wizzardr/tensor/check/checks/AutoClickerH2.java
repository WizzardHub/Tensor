package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;

/*
 * Flags very low kurtosis patterns
 */
public class AutoClickerH2 extends SwingCheck {

    public AutoClickerH2(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker H2")
                .withSize(200)
                .asDeltaCheck()
                .clearSamplesWhenFull()
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double bds = Statistics.getBDS(sample);

        int oscillation = Statistics.getOscillation(sample);

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, bds: %.2f, oscillation: %s")
                .values(cps, bds, oscillation)
                .build();

        if (cps > 8 && oscillation <= 1 && bds > 90) {
            alert(data);
        }
    }
}