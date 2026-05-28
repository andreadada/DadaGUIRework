package me.mrbast.dadagui.api.pagination;

import me.mrbast.dadagui.api.ClickContext;
import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Generic paginated GUI implementation independent from Bukkit/Paper.
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 * @param <T> data item type
 */
public final class PaginatedGui<C, I, T> implements Gui<C, I> {
    private final String title;
    private final int size;
    private final List<Integer> contentSlots;
    private final PageDataProvider<C, T> dataProvider;
    private final PageItemFactory<C, I, T> itemFactory;
    private final Integer previousSlotIndex;
    private final Integer nextSlotIndex;
    private final PageControlFactory<C, I> previousControlFactory;
    private final PageControlFactory<C, I> nextControlFactory;
    private final GuiSlot<C, I> emptySlot;
    private final String pageAttributeKey;

    private PaginatedGui(Builder<C, I, T> builder) {
        this.title = builder.title;
        this.size = builder.size;
        this.contentSlots = Collections.unmodifiableList(new ArrayList<>(builder.contentSlots));
        this.dataProvider = builder.dataProvider;
        this.itemFactory = builder.itemFactory;
        this.previousSlotIndex = builder.previousSlotIndex;
        this.nextSlotIndex = builder.nextSlotIndex;
        this.previousControlFactory = builder.previousControlFactory;
        this.nextControlFactory = builder.nextControlFactory;
        this.emptySlot = builder.emptySlot;
        this.pageAttributeKey = builder.pageAttributeKey;
    }

    public static <C, I, T> Builder<C, I, T> builder(String title, int size) {
        return new Builder<>(title, size);
    }

    @Override
    public String title(C viewer) {
        return title;
    }

    @Override
    public int size(C viewer) {
        return size;
    }

    @Override
    public void render(GuiRenderContext<C, I> context) {
        if (contentSlots.isEmpty()) {
            return;
        }

        List<T> items = safeItems(dataProvider.getItems(context.viewer()));
        int maxPage = Math.max(0, (items.size() - 1) / contentSlots.size());
        int page = currentPage(context, maxPage);
        int start = page * contentSlots.size();

        for (int i = 0; i < contentSlots.size(); i++) {
            int absoluteIndex = start + i;
            int slotIndex = contentSlots.get(i);
            if (absoluteIndex < items.size()) {
                context.setSlot(slotIndex, itemFactory.create(context.viewer(), items.get(absoluteIndex), absoluteIndex));
            } else if (emptySlot != null) {
                context.setSlot(slotIndex, emptySlot);
            }
        }

        if (previousSlotIndex != null && previousControlFactory != null && page > 0) {
            GuiSlot<C, I> control = previousControlFactory.create(page, maxPage);
            context.setSlot(previousSlotIndex, wrapControl(control, page - 1));
        }
        if (nextSlotIndex != null && nextControlFactory != null && page < maxPage) {
            GuiSlot<C, I> control = nextControlFactory.create(page, maxPage);
            context.setSlot(nextSlotIndex, wrapControl(control, page + 1));
        }
    }

    private int currentPage(GuiRenderContext<C, I> context, int maxPage) {
        Optional<Integer> optionalPage = context.session().getAttribute(pageAttributeKey, Integer.class);
        int page = optionalPage.orElse(0);
        page = Math.max(0, Math.min(maxPage, page));
        context.session().setAttribute(pageAttributeKey, page);
        return page;
    }

    private GuiSlot<C, I> wrapControl(GuiSlot<C, I> source, int targetPage) {
        return GuiSlot.<C, I>builder(source.item())
                .cancelClick(source.cancelClick())
                .onClick((ClickContext<C, I> context) -> {
                    context.session().setAttribute(pageAttributeKey, targetPage);
                    context.session().refresh();
                    source.click(context);
                })
                .build();
    }

    private List<T> safeItems(List<T> items) {
        return items == null ? Collections.emptyList() : items;
    }

    /**
     * Builder for paginated GUIs.
     *
     * @param <C> viewer/player type
     * @param <I> platform item type
     * @param <T> data item type
     */
    public static final class Builder<C, I, T> {
        private final String title;
        private final int size;
        private final List<Integer> contentSlots = new ArrayList<>();
        private PageDataProvider<C, T> dataProvider = viewer -> Collections.emptyList();
        private PageItemFactory<C, I, T> itemFactory;
        private Integer previousSlotIndex;
        private Integer nextSlotIndex;
        private PageControlFactory<C, I> previousControlFactory;
        private PageControlFactory<C, I> nextControlFactory;
        private GuiSlot<C, I> emptySlot;
        private String pageAttributeKey = "dadagui.page";

        private Builder(String title, int size) {
            this.title = title;
            this.size = size;
        }

        public Builder<C, I, T> contentSlots(List<Integer> slots) {
            this.contentSlots.clear();
            if (slots != null) {
                this.contentSlots.addAll(slots);
            }
            return this;
        }

        public Builder<C, I, T> contentSlotRange(int fromInclusive, int toInclusive) {
            this.contentSlots.clear();
            for (int slot = fromInclusive; slot <= toInclusive; slot++) {
                this.contentSlots.add(slot);
            }
            return this;
        }

        public Builder<C, I, T> dataProvider(PageDataProvider<C, T> dataProvider) {
            this.dataProvider = dataProvider;
            return this;
        }

        public Builder<C, I, T> itemFactory(PageItemFactory<C, I, T> itemFactory) {
            this.itemFactory = itemFactory;
            return this;
        }

        public Builder<C, I, T> previousControl(int slotIndex, PageControlFactory<C, I> factory) {
            this.previousSlotIndex = slotIndex;
            this.previousControlFactory = factory;
            return this;
        }

        public Builder<C, I, T> nextControl(int slotIndex, PageControlFactory<C, I> factory) {
            this.nextSlotIndex = slotIndex;
            this.nextControlFactory = factory;
            return this;
        }

        public Builder<C, I, T> emptySlot(GuiSlot<C, I> emptySlot) {
            this.emptySlot = emptySlot;
            return this;
        }

        public Builder<C, I, T> pageAttributeKey(String pageAttributeKey) {
            this.pageAttributeKey = pageAttributeKey;
            return this;
        }

        public PaginatedGui<C, I, T> build() {
            if (itemFactory == null) {
                throw new IllegalStateException("itemFactory is required");
            }
            return new PaginatedGui<>(this);
        }
    }
}
