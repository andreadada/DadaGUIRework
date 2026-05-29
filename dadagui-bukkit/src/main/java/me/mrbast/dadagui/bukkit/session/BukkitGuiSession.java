package me.mrbast.dadagui.bukkit.session;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiSession;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.bukkit.BukkitGuiManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Bukkit implementation of a GUI session.
 */
public final class BukkitGuiSession implements GuiSession<Player, ItemStack> {
    private final BukkitGuiManager manager;
    private final Player viewer;
    private final Gui<Player, ItemStack> gui;
    private final Map<Integer, GuiSlot<Player, ItemStack>> bindings = new HashMap<>();
    private final Map<String, Object> attributes;
    private Inventory inventory;

    public BukkitGuiSession(BukkitGuiManager manager, Player viewer, Gui<Player, ItemStack> gui) {
        this(manager, viewer, gui, new HashMap<>());
    }

    public BukkitGuiSession(BukkitGuiManager manager, Player viewer, Gui<Player, ItemStack> gui, Map<String, Object> attributes) {
        this.manager = manager;
        this.viewer = viewer;
        this.gui = gui;
        this.attributes = attributes == null ? new HashMap<>() : attributes;
    }

    public void attach(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory inventory() {
        return inventory;
    }

    public Map<Integer, GuiSlot<Player, ItemStack>> bindings() {
        return bindings;
    }

    public void clearBindings() {
        bindings.clear();
    }

    public void bind(int index, GuiSlot<Player, ItemStack> slot) {
        bindings.put(index, slot);
    }

    @Override
    public Player viewer() {
        return viewer;
    }

    @Override
    public Gui<Player, ItemStack> gui() {
        return gui;
    }

    @Override
    public void refresh() {
        manager.refresh(this);
    }

    @Override
    public void close() {
        viewer.closeInventory();
    }

    @Override
    public void refreshAllViewers() {
        manager.refreshAll(gui);
    }

    @Override
    public void setAttribute(String key, Object value) {
        if (value == null) {
            attributes.remove(key);
        } else {
            attributes.put(key, value);
        }
    }

    @Override
    public <T> Optional<T> getAttribute(String key, Class<T> type) {
        Object value = attributes.get(key);
        if (value == null || !type.isInstance(value)) {
            return Optional.empty();
        }
        return Optional.of(type.cast(value));
    }
}
