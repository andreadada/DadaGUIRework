package me.mrbast.dadagui.api.storage;

import me.mrbast.dadagui.api.GuiSession;

import java.util.Objects;

/**
 * Default storage binding implementation.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public final class DefaultStorageBinding<C, I> implements StorageBinding<C, I> {
    private final String storageId;
    private final StorageContainer<I> storage;
    private final int storageIndex;
    private final StorageSaveHandler<C, I> saveHandler;

    public DefaultStorageBinding(String storageId,
                                 StorageContainer<I> storage,
                                 int storageIndex,
                                 StorageSaveHandler<C, I> saveHandler) {
        this.storageId = storageId == null ? "storage-" + System.identityHashCode(storage) : storageId;
        this.storage = Objects.requireNonNull(storage, "storage");
        this.storageIndex = storageIndex;
        this.saveHandler = saveHandler == null ? (session, container) -> { } : saveHandler;
    }

    @Override
    public String storageId() {
        return storageId;
    }

    @Override
    public int storageIndex() {
        return storageIndex;
    }

    @Override
    public void updateItem(I item) {
        storage.setItem(storageIndex, item);
    }

    @Override
    public void save(GuiSession<C, I> session) {
        saveHandler.save(session, storage);
    }
}
