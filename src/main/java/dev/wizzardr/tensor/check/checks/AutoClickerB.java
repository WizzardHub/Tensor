package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;

import java.util.ArrayDeque;

/*
 * It is almost impossible for human to consistently click 16+ cps
 * with no double clicks, especially when the clicks are being
 * rounded into ticks
 */
public class AutoClickerB extends SwingCheck {

    public AutoClickerB(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker B")
                .withSize(50)
                .clearSamplesWhenFull()
                .excludeDoubleClicks()
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> samples) {
        double cps = getCps();

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, threshold: %.2f")
                .values(cps, threshold)
                .build();

        // Balance 0.75 = 15cps which is the decay
        if (threshold((cps / 20.0) - 0.75) > 0.15) {
            alert(data);
        }

        debug(data);
    }
}