package me.mrbast.dadagui.api;

import java.util.Optional;

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
    /**
     * Convenience method for refreshing the current GUI session.
     */
    default void refresh() {
        session().refresh();
    }

    /**
     * Convenience method for refreshing all viewers of the same shared GUI.
     */
    default void refreshAllViewers() {
        session().refreshAllViewers();
    }

    /**
     * Convenience method for closing the current GUI.
     */
    default void close() {
        session().close();
    }

    /**
     * Stores a session attribute.
     *
     * @param key attribute key
     * @param value attribute value, or null to remove it
     */
    default void setAttribute(String key, Object value) {
        session().setAttribute(key, value);
    }

    /**
     * Reads a typed session attribute.
     *
     * @param key attribute key
     * @param type expected type
     * @param <T> value type
     * @return optional value
     */
    default <T> Optional<T> getAttribute(String key, Class<T> type) {
        return session().getAttribute(key, type);
    }

}
