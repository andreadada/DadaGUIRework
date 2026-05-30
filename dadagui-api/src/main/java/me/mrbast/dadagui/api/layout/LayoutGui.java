package me.mrbast.dadagui.api.layout;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.GuiSlot;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Layout-based GUI where marker characters are mapped to ingredients.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public final class LayoutGui<C, I> implements Gui<C, I> {
    private final Function<C, String> titleProvider;
    private final GuiLayout layout;
    private final Map<Character, GuiIngredient<C, I>> ingredients;
    private final GuiScope scope;
    private final String sharedKey;

    public LayoutGui(Function<C, String> titleProvider,
                     GuiLayout layout,
                     Map<Character, GuiIngredient<C, I>> ingredients,
                     GuiScope scope,
                     String sharedKey) {
        this.titleProvider = titleProvider;
        this.layout = layout;
        this.ingredients = Collections.unmodifiableMap(new LinkedHashMap<>(ingredients));
        this.scope = scope == null ? GuiScope.PER_PLAYER : scope;
        this.sharedKey = sharedKey;
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
        for (int slot = 0; slot < layout.size(); slot++) {
            GuiIngredient<C, I> ingredient = ingredients.get(layout.markerAt(slot));
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
