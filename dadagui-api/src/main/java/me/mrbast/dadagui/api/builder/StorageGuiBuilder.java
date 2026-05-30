package me.mrbast.dadagui.api.builder;

import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.layout.GuiIngredient;
import me.mrbast.dadagui.api.layout.GuiLayout;
import me.mrbast.dadagui.api.layout.StorageLayoutGui;
import me.mrbast.dadagui.api.storage.StorageProvider;
import me.mrbast.dadagui.api.storage.StorageSaveHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Fluent builder for storage/vault GUIs.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public final class StorageGuiBuilder<C, I> {
    private final char storageMarker;
    private Function<C, String> titleProvider = viewer -> "";
    private GuiLayout layout;
    private final Map<Character, GuiIngredient<C, I>> ingredients = new LinkedHashMap<>();
    private StorageProvider<C, I> storageProvider;
    private StorageSaveHandler<C, I> saveHandler = (session, storage) -> { };
    private GuiScope scope = GuiScope.PER_PLAYER;
    private String sharedKey;
    private String storageKey = "dadagui.storage";

    public StorageGuiBuilder(char storageMarker) {
        this.storageMarker = storageMarker;
    }

    public StorageGuiBuilder<C, I> title(String title) {
        this.titleProvider = viewer -> title == null ? "" : title;
        return this;
    }

    public StorageGuiBuilder<C, I> title(Function<C, String> titleProvider) {
        this.titleProvider = titleProvider == null ? viewer -> "" : titleProvider;
        return this;
    }

    public StorageGuiBuilder<C, I> layout(String... rows) {
        this.layout = GuiLayout.of(rows);
        return this;
    }

    public StorageGuiBuilder<C, I> ingredient(char marker, GuiIngredient<C, I> ingredient) {
        if (ingredient == null) {
            ingredients.remove(marker);
        } else {
            ingredients.put(marker, ingredient);
        }
        return this;
    }

    public StorageGuiBuilder<C, I> storageProvider(StorageProvider<C, I> storageProvider) {
        this.storageProvider = storageProvider;
        return this;
    }

    public StorageGuiBuilder<C, I> onSave(StorageSaveHandler<C, I> saveHandler) {
        this.saveHandler = saveHandler == null ? (session, storage) -> { } : saveHandler;
        return this;
    }

    public StorageGuiBuilder<C, I> scope(GuiScope scope) {
        this.scope = scope == null ? GuiScope.PER_PLAYER : scope;
        return this;
    }

    public StorageGuiBuilder<C, I> sharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
        return this;
    }

    public StorageGuiBuilder<C, I> storageKey(String storageKey) {
        this.storageKey = storageKey == null ? "dadagui.storage" : storageKey;
        return this;
    }

    public StorageLayoutGui<C, I> build() {
        if (layout == null) {
            throw new IllegalStateException("layout is required");
        }
        if (layout.positionsOf(storageMarker).isEmpty()) {
            throw new IllegalStateException("Layout does not contain storage marker '" + storageMarker + "'");
        }
        if (storageProvider == null) {
            throw new IllegalStateException("storageProvider is required");
        }
        validateMarkers(layout.markers());
        return new StorageLayoutGui<>(storageMarker, titleProvider, layout, ingredients,
                storageProvider, saveHandler, scope, sharedKey, storageKey);
    }

    private void validateMarkers(Set<Character> markers) {
        for (Character marker : markers) {
            if (marker == storageMarker) {
                continue;
            }
            if (!ingredients.containsKey(marker)) {
                throw new IllegalStateException("Missing ingredient for layout marker '" + marker + "'");
            }
        }
    }
}
