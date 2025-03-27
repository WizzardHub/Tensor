package dev.wizzardr.tensor.check.base;

import org.bukkit.ChatColor;

public class DebugContainer {

    private final String formatString;
    private final Object[] values;

    private DebugContainer(Builder builder) {
        this.formatString = builder.formatString;
        this.values = builder.values;
    }

    public String getFormattedOutput() {
        String[] parts = formatString.split(", ");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            String formattedValue = formatValue(part, values[i]);
            sb.append(ChatColor.GRAY).append(part, 0, part.indexOf(':') + 1).append(" ").append(formattedValue).append("\n");
        }
        return sb.toString();
    }

    private String formatValue(String part, Object value) {
        String formattedValue;
        if (value instanceof Number) {
            formattedValue = String.format(part, value);
            formattedValue = ChatColor.YELLOW + formattedValue;
        } else {
            formattedValue = String.format(part, value);
            formattedValue = ChatColor.GRAY + formattedValue;
        }
        return formattedValue;
    }

    public static class Builder {
        private final String formatString;
        private Object[] values;

        public Builder(String formatString) {
            this.formatString = formatString;
        }

        public Builder withValues(Object... values) {
            this.values = values;
            return this;
        }

        public DebugContainer build() {
            return new DebugContainer(this);
        }
    }
}