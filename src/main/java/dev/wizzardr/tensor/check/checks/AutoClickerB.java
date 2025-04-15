package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;

import java.util.ArrayDeque;

/*
 * It is almost impossible for human to consistently click 17+ cps
 * with no double clicks, especially when the clicks are being
 * rounded into ticks
 */
public class AutoClickerB extends SwingCheck {

    public AutoClickerB(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker B")
                .withSize(100)
                .clearSamplesWhenFull()
                .excludeDoubleClicks()
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> sample) {
        double cps = getCps();

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, threshold: %.2f")
                .values(cps, threshold)
                .build();

        // Balance 0.85 = 17cps which is the decay
        if (threshold((cps / 20.0) - 0.85) > 0.2) {
            alert(data);
            threshold(-1);
        }

        debug(data);
    }
}