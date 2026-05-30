package me.mrbast.dadagui.api.behavior;

import me.mrbast.dadagui.api.ClickContext;
import me.mrbast.dadagui.api.SlotClickHandler;
import me.mrbast.dadagui.api.storage.StorageBinding;

/**
 * Factory for common slot behaviors.
 *
 * <p>This class intentionally contains small Strategy objects instead of
 * requiring users to subclass slot classes. The public builder methods can use
 * these defaults while advanced users can pass their own {@link SlotBehavior}.</p>
 */
public final class SlotBehaviors {
    private SlotBehaviors() {
    }

    public static <C, I> SlotBehavior<C, I> filler() {
        return passive("filler", true);
    }

    public static <C, I> SlotBehavior<C, I> display() {
        return passive("display", true);
    }

    public static <C, I> SlotBehavior<C, I> locked() {
        return passive("locked", true);
    }

    public static <C, I> SlotBehavior<C, I> button(SlotClickHandler<C, I> handler) {
        return action("button", true, handler);
    }

    public static <C, I> SlotBehavior<C, I> content(SlotClickHandler<C, I> handler) {
        return action("content", true, handler);
    }

    public static <C, I> SlotBehavior<C, I> nativeClickPolicy(final boolean cancelClick) {
        return passive("native-click-policy", cancelClick);
    }

    public static <C, I> SlotBehavior<C, I> storage(StorageBinding<C, I> binding) {
        return new StorageSlotBehavior<>(binding);
    }

    public static <C, I> SlotBehavior<C, I> passive(final String key, final boolean cancelClick) {
        return new BasicSlotBehavior<>(key, cancelClick, null);
    }

    public static <C, I> SlotBehavior<C, I> action(final String key,
                                                   final boolean cancelClick,
                                                   final SlotClickHandler<C, I> handler) {
        return new BasicSlotBehavior<>(key, cancelClick, handler);
    }

    private static final class BasicSlotBehavior<C, I> implements SlotBehavior<C, I> {
        private final String key;
        private final boolean cancelClick;
        private final SlotClickHandler<C, I> handler;

        private BasicSlotBehavior(String key, boolean cancelClick, SlotClickHandler<C, I> handler) {
            this.key = key == null ? "custom" : key;
            this.cancelClick = cancelClick;
            this.handler = handler;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public boolean shouldCancelClick(ClickContext<C, I> context) {
            return cancelClick;
        }

        @Override
        public void onClick(ClickContext<C, I> context) {
            if (handler != null) {
                handler.handle(context);
            }
        }
    }

    private static final class StorageSlotBehavior<C, I> implements SlotBehavior<C, I> {
        private final StorageBinding<C, I> binding;

        private StorageSlotBehavior(StorageBinding<C, I> binding) {
            this.binding = binding;
        }

        @Override
        public String key() {
            return "storage";
        }

        @Override
        public boolean shouldCancelClick(ClickContext<C, I> context) {
            return false;
        }

        @Override
        public boolean acceptsNativeItemMovement() {
            return binding != null;
        }

        @Override
        public StorageBinding<C, I> storageBinding() {
            return binding;
        }
    }
}
