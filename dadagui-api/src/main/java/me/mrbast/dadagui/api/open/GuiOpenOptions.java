package me.mrbast.dadagui.api.open;

import me.mrbast.dadagui.api.GuiSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Per-open data passed to the runtime when a GUI is shown.
 *
 * <p>This class exists to avoid mutating reusable GUI definitions. Runtime data
 * such as recipe lists, temporary selections and close callbacks belongs to the
 * concrete open request/session, not to the shared GUI template.</p>
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public final class GuiOpenOptions<C, I> {
    private final Map<String, Object> attributes;
    private final List<Consumer<GuiSession<C, I>>> openHandlers;
    private final List<Consumer<GuiSession<C, I>>> closeHandlers;

    private GuiOpenOptions(Builder<C, I> builder) {
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(builder.attributes));
        this.openHandlers = Collections.unmodifiableList(new ArrayList<>(builder.openHandlers));
        this.closeHandlers = Collections.unmodifiableList(new ArrayList<>(builder.closeHandlers));
    }

    /**
     * @param <C> viewer/player type
     * @param <I> platform item type
     * @return empty immutable options
     */
    public static <C, I> GuiOpenOptions<C, I> empty() {
        return GuiOpenOptions.<C, I>builder().build();
    }

    /**
     * @param <C> viewer/player type
     * @param <I> platform item type
     * @return mutable options builder
     */
    public static <C, I> Builder<C, I> builder() {
        return new Builder<>();
    }

    public Map<String, Object> attributes() {
        return attributes;
    }

    public List<Consumer<GuiSession<C, I>>> openHandlers() {
        return openHandlers;
    }

    public List<Consumer<GuiSession<C, I>>> closeHandlers() {
        return closeHandlers;
    }

    /**
     * Builder for {@link GuiOpenOptions}.
     *
     * @param <C> viewer/player type
     * @param <I> platform item type
     */
    public static final class Builder<C, I> {
        private final Map<String, Object> attributes = new LinkedHashMap<>();
        private final List<Consumer<GuiSession<C, I>>> openHandlers = new ArrayList<>();
        private final List<Consumer<GuiSession<C, I>>> closeHandlers = new ArrayList<>();

        private Builder() {
        }

        public Builder<C, I> attribute(String key, Object value) {
            if (key == null || key.trim().isEmpty()) {
                throw new IllegalArgumentException("attribute key cannot be null or empty");
            }
            if (value == null) {
                attributes.remove(key);
            } else {
                attributes.put(key, value);
            }
            return this;
        }

        public Builder<C, I> attributes(Map<String, Object> values) {
            if (values == null) {
                return this;
            }
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                attribute(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public Builder<C, I> onOpen(Consumer<GuiSession<C, I>> handler) {
            if (handler != null) {
                openHandlers.add(handler);
            }
            return this;
        }

        public Builder<C, I> onClose(Consumer<GuiSession<C, I>> handler) {
            if (handler != null) {
                closeHandlers.add(handler);
            }
            return this;
        }

        public GuiOpenOptions<C, I> build() {
            return new GuiOpenOptions<>(this);
        }
    }
}
