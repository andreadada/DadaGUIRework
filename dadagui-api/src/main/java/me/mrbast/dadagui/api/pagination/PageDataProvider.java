package me.mrbast.dadagui.api.pagination;

import java.util.List;

/**
 * Supplies data for a paginated GUI.
 *
 * @param <C> viewer/player type
 * @param <T> data row type
 */
@FunctionalInterface
public interface PageDataProvider<C, T> {

    /**
     * Returns the data visible to the viewer.
     *
     * @param viewer current viewer
     * @return rows/items to paginate
     */
    List<T> getItems(C viewer);
}
