package dev.wizzardr.Tensor.check.factory;

import java.util.Objects;

public class SwingCheckBuilder {

    private String name;
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

    public SwingCheckBuilder withSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        this.size = size;
        return this;
    }

    public SwingCheckBuilder withDelta(boolean delta) {
        this.delta = delta;
        return this;
    }

    public SwingCheckBuilder clearSamplesWhenFull(boolean clearSamples) {
        this.clearSample = clearSamples;
        return this;
    }

    public SwingCheckBuilder includeDoubleClicks(boolean includeDoubleClicks) {
        this.includeDoubleClicks = includeDoubleClicks;
        return this;
    }

    public SwingCheckBuilder markAsExperimental(boolean experimental) {
        this.experimental = experimental;
        return this;
    }

    public SwingCheckData build() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalStateException("Check name must be set using withName()");
        }
        if (size <= 0) {
            throw new IllegalStateException("Sample size must be set to a positive value using withSize()");
        }

        return new SwingCheckData(
                this.name,
                this.size,
                this.delta,
                this.clearSample,
                this.includeDoubleClicks,
                this.experimental
        );
    }
}
