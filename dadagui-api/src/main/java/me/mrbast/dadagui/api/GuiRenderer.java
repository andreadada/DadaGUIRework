package me.mrbast.dadagui.api;

/**
 * Functional renderer used by runtime/dynamic GUIs.
 *
 * @param <C> viewer/player type
 * @param <I> item type
 */
@FunctionalInterface
public interface GuiRenderer<C, I> {

    /**
     * Renders a GUI.
     *
     * @param context render context
     */
    void render(GuiRenderContext<C, I> context);
}
