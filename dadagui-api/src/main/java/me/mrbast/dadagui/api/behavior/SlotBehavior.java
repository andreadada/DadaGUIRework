package me.mrbast.dadagui.api.behavior;

import me.mrbast.dadagui.api.ClickContext;
import me.mrbast.dadagui.api.storage.StorageBinding;

/**
 * Strategy interface that defines how a GUI slot behaves.
 *
 * <p>A slot is not a fixed enum value. A slot <strong>has one or more</strong>
 * behaviors, so custom frameworks/plugins can extend DadaGUI without changing
 * the core classes. This follows composition over inheritance and keeps the
 * platform runtime free from large switch statements.</p>
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public interface SlotBehavior<C, I> {

    /**
     * Human-readable stable behavior key, useful for diagnostics.
     *
     * @return behavior key
     */
    String key();

    /**
     * Decides if the native platform click should be cancelled.
     *
     * @param context click context
     * @return true to cancel the native platform event
     */
    default boolean shouldCancelClick(ClickContext<C, I> context) {
        return true;
    }

    /**
     * Executes behavior-specific click logic.
     *
     * @param context click context
     */
    default void onClick(ClickContext<C, I> context) {
    }

    /**
     * @return true when the native inventory system may move items in/out of this slot
     */
    default boolean acceptsNativeItemMovement() {
        return false;
    }

    /**
     * @return storage binding when this behavior maps the slot to a mutable storage container
     */
    default StorageBinding<C, I> storageBinding() {
        return null;
    }
}
