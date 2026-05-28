package me.mrbast.dadagui.api;

/**
 * Functional interface executed when a GUI slot is clicked.
 *
 * @param <C> viewer/player type
 * @param <I> item type
 */
@FunctionalInterface
public interface SlotClickHandler<C, I> {

    /**
     * Handles a click.
     *
     * @param context click context
     */
    void handle(ClickContext<C, I> context);
}
