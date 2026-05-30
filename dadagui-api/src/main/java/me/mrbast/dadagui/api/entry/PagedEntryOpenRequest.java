package me.mrbast.dadagui.api.entry;

import me.mrbast.dadagui.api.GuiSession;
import me.mrbast.dadagui.api.open.GuiOpenOptions;
import me.mrbast.dadagui.api.open.GuiOpener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent per-open request for item-oriented paginated GUIs.
 *
 * <p>This object is deliberately short-lived. It stores the entries for one
 * opening and then delegates the actual platform work to a {@link GuiOpener}.</p>
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public final class PagedEntryOpenRequest<C, I> {
    private final PagedEntryGui<C, I> gui;
    private final C viewer;
    private final GuiOpenOptions.Builder<C, I> optionsBuilder = GuiOpenOptions.builder();

    PagedEntryOpenRequest(PagedEntryGui<C, I> gui, C viewer) {
        this.gui = gui;
        this.viewer = viewer;
    }

    public PagedEntryOpenRequest<C, I> entries(Collection<? extends GuiEntry<C, I>> entries) {
        List<GuiEntry<C, I>> safeEntries = new ArrayList<>();
        if (entries != null) {
            safeEntries.addAll(entries);
        }
        optionsBuilder.attribute(gui.entriesAttributeKey(), safeEntries);
        return this;
    }

    public <T> PagedEntryOpenRequest<C, I> entries(Collection<T> values, GuiEntryFactory<T, C, I> factory) {
        List<GuiEntry<C, I>> entries = new ArrayList<>();
        if (values != null && factory != null) {
            int index = 0;
            for (T value : values) {
                GuiEntry<C, I> entry = factory.create(value, index);
                if (entry != null) {
                    entries.add(entry);
                }
                index++;
            }
        }
        return entries(entries);
    }

    public PagedEntryOpenRequest<C, I> attribute(String key, Object value) {
        optionsBuilder.attribute(key, value);
        return this;
    }

    public PagedEntryOpenRequest<C, I> onOpen(Consumer<GuiSession<C, I>> handler) {
        optionsBuilder.onOpen(handler);
        return this;
    }

    public PagedEntryOpenRequest<C, I> onClose(Consumer<GuiSession<C, I>> handler) {
        optionsBuilder.onClose(handler);
        return this;
    }

    public GuiOpenOptions<C, I> options() {
        return optionsBuilder.build();
    }

    public GuiSession<C, I> show(GuiOpener<C, I> opener) {
        if (opener == null) {
            throw new IllegalArgumentException("opener cannot be null");
        }
        return opener.open(viewer, gui, options());
    }
}
