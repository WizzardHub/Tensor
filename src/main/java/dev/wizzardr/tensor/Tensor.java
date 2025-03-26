package dev.wizzardr.tensor;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Tensor extends JavaPlugin {

    public static String PREFIX = ChatColor.BLUE + "[Tensor] " + ChatColor.RESET;

    @Override
    public void onDisable() {
        TensorAPI.INSTANCE.onDisable(this);
    }

    @Override
    public void onEnable() {
        TensorAPI.INSTANCE.onEnable(this);
    }
}