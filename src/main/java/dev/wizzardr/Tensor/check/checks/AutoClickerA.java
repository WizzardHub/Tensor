package dev.wizzardr.Tensor.check.checks;

import dev.wizzardr.Tensor.check.factory.SwingCheck;
import dev.wizzardr.Tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.Tensor.data.PlayerData;
import dev.wizzardr.Tensor.math.Statistics;

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
