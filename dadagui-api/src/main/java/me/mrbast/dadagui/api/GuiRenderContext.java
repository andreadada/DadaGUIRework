package me.mrbast.dadagui.api;

/**
 * Context used by GUI implementations during rendering.
 *
 * @param <C> viewer/player type
 * @param <I> item type
 */
public interface GuiRenderContext<C, I> {

    /**
     * @return current session
     */
    GuiSession<C, I> session();

    /**
     * @return current viewer
     */
    C viewer();

    /**
     * Binds a GUI slot to a numeric position.
     *
     * @param index slot index
     * @param slot slot content and behavior
     */
    void setSlot(int index, GuiSlot<C, I> slot);

    /**
     * Convenience method for a non-clickable item.
     *
     * @param index slot index
     * @param item platform item
     */
    default void setItem(int index, I item) {
        setSlot(index, GuiSlot.<C, I>builder(item).build());
    }
}
