package me.mrbast.dadagui.api.builder;

import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.layout.GuiIngredient;
import me.mrbast.dadagui.api.layout.GuiLayout;
import me.mrbast.dadagui.api.layout.LayoutGui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Fluent builder for marker-layout GUIs.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public final class LayoutGuiBuilder<C, I> {
    private Function<C, String> titleProvider = viewer -> "";
    private GuiLayout layout;
    private final Map<Character, GuiIngredient<C, I>> ingredients = new LinkedHashMap<>();
    private GuiScope scope = GuiScope.PER_PLAYER;
    private String sharedKey;

    public LayoutGuiBuilder<C, I> title(String title) {
        this.titleProvider = viewer -> title == null ? "" : title;
        return this;
    }

    public LayoutGuiBuilder<C, I> title(Function<C, String> titleProvider) {
        this.titleProvider = titleProvider == null ? viewer -> "" : titleProvider;
        return this;
    }

    public LayoutGuiBuilder<C, I> layout(String... rows) {
        this.layout = GuiLayout.of(rows);
        return this;
    }

    public LayoutGuiBuilder<C, I> ingredient(char marker, GuiIngredient<C, I> ingredient) {
        if (ingredient == null) {
            this.ingredients.remove(marker);
        } else {
            this.ingredients.put(marker, ingredient);
        }
        return this;
    }

    public LayoutGuiBuilder<C, I> scope(GuiScope scope) {
        this.scope = scope == null ? GuiScope.PER_PLAYER : scope;
        return this;
    }

    public LayoutGuiBuilder<C, I> sharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
        return this;
    }

    public LayoutGui<C, I> build() {
        if (layout == null) {
            throw new IllegalStateException("layout is required");
        }
        validateMarkers(layout.markers(), ingredients);
        return new LayoutGui<>(titleProvider, layout, ingredients, scope, sharedKey);
    }

    private void validateMarkers(Set<Character> markers, Map<Character, GuiIngredient<C, I>> ingredients) {
        for (Character marker : markers) {
            if (!ingredients.containsKey(marker)) {
                throw new IllegalStateException("Missing ingredient for layout marker '" + marker + "'");
            }
        }
    }
}
