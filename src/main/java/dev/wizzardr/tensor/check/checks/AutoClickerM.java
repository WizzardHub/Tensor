package dev.wizzardr.tensor.check.checks;

import dev.wizzardr.tensor.check.CheckCategory;
import dev.wizzardr.tensor.check.data.DebugContainer;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.check.factory.SwingCheckBuilder;
import dev.wizzardr.tensor.data.PlayerData;
import dev.wizzardr.tensor.math.Statistics;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/*
* Flags low cps auto clickers
*/
public class AutoClickerM extends SwingCheck {

    public AutoClickerM(PlayerData playerData) {
        super(playerData, SwingCheckBuilder.create()
                .withName("Auto Clicker M")
                .withCategory(CheckCategory.CLICK_PATTERN)
                .clearSamplesWhenFull()
                .withSize(250)
                .build());
    }

    @Override
    protected void handle(ArrayDeque<Integer> sample) {

        double cps = getCps();
        double skewness = Statistics.getSkewness(sample);
        double variation = Statistics.getVariation(sample);
        double entropy = Statistics.getEntropy(sample);
        double mad = Statistics.getMAD(sample);
        double bds = Statistics.getBDS(sample);

        DebugContainer data = DebugContainer.builder()
                .formatString("cps: %.2f, skewness: %.2f, variation: %.2f, entropy: %.2f, mad: %.2f, bds: %.2f")
                .values(cps, skewness, variation, entropy, mad, bds)
                .build();

        if (entropy > 16.0 && skewness < 0 && mad < 0.65 && bds < 200) {
            alert(data);
        }
    }
}