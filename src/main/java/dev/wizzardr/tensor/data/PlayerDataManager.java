package dev.wizzardr.tensor.data;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerDataManager {
    private final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public PlayerData getPlayerData(final UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public void add(final UUID uuid) {
        playerDataMap.put(uuid, new PlayerData(uuid));
    }

    public boolean has(final UUID uuid) {
        return this.playerDataMap.containsKey(uuid);
    }

    public void remove(final UUID uuid) {
        playerDataMap.remove(uuid);
    }

    public Collection<PlayerData> getAllData() {
        return playerDataMap.values();
    }

    public void dispose() {
        playerDataMap.clear();
    }
}
