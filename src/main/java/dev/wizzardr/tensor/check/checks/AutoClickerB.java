package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;

import java.util.ArrayDeque;

public class AutoClickerB extends SwingCheck {

    public AutoClickerB(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker B")
                .withSize(2)
                .clearSamplesWhenFull()
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> samples) {
        double cps = getCps();

        if (cps > 19) {
            debug(String.format("cps: %.2f", cps));
        }
    }
}

