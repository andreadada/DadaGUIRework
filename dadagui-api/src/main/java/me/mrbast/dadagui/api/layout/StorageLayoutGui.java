package me.mrbast.dadagui.api.layout;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.api.storage.DefaultStorageBinding;
import me.mrbast.dadagui.api.storage.StorageContainer;
import me.mrbast.dadagui.api.storage.StorageProvider;
import me.mrbast.dadagui.api.storage.StorageSaveHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Layout-based storage GUI for vaults and editable virtual inventories.
 *
 * <p>Only slots marked by {@code storageMarker} become mutable. Every other
 * marker keeps its own ingredient behavior, so a single layout can mix filler,
 * buttons, display-only slots and storage slots.</p>
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public final class StorageLayoutGui<C, I> implements Gui<C, I> {
    private final char storageMarker;
    private final Function<C, String> titleProvider;
    private final GuiLayout layout;
    private final Map<Character, GuiIngredient<C, I>> ingredients;
    private final StorageProvider<C, I> storageProvider;
    private final StorageSaveHandler<C, I> saveHandler;
    private final GuiScope scope;
    private final String sharedKey;
    private final String storageKey;
    private final List<Integer> storageSlots;

    public StorageLayoutGui(char storageMarker,
                            Function<C, String> titleProvider,
                            GuiLayout layout,
                            Map<Character, GuiIngredient<C, I>> ingredients,
                            StorageProvider<C, I> storageProvider,
                            StorageSaveHandler<C, I> saveHandler,
                            GuiScope scope,
                            String sharedKey,
                            String storageKey) {
        this.storageMarker = storageMarker;
        this.titleProvider = titleProvider;
        this.layout = layout;
        this.ingredients = Collections.unmodifiableMap(new LinkedHashMap<>(ingredients));
        this.storageProvider = storageProvider;
        this.saveHandler = saveHandler == null ? (session, storage) -> { } : saveHandler;
        this.scope = scope == null ? GuiScope.PER_PLAYER : scope;
        this.sharedKey = sharedKey;
        this.storageKey = storageKey == null ? "dadagui.storage" : storageKey;
        this.storageSlots = Collections.unmodifiableList(new ArrayList<>(layout.positionsOf(storageMarker)));
    }

    @Override
    public String title(C viewer) {
        return titleProvider.apply(viewer);
    }

    @Override
    public int size(C viewer) {
        return layout.size();
    }

    @Override
    public void render(GuiRenderContext<C, I> context) {
        StorageContainer<I> storage = storageProvider.open(context);
        if (storage == null) {
            throw new IllegalStateException("storageProvider returned null");
        }
        String resolvedStorageId = storageKey + "." + System.identityHashCode(storage);

        for (int i = 0; i < storageSlots.size(); i++) {
            int slot = storageSlots.get(i);
            if (i >= storage.size()) {
                continue;
            }
            GuiSlot<C, I> storageSlot = GuiSlot.<C, I>builder(storage.getItem(i))
                    .storage(new DefaultStorageBinding<>(resolvedStorageId, storage, i, saveHandler))
                    .build();
            context.setSlot(slot, storageSlot);
        }

        for (int slot = 0; slot < layout.size(); slot++) {
            char marker = layout.markerAt(slot);
            if (marker == storageMarker) {
                continue;
            }
            GuiIngredient<C, I> ingredient = ingredients.get(marker);
            if (ingredient == null) {
                continue;
            }
            GuiSlot<C, I> rendered = ingredient.render(context, slot);
            if (rendered != null) {
                context.setSlot(slot, rendered);
            }
        }
    }

    @Override
    public GuiScope scope() {
        return scope;
    }

    @Override
    public String sharedKey() {
        return sharedKey == null ? Gui.super.sharedKey() : sharedKey;
    }
}
