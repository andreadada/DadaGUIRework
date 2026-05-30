package me.mrbast.dadagui.api.entry;

/**
 * Factory that adapts domain objects to item-oriented GUI entries.
 *
 * @param <T> domain/content type
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
@FunctionalInterface
public interface GuiEntryFactory<T, C, I> {

    /**
     * @param value domain value
     * @param index absolute index in the paginated content list
     * @return entry representing the value
     */
    GuiEntry<C, I> create(T value, int index);
}
