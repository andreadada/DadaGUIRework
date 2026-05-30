package me.mrbast.dadagui.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Small runtime state holder used by examples.
 *
 * <p>This is intentionally tiny: it shows that GUI state can live outside the
 * framework and can later be replaced by YAML, SQL, Redis or another service.</p>
 */
public final class ExamplePlayerSettings {
    private final Map<UUID, Boolean> notifications = new HashMap<>();
    private final Map<UUID, Boolean> sounds = new HashMap<>();

    public boolean notifications(UUID playerId) {
        Boolean value = notifications.get(playerId);
        return value == null || value;
    }

    public boolean toggleNotifications(UUID playerId) {
        boolean next = !notifications(playerId);
        notifications.put(playerId, next);
        return next;
    }

    public boolean sounds(UUID playerId) {
        Boolean value = sounds.get(playerId);
        return value == null || value;
    }

    public boolean toggleSounds(UUID playerId) {
        boolean next = !sounds(playerId);
        sounds.put(playerId, next);
        return next;
    }
}
