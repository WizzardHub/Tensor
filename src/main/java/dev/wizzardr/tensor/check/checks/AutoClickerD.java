package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.CheckCategory;
import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/*
 * Butterfly like generated pattern
 * The amount of perfectly timed double clicks and delay
 * is way too high
 */
public class AutoClickerD extends SwingCheck {

    public AutoClickerD(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker D")
                .withCategory(CheckCategory.CLICK_PATTERN)
                .withSize(250)
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        int[] distribution = Statistics.getDistribution(sample);

        // Basic generated butterfly pattern
        // double click -> wait -> double click ...
        List<Integer> n = new ArrayList<>(sample);
        long butterfly = IntStream.range(0, n.size() - 1)
                .filter(i -> n.get(i) == 0 && n.get(i + 1) > 2)
                .count();

        double ratio = butterfly / (double) getSize();
        int doubleClicks = Statistics.getDoubleClicks(sample);

        double doubleClickRatio = butterfly / (double) doubleClicks;
        double product = ratio * doubleClickRatio;

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, distribution: %s, product: %.2f, threshold: %.2f")
                .values(cps, Arrays.toString(distribution), product, threshold)
                .build();

        // value patching
        if (distribution[2] < 25) {
            product = 0;
        }

        if (threshold((product - 0.125) * 10) > 0.5) {
            alert(data);
        }

        debug(data);
        remove(100);
    }
}