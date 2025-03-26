package dev.wizzardr.tensor.check.factory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SwingCheckData {

    private String name;
    private int size;
    private boolean delta, clearSample, includeDoubleClicks, experimental;

}
