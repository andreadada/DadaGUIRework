package me.mrbast.dadagui.api.pagination;

import me.mrbast.dadagui.api.GuiSlot;

/**
 * Creates previous/next page control slots.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
@FunctionalInterface
public interface PageControlFactory<C, I> {

    /**
     * Creates a control slot.
     *
     * @param page current page index, starting from 0
     * @param maxPage max page index, starting from 0
     * @return control slot
     */
    GuiSlot<C, I> create(int page, int maxPage);
}
