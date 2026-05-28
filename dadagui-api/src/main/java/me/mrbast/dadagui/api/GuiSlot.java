package me.mrbast.dadagui.api;

/**
 * Immutable slot definition: item, click policy and click behavior.
 *
 * @param <C> viewer/player type
 * @param <I> item type
 */
public final class GuiSlot<C, I> {
    private final I item;
    private final boolean cancelClick;
    private final SlotClickHandler<C, I> clickHandler;

    private GuiSlot(Builder<C, I> builder) {
        this.item = builder.item;
        this.cancelClick = builder.cancelClick;
        this.clickHandler = builder.clickHandler;
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
     * @return true when the platform click should be cancelled
     */
    public boolean cancelClick() {
        return cancelClick;
    }

    /**
     * Executes the bound click handler.
     *
     * @param context click context
     */
    public void click(ClickContext<C, I> context) {
        clickHandler.handle(context);
    }

    /**
     * Builder for immutable slots.
     *
     * @param <C> viewer/player type
     * @param <I> item type
     */
    public static final class Builder<C, I> {
        private final I item;
        private boolean cancelClick = true;
        private SlotClickHandler<C, I> clickHandler = context -> {
        };

        private Builder(I item) {
            this.item = item;
        }

        /**
         * Sets whether the underlying click event should be cancelled.
         *
         * @param cancelClick true to cancel the native click
         * @return this builder
         */
        public Builder<C, I> cancelClick(boolean cancelClick) {
            this.cancelClick = cancelClick;
            return this;
        }

        /**
         * Sets the click handler.
         *
         * @param clickHandler click handler
         * @return this builder
         */
        public Builder<C, I> onClick(SlotClickHandler<C, I> clickHandler) {
            if (clickHandler != null) {
                this.clickHandler = clickHandler;
            }
            return this;
        }

        /**
         * @return immutable slot
         */
        public GuiSlot<C, I> build() {
            return new GuiSlot<>(this);
        }
    }
}
