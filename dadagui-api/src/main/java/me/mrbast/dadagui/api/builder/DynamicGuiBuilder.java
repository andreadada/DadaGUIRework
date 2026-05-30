package me.mrbast.dadagui.api.builder;

import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiRenderer;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.view.DynamicGui;

import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * Builder for free-form runtime GUIs.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public final class DynamicGuiBuilder<C, I> {
    private Function<C, String> titleProvider = viewer -> "";
    private ToIntFunction<C> sizeProvider = viewer -> 27;
    private GuiRenderer<C, I> renderer = context -> { };
    private GuiScope scope = GuiScope.PER_PLAYER;
    private String sharedKey;

    public DynamicGuiBuilder<C, I> title(String title) {
        this.titleProvider = viewer -> title == null ? "" : title;
        return this;
    }

    public DynamicGuiBuilder<C, I> title(Function<C, String> titleProvider) {
        this.titleProvider = titleProvider == null ? viewer -> "" : titleProvider;
        return this;
    }

    public DynamicGuiBuilder<C, I> size(int size) {
        this.sizeProvider = viewer -> size;
        return this;
    }

    public DynamicGuiBuilder<C, I> rows(int rows) {
        this.sizeProvider = viewer -> rows * 9;
        return this;
    }

    public DynamicGuiBuilder<C, I> render(GuiRenderer<C, I> renderer) {
        this.renderer = renderer == null ? context -> { } : renderer;
        return this;
    }

    public DynamicGuiBuilder<C, I> scope(GuiScope scope) {
        this.scope = scope == null ? GuiScope.PER_PLAYER : scope;
        return this;
    }

    public DynamicGuiBuilder<C, I> sharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
        return this;
    }

    public DynamicGui<C, I> build() {
        return new ScopedDynamicGui<>(titleProvider, sizeProvider, renderer, scope, sharedKey);
    }

    private static final class ScopedDynamicGui<C, I> extends DynamicGui<C, I> {
        private final GuiScope scope;
        private final String sharedKey;

        private ScopedDynamicGui(Function<C, String> titleProvider,
                                 ToIntFunction<C> sizeProvider,
                                 GuiRenderer<C, I> renderer,
                                 GuiScope scope,
                                 String sharedKey) {
            super(titleProvider, sizeProvider, renderer);
            this.scope = scope;
            this.sharedKey = sharedKey;
        }

        @Override
        public GuiScope scope() {
            return scope;
        }

        @Override
        public String sharedKey() {
            return sharedKey == null ? super.sharedKey() : sharedKey;
        }
    }
}
