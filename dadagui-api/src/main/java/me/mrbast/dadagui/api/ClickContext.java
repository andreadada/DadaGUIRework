package me.mrbast.dadagui.api;

/**
 * Platform-neutral click context.
 *
 * @param <C> viewer/player type
 * @param <I> item type
 */
public interface ClickContext<C, I> {

    /**
     * @return current session
     */
    GuiSession<C, I> session();

    /**
     * @return viewer that clicked
     */
    C viewer();

    /**
     * @return raw slot clicked in the top inventory/window
     */
    int rawSlot();

    /**
     * @return true when the platform reports a left click
     */
    boolean isLeftClick();

    /**
     * @return true when the platform reports a right click
     */
    boolean isRightClick();

    /**
     * @return true when the platform reports a shift click
     */
    boolean isShiftClick();

    /**
     * Returns the platform-native event object when advanced handling is needed.
     * The framework core never depends on this object.
     *
     * @return native platform event
     */
    Object nativeEvent();
}
