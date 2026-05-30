package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.storage.SimpleStorageContainer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tiny in-memory vault repository for examples.
 *
 * <p>Real plugins can replace this class with YAML, SQLite, MySQL or any other
 * persistence implementation without changing the GUI definitions.</p>
 */
public final class ExampleVaultRepository {
    private final int size;
    private final Map<UUID, SimpleStorageContainer<ItemStack>> personalVaults = new HashMap<>();
    private final Map<String, SimpleStorageContainer<ItemStack>> sharedVaults = new HashMap<>();

    public ExampleVaultRepository(int size) {
        this.size = size;
    }

    public SimpleStorageContainer<ItemStack> personal(UUID playerId) {
        SimpleStorageContainer<ItemStack> storage = personalVaults.get(playerId);
        if (storage == null) {
            storage = new SimpleStorageContainer<>(size);
            personalVaults.put(playerId, storage);
        }
        return storage;
    }

    public SimpleStorageContainer<ItemStack> shared(String id) {
        SimpleStorageContainer<ItemStack> storage = sharedVaults.get(id);
        if (storage == null) {
            storage = new SimpleStorageContainer<>(size);
            sharedVaults.put(id, storage);
        }
        return storage;
    }

    public void savePersonal(UUID playerId, SimpleStorageContainer<ItemStack> storage) {
        personalVaults.put(playerId, storage);
    }

    public void saveShared(String id, SimpleStorageContainer<ItemStack> storage) {
        sharedVaults.put(id, storage);
    }
}
