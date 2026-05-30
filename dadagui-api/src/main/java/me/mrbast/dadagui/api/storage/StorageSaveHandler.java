package me.mrbast.dadagui.api.storage;

import me.mrbast.dadagui.api.GuiSession;

/**
 * Saves a storage container after the platform runtime synchronized its visible slots.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
@FunctionalInterface
public interface StorageSaveHandler<C, I> {

    /**
     * Persists the storage container.
     *
     * @param session current session
     * @param storage storage container
     */
    void save(GuiSession<C, I> session, StorageContainer<I> storage);
}
