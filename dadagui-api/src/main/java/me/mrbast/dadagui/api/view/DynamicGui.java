package me.mrbast.dadagui.api.view;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiRenderer;

import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Runtime GUI whose title, size and content can be generated per viewer/session.
 *
 * @param <C> viewer/player type
 * @param <I> item type
 */
public final class DynamicGui<C, I> implements Gui<C, I> {
    private final Function<C, String> titleProvider;
    private final ToIntFunction<C> sizeProvider;
    private final GuiRenderer<C, I> renderer;

    public DynamicGui(Function<C, String> titleProvider, ToIntFunction<C> sizeProvider, GuiRenderer<C, I> renderer) {
        this.titleProvider = titleProvider;
        this.sizeProvider = sizeProvider;
        this.renderer = renderer;
    }

    @Override
    public String title(C viewer) {
        return titleProvider.apply(viewer);
    }

    @Override
    public int size(C viewer) {
        return sizeProvider.applyAsInt(viewer);
    }

    @Override
    public void render(GuiRenderContext<C, I> context) {
        renderer.render(context);
    }
}
