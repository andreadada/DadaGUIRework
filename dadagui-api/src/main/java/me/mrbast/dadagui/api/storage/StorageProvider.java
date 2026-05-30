package me.mrbast.dadagui.api.storage;

import me.mrbast.dadagui.api.GuiRenderContext;

/**
 * Provides the storage container to show for a viewer/session.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
@FunctionalInterface
public interface StorageProvider<C, I> {

    /**
     * Opens or resolves the storage container for the current render.
     *
     * @param context render context
     * @return storage container
     */
    StorageContainer<I> open(GuiRenderContext<C, I> context);
}
