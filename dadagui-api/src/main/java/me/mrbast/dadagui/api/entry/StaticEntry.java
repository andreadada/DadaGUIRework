package me.mrbast.dadagui.api.entry;

import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiSlot;

/**
 * Simple immutable entry backed by a prebuilt slot.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public final class StaticEntry<C, I> implements GuiEntry<C, I> {
    private final GuiSlot<C, I> slot;

    public StaticEntry(GuiSlot<C, I> slot) {
        this.slot = slot;
    }

    @Override
    public GuiSlot<C, I> toSlot(GuiRenderContext<C, I> context, int slotIndex) {
        return slot;
    }
}
