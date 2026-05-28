package me.mrbast.dadagui.api.pagination;

import me.mrbast.dadagui.api.GuiSlot;

/**
 * Converts a data item into a GUI slot.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 * @param <T> data item type
 */
@FunctionalInterface
public interface PageItemFactory<C, I, T> {

    /**
     * Creates a slot for one row of data.
     *
     * @param viewer current viewer
     * @param item data item
     * @param absoluteIndex index in the full data list
     * @return slot to render
     */
    GuiSlot<C, I> create(C viewer, T item, int absoluteIndex);
}
