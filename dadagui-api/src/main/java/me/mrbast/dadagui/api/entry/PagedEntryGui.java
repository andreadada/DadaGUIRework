package me.mrbast.dadagui.api.entry;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.api.PageMode;
import me.mrbast.dadagui.api.layout.GuiIngredient;
import me.mrbast.dadagui.api.layout.GuiLayout;
import me.mrbast.dadagui.api.layout.PagedLayoutGui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Paginated GUI optimized for item-oriented entries.
 *
 * <p>The GUI definition is immutable and reusable. The concrete entries are
 * passed through {@link PagedEntryOpenRequest} / {@code GuiOpenOptions} for one
 * opening, preventing state leaks between players.</p>
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public final class PagedEntryGui<C, I> implements Gui<C, I> {
    private final String entriesAttributeKey;
    private final PagedLayoutGui<C, I, GuiEntry<C, I>> delegate;

    public PagedEntryGui(char contentMarker,
                         char previousMarker,
                         char nextMarker,
                         Function<C, String> titleProvider,
                         GuiLayout layout,
                         Map<Character, GuiIngredient<C, I>> ingredients,
                         GuiIngredient<C, I> emptyIngredient,
                         GuiScope scope,
                         PageMode pageMode,
                         String sharedKey,
                         String pageAttributeKey,
                         String entriesAttributeKey) {
        this.entriesAttributeKey = entriesAttributeKey == null || entriesAttributeKey.trim().isEmpty()
                ? "dadagui.entries"
                : entriesAttributeKey;
        this.delegate = new PagedLayoutGui<>(contentMarker, previousMarker, nextMarker, titleProvider, layout, ingredients,
                context -> entriesFrom(context),
                (context, entry, index) -> new GuiIngredient<C, I>() {
                    @Override
                    public GuiSlot<C, I> render(GuiRenderContext<C, I> renderContext, int slotIndex) {
                        return entry == null ? null : entry.toSlot(renderContext, slotIndex);
                    }
                },
                emptyIngredient, scope, pageMode, sharedKey, pageAttributeKey);
    }

    public String entriesAttributeKey() {
        return entriesAttributeKey;
    }

    public PagedEntryOpenRequest<C, I> open(C viewer) {
        return new PagedEntryOpenRequest<>(this, viewer);
    }

    public PagedEntryOpenRequest<C, I> open(C viewer, Collection<? extends GuiEntry<C, I>> entries) {
        return open(viewer).entries(entries);
    }

    public <T> PagedEntryOpenRequest<C, I> open(C viewer, Collection<T> values, GuiEntryFactory<T, C, I> factory) {
        return open(viewer).entries(values, factory);
    }

    @Override
    public String title(C viewer) {
        return delegate.title(viewer);
    }

    @Override
    public int size(C viewer) {
        return delegate.size(viewer);
    }

    @Override
    public void render(GuiRenderContext<C, I> context) {
        delegate.render(context);
    }

    @Override
    public GuiScope scope() {
        return delegate.scope();
    }

    @Override
    public String sharedKey() {
        return delegate.sharedKey();
    }

    @SuppressWarnings("unchecked")
    private List<GuiEntry<C, I>> entriesFrom(GuiRenderContext<C, I> context) {
        Object value = context.session().getAttribute(entriesAttributeKey, Object.class).orElse(null);
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof List) {
            return (List<GuiEntry<C, I>>) value;
        }
        if (value instanceof Collection) {
            return new ArrayList<>((Collection<GuiEntry<C, I>>) value);
        }
        return Collections.emptyList();
    }
}
