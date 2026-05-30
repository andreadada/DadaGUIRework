package me.mrbast.dadagui.api.layout;

import me.mrbast.dadagui.api.GuiRenderContext;

/**
 * Converts one data item into a GUI ingredient.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 * @param <T> data item type
 */
@FunctionalInterface
public interface GuiContentRenderer<C, I, T> {

    /**
     * @param context render context
     * @param item data item
     * @param absoluteIndex item index in the full unpaginated data list
     * @return renderable ingredient
     */
    GuiIngredient<C, I> render(GuiRenderContext<C, I> context, T item, int absoluteIndex);
}
