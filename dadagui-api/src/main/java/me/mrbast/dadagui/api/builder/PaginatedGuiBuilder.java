package me.mrbast.dadagui.api.builder;

import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.PageMode;
import me.mrbast.dadagui.api.layout.GuiContentProvider;
import me.mrbast.dadagui.api.layout.GuiContentRenderer;
import me.mrbast.dadagui.api.layout.GuiIngredient;
import me.mrbast.dadagui.api.layout.GuiLayout;
import me.mrbast.dadagui.api.layout.PagedLayoutGui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Fluent builder for marker-layout paginated GUIs.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 * @param <T> data item type
 */
public final class PaginatedGuiBuilder<C, I, T> {
    private final char contentMarker;
    private char previousMarker = '<';
    private char nextMarker = '>';
    private Function<C, String> titleProvider = viewer -> "";
    private GuiLayout layout;
    private final Map<Character, GuiIngredient<C, I>> ingredients = new LinkedHashMap<>();
    private GuiContentProvider<C, I, T> contentProvider = context -> java.util.Collections.emptyList();
    private GuiContentRenderer<C, I, T> contentRenderer;
    private GuiIngredient<C, I> emptyIngredient;
    private GuiScope scope = GuiScope.PER_PLAYER;
    private PageMode pageMode = PageMode.PER_PLAYER;
    private String sharedKey;
    private String pageAttributeKey = "dadagui.page";

    public PaginatedGuiBuilder(char contentMarker) {
        this.contentMarker = contentMarker;
    }

    public PaginatedGuiBuilder<C, I, T> title(String title) {
        this.titleProvider = viewer -> title == null ? "" : title;
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> title(Function<C, String> titleProvider) {
        this.titleProvider = titleProvider == null ? viewer -> "" : titleProvider;
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> layout(String... rows) {
        this.layout = GuiLayout.of(rows);
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> ingredient(char marker, GuiIngredient<C, I> ingredient) {
        if (ingredient == null) {
            ingredients.remove(marker);
        } else {
            ingredients.put(marker, ingredient);
        }
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> previousMarker(char previousMarker) {
        this.previousMarker = previousMarker;
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> nextMarker(char nextMarker) {
        this.nextMarker = nextMarker;
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> contentProvider(GuiContentProvider<C, I, T> contentProvider) {
        this.contentProvider = contentProvider == null ? context -> java.util.Collections.emptyList() : contentProvider;
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> contentRenderer(GuiContentRenderer<C, I, T> contentRenderer) {
        this.contentRenderer = contentRenderer;
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> emptyIngredient(GuiIngredient<C, I> emptyIngredient) {
        this.emptyIngredient = emptyIngredient;
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> scope(GuiScope scope) {
        this.scope = scope == null ? GuiScope.PER_PLAYER : scope;
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> pageMode(PageMode pageMode) {
        this.pageMode = pageMode == null ? PageMode.PER_PLAYER : pageMode;
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> sharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
        return this;
    }

    public PaginatedGuiBuilder<C, I, T> pageAttributeKey(String pageAttributeKey) {
        this.pageAttributeKey = pageAttributeKey;
        return this;
    }

    public PagedLayoutGui<C, I, T> build() {
        if (layout == null) {
            throw new IllegalStateException("layout is required");
        }
        if (layout.positionsOf(contentMarker).isEmpty()) {
            throw new IllegalStateException("Layout does not contain content marker '" + contentMarker + "'");
        }
        if (contentRenderer == null) {
            throw new IllegalStateException("contentRenderer is required");
        }
        validateMarkers(layout.markers());
        return new PagedLayoutGui<>(contentMarker, previousMarker, nextMarker, titleProvider, layout, ingredients,
                contentProvider, contentRenderer, emptyIngredient, scope, pageMode, sharedKey, pageAttributeKey);
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
