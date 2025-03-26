package dev.wizzardr.tensor.check.checks;

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

        if (cps > 16) {
            if (threshold() > 3.0) {
                alert(String.format("cps: %.2f threshold: %.2f", cps, this.threshold));
            }
        } else {
            threshold(-0.5);
            decreaseVl(0.1);
        }
    }
}

