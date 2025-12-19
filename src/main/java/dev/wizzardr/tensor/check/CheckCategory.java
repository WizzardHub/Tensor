package dev.wizzardr.tensor.check;

import lombok.Getter;

@Getter
public enum CheckCategory {

    CPS_LIMIT("Click Per Second Limit"),
    CLICK_PATTERN("Clicking Suspiciously");

    private final String displayName;

    CheckCategory(String displayName) {
        this.displayName = displayName;
    }

}