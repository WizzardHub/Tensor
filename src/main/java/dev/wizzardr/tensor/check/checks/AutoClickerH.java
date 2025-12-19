package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.CheckCategory;
import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;

/*
 * Flags very low kurtosis patterns
 */
public class AutoClickerH extends SwingCheck {

    public AutoClickerH(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker H")
                .withCategory(CheckCategory.CLICK_PATTERN)
                .withSize(200)
                .clearSamplesWhenFull()
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double bds = Statistics.getBDS(sample);

        double oscillation = Statistics.getOscillation(sample);

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, bds: %.2f, oscillation: %s")
                .values(cps, bds, oscillation)
                .build();

        boolean bdsCompliant = Math.abs(100.0 - bds) > 0.5;
        if (cps > 8 && oscillation <= 1 && bdsCompliant) {
            alert(data);
        }
    }
}