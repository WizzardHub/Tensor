package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;

public class AutoClickerA extends SwingCheck {

    public AutoClickerA(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                        .withName("Auto Clicker A")
                        .withSize(20)
                        .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> samples) {
        double cps = getCps();
        double stDev = Statistics.getStDev(samples);

        debug(String.format("cps: %.2f stDev: %.2f", cps, stDev));
    }
}
