package dev.wizzardr.tensor.check.factory;

import dev.wizzardr.tensor.check.CheckCategory;

import java.util.Objects;

public class SwingCheckBuilder {

    private String name;
    private String displayName;

    private CheckCategory category;

    private int size = Integer.MIN_VALUE;

    private boolean delta = false;
    private boolean clearSample = false;
    private boolean includeDoubleClicks = true;
    private boolean experimental = false;

    private SwingCheckBuilder() {}

    public static SwingCheckBuilder create() {
        return new SwingCheckBuilder();
    }

    public SwingCheckBuilder withName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        return this;
    }

    public SwingCheckBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public SwingCheckBuilder withCategory(CheckCategory category) {
        this.category = Objects.requireNonNull(category, "Category cannot be null");
        return this;
    }

    public SwingCheckBuilder withSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        this.size = size;
        return this;
    }

    public SwingCheckBuilder asDeltaCheck() {
        this.delta = true;
        return this;
    }

    public SwingCheckBuilder clearSamplesWhenFull() {
        this.clearSample = true;
        return this;
    }

    public SwingCheckBuilder excludeDoubleClicks() {
        this.includeDoubleClicks = false;
        return this;
    }

    public SwingCheckBuilder markAsExperimental() {
        this.experimental = true;
        return this;
    }

    public SwingCheckData build() {
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("Check name must be set using withName()");
        }

        if (size <= 0) {
            throw new IllegalStateException("Sample size must be set to a positive value using withSize()");
        }

        return new SwingCheckData(
                name,
                displayName,
                category,
                size,
                delta,
                clearSample,
                includeDoubleClicks,
                experimental
        );
    }
}