package me.mrbast.dadagui.api.layout;

import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiSlot;

/**
 * A renderable GUI ingredient bound to one or more layout markers.
 *
 * <p>An ingredient may be completely static or may inspect the render context to
 * produce player/session-specific items.</p>
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
@FunctionalInterface
public interface GuiIngredient<C, I> {

    /**
     * Renders this ingredient for a concrete slot.
     *
     * @param context render context
     * @param slotIndex numeric slot index
     * @return slot definition, or null to leave the slot empty
     */
    GuiSlot<C, I> render(GuiRenderContext<C, I> context, int slotIndex);
}
