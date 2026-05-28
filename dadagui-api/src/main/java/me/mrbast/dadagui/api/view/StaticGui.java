package me.mrbast.dadagui.api.view;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiSlot;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple immutable GUI with fixed slots.
 *
 * @param <C> viewer/player type
 * @param <I> item type
 */
public final class StaticGui<C, I> implements Gui<C, I> {
    private final String title;
    private final int size;
    private final Map<Integer, GuiSlot<C, I>> slots;

    private StaticGui(Builder<C, I> builder) {
        this.title = builder.title;
        this.size = builder.size;
        this.slots = Collections.unmodifiableMap(new LinkedHashMap<>(builder.slots));
    }

    public static <C, I> Builder<C, I> builder(String title, int size) {
        return new Builder<>(title, size);
    }

    @Override
    public String title(C viewer) {
        return title;
    }

    @Override
    public int size(C viewer) {
        return size;
    }

    @Override
    public void render(GuiRenderContext<C, I> context) {
        for (Map.Entry<Integer, GuiSlot<C, I>> entry : slots.entrySet()) {
            context.setSlot(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Builder for static GUIs.
     *
     * @param <C> viewer/player type
     * @param <I> item type
     */
    public static final class Builder<C, I> {
        private final String title;
        private final int size;
        private final Map<Integer, GuiSlot<C, I>> slots = new LinkedHashMap<>();

        private Builder(String title, int size) {
            this.title = title;
            this.size = size;
        }

        public Builder<C, I> slot(int index, GuiSlot<C, I> slot) {
            slots.put(index, slot);
            return this;
        }

        public StaticGui<C, I> build() {
            return new StaticGui<>(this);
        }
    }
}
