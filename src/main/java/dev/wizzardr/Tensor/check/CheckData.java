package dev.wizzardr.Tensor.check;


import dev.wizzardr.Tensor.TensorAPI;
import dev.wizzardr.Tensor.check.factory.SwingCheck;
import dev.wizzardr.Tensor.data.PlayerData;
import lombok.Getter;

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