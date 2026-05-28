package me.mrbast.dadagui.api;

import java.util.Optional;

/**
 * Runtime state of one opened GUI.
 *
 * @param <C> viewer/player type
 * @param <I> item type
 */
public interface GuiSession<C, I> {

    /**
     * @return viewer associated with this session
     */
    C viewer();

    /**
     * @return GUI currently rendered by this session
     */
    Gui<C, I> gui();

    /**
     * Re-renders the current GUI.
     */
    void refresh();

    /**
     * Closes the current GUI.
     */
    void close();

    /**
     * Stores session-scoped state used by dynamic/paginated GUIs.
     *
     * @param key state key
     * @param value state value
     */
    void setAttribute(String key, Object value);

    /**
     * Reads typed session-scoped state.
     *
     * @param key state key
     * @param type expected value type
     * @param <T> value type
     * @return optional value
     */
    <T> Optional<T> getAttribute(String key, Class<T> type);
}
