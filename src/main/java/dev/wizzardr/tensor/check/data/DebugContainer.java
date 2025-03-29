package dev.wizzardr.tensor.check.data;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class DebugContainer {
    private final String formatString;
    private final List<Object> values;

    public String getFormattedOutput() {

        try {
            String[] parts = formatString.split(", ");
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < parts.length; i++) {
                String part = parts[i].trim();
                int colonIndex = part.indexOf(':');
                String label = part.substring(0, colonIndex + 1);

                sb.append(ChatColor.WHITE)
                        .append(label)
                        .append(" ")
                        .append(formatValue(part.substring(colonIndex + 1).trim(), values.get(i)));

                if (i < parts.length - 1) {
                    sb.append("\n");
                }
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Malformed input";
    }

    private String formatValue(String formatSpec, Object value) {
        String formattedValue = String.format(formatSpec.trim(), value);
        return (value instanceof Number ? ChatColor.AQUA : ChatColor.WHITE) + formattedValue;
    }

    public static class DebugContainerBuilder {
        public DebugContainerBuilder values(Object... values) {
            if (this.values == null) {
                this.values = new ArrayList<>();
            }
            this.values.addAll(List.of(values));
            return this;
        }
    }
}