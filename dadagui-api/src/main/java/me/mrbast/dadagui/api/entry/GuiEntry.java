package me.mrbast.dadagui.api.entry;

import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.api.layout.GuiIngredient;

/**
 * Item-oriented GUI entry.
 *
 * <p>A {@code GuiEntry} is useful when the content object itself should decide
 * how it is drawn and how it reacts to clicks, like older systems based on
 * {@code AbstractItem#getItemProvider()} and {@code handleClick(...)}.</p>
 *
 * <p>It remains platform-neutral: the item type is generic and Bukkit-specific
 * objects stay in the Bukkit module or in the plugin using the framework.</p>
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
@FunctionalInterface
public interface GuiEntry<C, I> {

    /**
     * Converts this entry to a slot for a concrete render pass.
     *
     * @param context render context
     * @param slotIndex inventory/window slot index
     * @return rendered slot, or null to leave the slot empty
     */
    GuiSlot<C, I> toSlot(GuiRenderContext<C, I> context, int slotIndex);

    /**
     * Adapts this entry to a normal layout ingredient.
     *
     * @return ingredient wrapper
     */
    default GuiIngredient<C, I> asIngredient() {
        return this::toSlot;
    }
}
