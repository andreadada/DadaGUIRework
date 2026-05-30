package me.mrbast.dadagui.api.layout;

import me.mrbast.dadagui.api.GuiRenderContext;

import java.util.List;

/**
 * Supplies runtime data for a content-based GUI.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 * @param <T> data item type
 */
@FunctionalInterface
public interface GuiContentProvider<C, I, T> {

    /**
     * @param context render context
     * @return data items to render
     */
    List<T> getItems(GuiRenderContext<C, I> context);
}
