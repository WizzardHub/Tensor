package dev.wizzardr.tensor.check;


import dev.wizzardr.tensor.TensorAPI;
import dev.wizzardr.tensor.check.factory.SwingCheck;
import dev.wizzardr.tensor.data.PlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CheckData {
    private Set<SwingCheck> swingChecks;

    public void register(PlayerData playerData) {
        CheckManager checkManager = TensorAPI.INSTANCE.getCheckManager();

        swingChecks = checkManager.getConstructors().stream()
                .map(clazz -> {
                    try {
                        return (SwingCheck) clazz.newInstance(playerData);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }
}