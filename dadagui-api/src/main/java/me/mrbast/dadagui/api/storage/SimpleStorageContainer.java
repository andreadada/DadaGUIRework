package me.mrbast.dadagui.api.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple in-memory storage container useful for examples, tests and runtime-only vaults.
 *
 * @param <I> platform item type
 */
public final class SimpleStorageContainer<I> implements StorageContainer<I> {
    private final List<I> items;

    public SimpleStorageContainer(int size) {
        this.items = new ArrayList<>(Collections.nCopies(Math.max(0, size), null));
    }

    public SimpleStorageContainer(List<I> items) {
        this.items = new ArrayList<>(items == null ? Collections.<I>emptyList() : items);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public I getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    @Override
    public void setItem(int index, I item) {
        if (index < 0 || index >= items.size()) {
            return;
        }
        items.set(index, item);
    }

    /**
     * @return mutable backing list
     */
    public List<I> items() {
        return items;
    }
}
