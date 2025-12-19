package dev.wizzardr.tensor.check.factory;

import dev.wizzardr.tensor.check.CheckCategory;

public record SwingCheckData(

        String name,
        String displayName,

        CheckCategory category,

        int size,

        boolean delta,
        boolean clearSample,
        boolean includeDoubleClicks,
        boolean experimental) {

    public String getEffectiveDisplayName() {
        return displayName != null ? displayName : name;
    }
}