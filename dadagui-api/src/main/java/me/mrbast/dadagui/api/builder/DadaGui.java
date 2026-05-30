package me.mrbast.dadagui.api.builder;

/**
 * Static entry point for the fluent DadaGUI API.
 */
public final class DadaGui {
    private DadaGui() {
    }

    /**
     * Starts a marker-layout GUI builder.
     *
     * @param <C> viewer/player type
     * @param <I> platform item type
     * @return builder
     */
    public static <C, I> LayoutGuiBuilder<C, I> staticGui() {
        return new LayoutGuiBuilder<>();
    }

    /**
     * Starts a free-form runtime GUI builder.
     *
     * @param <C> viewer/player type
     * @param <I> platform item type
     * @return builder
     */
    public static <C, I> DynamicGuiBuilder<C, I> dynamic() {
        return new DynamicGuiBuilder<>();
    }

    /**
     * Starts a paginated marker-layout GUI builder.
     *
     * @param contentMarker marker used by paginated content slots
     * @param <C> viewer/player type
     * @param <I> platform item type
     * @param <T> data item type
     * @return builder
     */
    public static <C, I, T> PaginatedGuiBuilder<C, I, T> paginated(char contentMarker) {
        return new PaginatedGuiBuilder<>(contentMarker);
    }


    /**
     * Starts an item-oriented paginated GUI builder.
     *
     * <p>This is the ergonomic layer for content classes that know how to draw
     * themselves and how to react to clicks, such as RecipeEntry, ShopEntry or
     * PlayerEntry. Runtime entries are passed when the GUI is opened, not stored
     * in the reusable GUI definition.</p>
     *
     * @param contentMarker marker used by paginated entry slots
     * @param <C> viewer/player type
     * @param <I> platform item type
     * @return builder
     */
    public static <C, I> PagedEntryGuiBuilder<C, I> pagedEntries(char contentMarker) {
        return new PagedEntryGuiBuilder<>(contentMarker);
    }

    /**
     * Starts a storage/vault marker-layout GUI builder.
     *
     * @param storageMarker marker used by mutable storage slots
     * @param <C> viewer/player type
     * @param <I> platform item type
     * @return builder
     */
    public static <C, I> StorageGuiBuilder<C, I> storage(char storageMarker) {
        return new StorageGuiBuilder<>(storageMarker);
    }
}
