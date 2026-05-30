package me.mrbast.dadagui.api;

import me.mrbast.dadagui.api.behavior.SlotBehavior;
import me.mrbast.dadagui.api.behavior.SlotBehaviors;
import me.mrbast.dadagui.api.storage.StorageBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Immutable slot definition: platform item plus a set of composable behaviors.
 *
 * <p>The slot is designed around <strong>HAS-A</strong> relationships: it has
 * behaviors, it is not a rigid subtype or enum. This keeps the model open for
 * custom slot policies while the builder still exposes convenient presets such
 * as {@link Builder#filler()}, {@link Builder#button()} and
 * {@link Builder#storage(StorageBinding)}.</p>
 *
 * @param <C> viewer/player type
 * @param <I> item type
 */
public final class GuiSlot<C, I> {
    private final I item;
    private final List<SlotBehavior<C, I>> behaviors;

    private GuiSlot(Builder<C, I> builder) {
        this.item = builder.item;
        this.behaviors = Collections.unmodifiableList(new ArrayList<>(builder.behaviors));
    }

    /**
     * Creates a new slot builder.
     *
     * @param item platform item
     * @param <C> viewer/player type
     * @param <I> item type
     * @return builder
     */
    public static <C, I> Builder<C, I> builder(I item) {
        return new Builder<>(item);
    }

    /**
     * @return platform item shown in the slot
     */
    public I item() {
        return item;
    }

    /**
     * @return immutable behavior list
     */
    public List<SlotBehavior<C, I>> behaviors() {
        return behaviors;
    }

    /**
     * Finds a behavior by concrete type.
     *
     * @param behaviorType behavior class/interface
     * @param <B> behavior type
     * @return first matching behavior
     */
    public <B extends SlotBehavior<C, I>> Optional<B> behavior(Class<B> behaviorType) {
        if (behaviorType == null) {
            return Optional.empty();
        }
        for (SlotBehavior<C, I> behavior : behaviors) {
            if (behaviorType.isInstance(behavior)) {
                return Optional.of(behaviorType.cast(behavior));
            }
        }
        return Optional.empty();
    }

    /**
     * @param behaviorType behavior class/interface
     * @return true if this slot has the requested behavior
     */
    public boolean hasBehavior(Class<? extends SlotBehavior> behaviorType) {
        if (behaviorType == null) {
            return false;
        }
        for (SlotBehavior<C, I> behavior : behaviors) {
            if (behaviorType.isInstance(behavior)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compatibility helper for old code that inspected enum slot types.
     * Prefer {@link #behaviors()} or {@link #hasBehavior(Class)}.
     *
     * @return best-effort preset inferred from the first known behavior
     */
    @Deprecated
    public SlotType type() {
        for (SlotBehavior<C, I> behavior : behaviors) {
            SlotType inferred = SlotType.fromBehaviorKey(behavior.key());
            if (inferred != SlotType.CUSTOM) {
                return inferred;
            }
        }
        return SlotType.CUSTOM;
    }

    /**
     * Compatibility helper. Prefer {@link #shouldCancelClick(ClickContext)}.
     *
     * @return true when the slot has no native-movement behavior
     */
    @Deprecated
    public boolean cancelClick() {
        return !acceptsNativeItemMovement();
    }

    /**
     * @param context click context
     * @return true when the native platform click should be cancelled
     */
    public boolean shouldCancelClick(ClickContext<C, I> context) {
        boolean cancel = true;
        for (SlotBehavior<C, I> behavior : behaviors) {
            if (!behavior.shouldCancelClick(context)) {
                cancel = false;
            }
        }
        return cancel;
    }

    /**
     * @return optional storage binding for mutable storage slots
     */
    public StorageBinding<C, I> storageBinding() {
        for (SlotBehavior<C, I> behavior : behaviors) {
            StorageBinding<C, I> binding = behavior.storageBinding();
            if (binding != null) {
                return binding;
            }
        }
        return null;
    }

    /**
     * @return true when this slot can be modified by native inventory movement
     */
    public boolean acceptsNativeItemMovement() {
        for (SlotBehavior<C, I> behavior : behaviors) {
            if (behavior.acceptsNativeItemMovement()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compatibility helper. Prefer {@link #acceptsNativeItemMovement()}.
     *
     * @return true when this slot is bound to mutable storage
     */
    @Deprecated
    public boolean isStorageSlot() {
        return acceptsNativeItemMovement() && storageBinding() != null;
    }

    /**
     * Executes all bound behavior click strategies.
     *
     * @param context click context
     */
    public void click(ClickContext<C, I> context) {
        for (SlotBehavior<C, I> behavior : behaviors) {
            behavior.onClick(context);
        }
    }

    /**
     * Builder for immutable slots.
     *
     * @param <C> viewer/player type
     * @param <I> item type
     */
    public static final class Builder<C, I> {
        private final I item;
        private final List<SlotBehavior<C, I>> behaviors = new ArrayList<>();

        private Builder(I item) {
            this.item = item;
            this.behaviors.add(SlotBehaviors.<C, I>button(context -> { }));
        }

        /**
         * Compatibility preset method. Enums are now only a builder convenience.
         * Prefer direct presets or {@link #behavior(SlotBehavior)} for extension.
         *
         * @param type preset slot type
         * @return this builder
         */
        public Builder<C, I> type(SlotType type) {
            SlotType resolved = type == null ? SlotType.BUTTON : type;
            if (resolved == SlotType.FILLER) {
                return filler();
            }
            if (resolved == SlotType.DISPLAY) {
                return display();
            }
            if (resolved == SlotType.CONTENT) {
                return content();
            }
            if (resolved == SlotType.STORAGE) {
                return clearBehaviors().behavior(SlotBehaviors.<C, I>storage(null));
            }
            if (resolved == SlotType.LOCKED) {
                return locked();
            }
            if (resolved == SlotType.CUSTOM) {
                return this;
            }
            return button();
        }

        /**
         * Replaces all behaviors with decorative filler behavior.
         *
         * @return this builder
         */
        public Builder<C, I> filler() {
            return clearBehaviors().behavior(SlotBehaviors.<C, I>filler());
        }

        /**
         * Replaces all behaviors with read-only display behavior.
         *
         * @return this builder
         */
        public Builder<C, I> display() {
            return clearBehaviors().behavior(SlotBehaviors.<C, I>display());
        }

        /**
         * Replaces all behaviors with clickable button behavior.
         *
         * @return this builder
         */
        public Builder<C, I> button() {
            return clearBehaviors().behavior(SlotBehaviors.<C, I>button(context -> { }));
        }

        /**
         * Replaces all behaviors with paginated content behavior.
         *
         * @return this builder
         */
        public Builder<C, I> content() {
            return clearBehaviors().behavior(SlotBehaviors.<C, I>content(context -> { }));
        }

        /**
         * Replaces all behaviors with locked behavior.
         *
         * @return this builder
         */
        public Builder<C, I> locked() {
            return clearBehaviors().behavior(SlotBehaviors.<C, I>locked());
        }

        /**
         * Replaces all behaviors with mutable storage behavior bound to a container.
         *
         * @param storageBinding storage binding
         * @return this builder
         */
        public Builder<C, I> storage(StorageBinding<C, I> storageBinding) {
            return clearBehaviors().behavior(SlotBehaviors.<C, I>storage(storageBinding));
        }

        /**
         * Adds a custom behavior. This is the extension point for new slot kinds.
         *
         * @param behavior slot behavior
         * @return this builder
         */
        public Builder<C, I> behavior(SlotBehavior<C, I> behavior) {
            if (behavior != null) {
                this.behaviors.add(behavior);
            }
            return this;
        }

        /**
         * Replaces all behaviors.
         *
         * @param behaviors slot behaviors
         * @return this builder
         */
        public Builder<C, I> behaviors(List<SlotBehavior<C, I>> behaviors) {
            clearBehaviors();
            if (behaviors != null) {
                for (SlotBehavior<C, I> behavior : behaviors) {
                    behavior(behavior);
                }
            }
            return this;
        }

        /**
         * Clears all behaviors so the caller can compose the slot manually.
         *
         * @return this builder
         */
        public Builder<C, I> clearBehaviors() {
            this.behaviors.clear();
            return this;
        }

        /**
         * Compatibility method that adds a native click policy behavior.
         * Prefer behavior composition for new code.
         *
         * @param cancelClick true to cancel the native click
         * @return this builder
         */
        public Builder<C, I> cancelClick(boolean cancelClick) {
            return behavior(SlotBehaviors.<C, I>nativeClickPolicy(cancelClick));
        }

        /**
         * Adds click logic as a behavior, without changing the current slot preset.
         *
         * @param clickHandler click handler
         * @return this builder
         */
        public Builder<C, I> onClick(SlotClickHandler<C, I> clickHandler) {
            if (clickHandler != null) {
                behavior(SlotBehaviors.<C, I>action("click", true, clickHandler));
            }
            return this;
        }

        /**
         * @return immutable slot
         */
        public GuiSlot<C, I> build() {
            if (behaviors.isEmpty()) {
                behaviors.add(SlotBehaviors.<C, I>display());
            }
            return new GuiSlot<>(this);
        }
    }
}
