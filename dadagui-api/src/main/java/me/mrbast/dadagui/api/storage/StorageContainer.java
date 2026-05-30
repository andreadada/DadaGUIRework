package me.mrbast.dadagui.api.storage;

/**
 * Platform-neutral mutable item container used by storage GUIs such as vaults.
 *
 * @param <I> platform item type
 */
public interface StorageContainer<I> {

    /**
     * @return logical storage size
     */
    int size();

    /**
     * Reads an item at a logical index.
     *
     * @param index logical storage index
     * @return item, or null/air depending on the platform adapter
     */
    I getItem(int index);

    /**
     * Writes an item at a logical index.
     *
     * @param index logical storage index
     * @param item item, or null/air depending on the platform adapter
     */
    void setItem(int index, I item);
}
