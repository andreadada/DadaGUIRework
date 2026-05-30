package me.mrbast.dadagui.api.builder;

import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.PageMode;
import me.mrbast.dadagui.api.entry.PagedEntryGui;
import me.mrbast.dadagui.api.layout.GuiIngredient;
import me.mrbast.dadagui.api.layout.GuiLayout;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Builder for item-oriented paginated GUIs.
 *
 * <p>Use this when each content object should decide how to render itself and
 * how to handle clicks. It is the ergonomic layer for recipes, shop items,
 * player rows, quest entries and similar use cases.</p>
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public final class PagedEntryGuiBuilder<C, I> {
    private final char contentMarker;
    private char previousMarker = '<';
    private char nextMarker = '>';
    private Function<C, String> titleProvider = viewer -> "";
    private GuiLayout layout;
    private final Map<Character, GuiIngredient<C, I>> ingredients = new LinkedHashMap<>();
    private GuiIngredient<C, I> emptyIngredient;
    private GuiScope scope = GuiScope.PER_PLAYER;
    private PageMode pageMode = PageMode.PER_PLAYER;
    private String sharedKey;
    private String pageAttributeKey = "dadagui.page";
    private String entriesAttributeKey = "dadagui.entries";

    public PagedEntryGuiBuilder(char contentMarker) {
        this.contentMarker = contentMarker;
    }

    public PagedEntryGuiBuilder<C, I> title(String title) {
        this.titleProvider = viewer -> title == null ? "" : title;
        return this;
    }

    public PagedEntryGuiBuilder<C, I> title(Function<C, String> titleProvider) {
        this.titleProvider = titleProvider == null ? viewer -> "" : titleProvider;
        return this;
    }

    public PagedEntryGuiBuilder<C, I> layout(String... rows) {
        this.layout = GuiLayout.of(rows);
        return this;
    }

    public PagedEntryGuiBuilder<C, I> layout(GuiLayout layout) {
        this.layout = layout;
        return this;
    }

    public PagedEntryGuiBuilder<C, I> ingredient(char marker, GuiIngredient<C, I> ingredient) {
        if (ingredient == null) {
            ingredients.remove(marker);
        } else {
            ingredients.put(marker, ingredient);
        }
        return this;
    }

    public PagedEntryGuiBuilder<C, I> previousMarker(char previousMarker) {
        this.previousMarker = previousMarker;
        return this;
    }

    public PagedEntryGuiBuilder<C, I> nextMarker(char nextMarker) {
        this.nextMarker = nextMarker;
        return this;
    }

    public PagedEntryGuiBuilder<C, I> emptyIngredient(GuiIngredient<C, I> emptyIngredient) {
        this.emptyIngredient = emptyIngredient;
        return this;
    }

    public PagedEntryGuiBuilder<C, I> scope(GuiScope scope) {
        this.scope = scope == null ? GuiScope.PER_PLAYER : scope;
        return this;
    }

    public PagedEntryGuiBuilder<C, I> pageMode(PageMode pageMode) {
        this.pageMode = pageMode == null ? PageMode.PER_PLAYER : pageMode;
        return this;
    }

    public PagedEntryGuiBuilder<C, I> sharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
        return this;
    }

    public PagedEntryGuiBuilder<C, I> pageAttributeKey(String pageAttributeKey) {
        this.pageAttributeKey = pageAttributeKey;
        return this;
    }

    public PagedEntryGuiBuilder<C, I> entriesAttributeKey(String entriesAttributeKey) {
        this.entriesAttributeKey = entriesAttributeKey;
        return this;
    }

    public PagedEntryGui<C, I> build() {
        if (layout == null) {
            throw new IllegalStateException("layout is required");
        }
        if (layout.positionsOf(contentMarker).isEmpty()) {
            throw new IllegalStateException("Layout does not contain content marker '" + contentMarker + "'");
        }
        validateMarkers(layout.markers());
        return new PagedEntryGui<>(contentMarker, previousMarker, nextMarker, titleProvider, layout, ingredients,
                emptyIngredient, scope, pageMode, sharedKey, pageAttributeKey, entriesAttributeKey);
    }

    private void validateMarkers(Set<Character> markers) {
        for (Character marker : markers) {
            if (marker == contentMarker) {
                continue;
            }
            if (!ingredients.containsKey(marker)) {
                throw new IllegalStateException("Missing ingredient for layout marker '" + marker + "'");
            }
        }
    }
}
