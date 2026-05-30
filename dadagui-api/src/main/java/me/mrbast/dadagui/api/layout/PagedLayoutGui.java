package me.mrbast.dadagui.api.layout;

import me.mrbast.dadagui.api.ClickContext;
import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.api.PageMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Layout-based paginated GUI. A dedicated marker identifies the content area;
 * every other marker can be mapped to normal ingredients or navigation controls.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 * @param <T> data item type
 */
public final class PagedLayoutGui<C, I, T> implements Gui<C, I> {
    private final char contentMarker;
    private final char previousMarker;
    private final char nextMarker;
    private final Function<C, String> titleProvider;
    private final GuiLayout layout;
    private final Map<Character, GuiIngredient<C, I>> ingredients;
    private final GuiContentProvider<C, I, T> contentProvider;
    private final GuiContentRenderer<C, I, T> contentRenderer;
    private final GuiIngredient<C, I> emptyIngredient;
    private final GuiScope scope;
    private final PageMode pageMode;
    private final String sharedKey;
    private final String pageAttributeKey;
    private final List<Integer> contentSlots;

    public PagedLayoutGui(char contentMarker,
                          char previousMarker,
                          char nextMarker,
                          Function<C, String> titleProvider,
                          GuiLayout layout,
                          Map<Character, GuiIngredient<C, I>> ingredients,
                          GuiContentProvider<C, I, T> contentProvider,
                          GuiContentRenderer<C, I, T> contentRenderer,
                          GuiIngredient<C, I> emptyIngredient,
                          GuiScope scope,
                          PageMode pageMode,
                          String sharedKey,
                          String pageAttributeKey) {
        this.contentMarker = contentMarker;
        this.previousMarker = previousMarker;
        this.nextMarker = nextMarker;
        this.titleProvider = titleProvider;
        this.layout = layout;
        this.ingredients = Collections.unmodifiableMap(new LinkedHashMap<>(ingredients));
        this.contentProvider = contentProvider;
        this.contentRenderer = contentRenderer;
        this.emptyIngredient = emptyIngredient;
        this.scope = scope == null ? GuiScope.PER_PLAYER : scope;
        this.pageMode = pageMode == null ? PageMode.PER_PLAYER : pageMode;
        this.sharedKey = sharedKey;
        this.pageAttributeKey = pageAttributeKey == null ? "dadagui.page" : pageAttributeKey;
        this.contentSlots = Collections.unmodifiableList(new ArrayList<>(layout.positionsOf(contentMarker)));
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
        if (contentSlots.isEmpty()) {
            renderFixedIngredients(context, 0, 0);
            return;
        }

        List<T> items = safeItems(contentProvider.getItems(context));
        int maxPage = Math.max(0, (items.size() - 1) / contentSlots.size());
        int page = currentPage(context, maxPage);
        int start = page * contentSlots.size();

        context.session().setAttribute(PageAttributes.CURRENT_PAGE, page);
        context.session().setAttribute(PageAttributes.MAX_PAGE, maxPage);
        context.session().setAttribute(PageAttributes.HAS_PREVIOUS, page > 0);
        context.session().setAttribute(PageAttributes.HAS_NEXT, page < maxPage);

        for (int i = 0; i < contentSlots.size(); i++) {
            int slot = contentSlots.get(i);
            int absoluteIndex = start + i;
            GuiIngredient<C, I> ingredient;
            if (absoluteIndex < items.size()) {
                ingredient = contentRenderer.render(context, items.get(absoluteIndex), absoluteIndex);
            } else {
                ingredient = emptyIngredient;
            }

            if (ingredient != null) {
                GuiSlot<C, I> rendered = ingredient.render(context, slot);
                if (rendered != null) {
                    context.setSlot(slot, rendered);
                }
            }
        }

        renderFixedIngredients(context, page, maxPage);
    }

    private void renderFixedIngredients(GuiRenderContext<C, I> context, int page, int maxPage) {
        for (int slot = 0; slot < layout.size(); slot++) {
            char marker = layout.markerAt(slot);
            if (marker == contentMarker) {
                continue;
            }
            GuiIngredient<C, I> ingredient = ingredients.get(marker);
            if (ingredient == null) {
                continue;
            }
            GuiSlot<C, I> rendered = ingredient.render(context, slot);
            if (rendered == null) {
                continue;
            }
            if (marker == previousMarker) {
                rendered = wrapPageControl(rendered, Math.max(0, page - 1), page > 0);
            } else if (marker == nextMarker) {
                rendered = wrapPageControl(rendered, Math.min(maxPage, page + 1), page < maxPage);
            }
            context.setSlot(slot, rendered);
        }
    }

    private GuiSlot<C, I> wrapPageControl(GuiSlot<C, I> source, int targetPage, boolean enabled) {
        return GuiSlot.<C, I>builder(source.item())
                .behaviors(source.behaviors())
                .onClick(context -> {
                    if (enabled) {
                        context.session().setAttribute(pageKey(context), targetPage);
                        context.session().refresh();
                    }
                })
                .build();
    }

    private int currentPage(GuiRenderContext<C, I> context, int maxPage) {
        String key = pageKey(context);
        Optional<Integer> optionalPage = context.session().getAttribute(key, Integer.class);
        int page = optionalPage.orElse(0);
        page = Math.max(0, Math.min(maxPage, page));
        context.session().setAttribute(key, page);
        return page;
    }

    private String pageKey(GuiRenderContext<C, I> context) {
        if (pageMode == PageMode.SHARED) {
            return pageAttributeKey;
        }
        return pageAttributeKey + ".viewer." + Integer.toHexString(System.identityHashCode(context.viewer()));
    }

    private String pageKey(ClickContext<C, I> context) {
        if (pageMode == PageMode.SHARED) {
            return pageAttributeKey;
        }
        return pageAttributeKey + ".viewer." + Integer.toHexString(System.identityHashCode(context.viewer()));
    }

    private List<T> safeItems(List<T> items) {
        return items == null ? Collections.emptyList() : items;
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
