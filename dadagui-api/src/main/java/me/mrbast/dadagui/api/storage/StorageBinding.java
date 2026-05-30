package me.mrbast.dadagui.api.storage;

import me.mrbast.dadagui.api.GuiSession;

/**
 * Links a physical GUI slot to a logical storage index.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public interface StorageBinding<C, I> {

    /**
     * @return stable identity for the backing storage container inside a render pass
     */
    String storageId();

    /**
     * @return logical index inside the storage container
     */
    int storageIndex();

    /**
     * Updates the backing storage with the item currently visible in the native inventory slot.
     *
     * @param item visible item
     */
    void updateItem(I item);

    /**
     * Saves the backing storage.
     *
     * @param session current session
     */
    void save(GuiSession<C, I> session);
}
