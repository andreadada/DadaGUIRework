package me.mrbast.dadagui.api;

/**
 * Platform-neutral contract for a GUI.
 *
 * @param <C> viewer/player type supplied by the platform adapter
 * @param <I> item type supplied by the platform adapter
 */
public interface Gui<C, I> {

    /**
     * Returns the visible title for the viewer.
     *
     * @param viewer viewer that is opening the GUI
     * @return inventory/window title
     */
    String title(C viewer);

    /**
     * Returns the desired inventory size. Platform adapters may normalize it.
     *
     * @param viewer viewer that is opening the GUI
     * @return desired slot count
     */
    int size(C viewer);

    /**
     * Renders slots for the current session.
     *
     * @param context render context used to bind slots
     */
    void render(GuiRenderContext<C, I> context);

    /**
     * Hook called after the GUI is opened.
     *
     * @param session current GUI session
     */
    default void onOpen(GuiSession<C, I> session) {
    }

    /**
     * Hook called after the GUI is closed.
     *
     * @param session current GUI session
     */
    default void onClose(GuiSession<C, I> session) {
    }
}
