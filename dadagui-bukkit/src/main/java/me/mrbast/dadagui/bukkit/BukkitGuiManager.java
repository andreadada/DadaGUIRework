package me.mrbast.dadagui.bukkit;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.bukkit.session.BukkitClickContext;
import me.mrbast.dadagui.bukkit.session.BukkitGuiHolder;
import me.mrbast.dadagui.bukkit.session.BukkitGuiRenderContext;
import me.mrbast.dadagui.bukkit.session.BukkitGuiSession;
import me.mrbast.dadagui.bukkit.version.BukkitVersionAdapter;
import me.mrbast.dadagui.bukkit.version.BukkitVersionAdapters;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Bukkit/Spigot/Paper runtime coordinator.
 * The public API remains generic; this class only adapts it to Bukkit inventories/events.
 */
public final class BukkitGuiManager implements Listener {
    private final Plugin plugin;
    private final BukkitVersionAdapter versionAdapter;
    private final Map<UUID, BukkitGuiSession> sessions = new HashMap<>();
    private final Map<String, Map<String, Object>> sharedAttributes = new HashMap<>();
    private boolean registered;

    public BukkitGuiManager(Plugin plugin) {
        this.plugin = plugin;
        this.versionAdapter = BukkitVersionAdapters.bestFor(Bukkit.getServer());
    }

    public Plugin plugin() {
        return plugin;
    }

    public BukkitVersionAdapter versionAdapter() {
        return versionAdapter;
    }

    public void register() {
        if (!registered) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            registered = true;
        }
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        sessions.clear();
        registered = false;
    }

    public BukkitGuiSession open(Player player, Gui<Player, ItemStack> gui) {
        BukkitGuiSession previous = sessions.get(player.getUniqueId());
        if (previous != null) {
            sessions.remove(player.getUniqueId());
        }
        BukkitGuiSession session = new BukkitGuiSession(this, player, gui, attributesFor(gui));
        String title = versionAdapter.normalizeInventoryTitle(gui.title(player));
        Inventory inventory = Bukkit.createInventory(new BukkitGuiHolder(session), normalizeSize(gui.size(player)), title);
        session.attach(inventory);
        render(session);
        sessions.put(player.getUniqueId(), session);
        player.openInventory(inventory);
        gui.onOpen(session);
        return session;
    }

    public void refresh(BukkitGuiSession session) {
        if (session == null || session.inventory() == null || session.viewer() == null) {
            return;
        }
        render(session);
        session.viewer().updateInventory();
    }

    public void refreshAll(Gui<Player, ItemStack> gui) {
        if (gui == null) {
            return;
        }
        String sharedKey = gui.sharedKey();
        for (BukkitGuiSession session : sessions.values()) {
            if (session.gui().sharedKey().equals(sharedKey)) {
                refresh(session);
            }
        }
    }

    private Map<String, Object> attributesFor(Gui<Player, ItemStack> gui) {
        if (gui.scope() != GuiScope.SHARED) {
            return new HashMap<>();
        }
        Map<String, Object> attributes = sharedAttributes.get(gui.sharedKey());
        if (attributes == null) {
            attributes = new HashMap<>();
            sharedAttributes.put(gui.sharedKey(), attributes);
        }
        return attributes;
    }

    private void render(BukkitGuiSession session) {
        Inventory inventory = session.inventory();
        inventory.clear();
        session.clearBindings();
        session.gui().render(new BukkitGuiRenderContext(session, inventory));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (!(event.getInventory().getHolder() instanceof BukkitGuiHolder)) {
            return;
        }

        BukkitGuiHolder holder = (BukkitGuiHolder) event.getInventory().getHolder();
        BukkitGuiSession session = holder.session();
        int rawSlot = event.getRawSlot();

        if (rawSlot < 0 || rawSlot >= event.getInventory().getSize()) {
            return;
        }

        GuiSlot<Player, ItemStack> slot = session.bindings().get(rawSlot);
        if (slot == null) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(slot.cancelClick());
        slot.click(new BukkitClickContext(session, event));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        if (!(event.getInventory().getHolder() instanceof BukkitGuiHolder)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        BukkitGuiSession session = sessions.remove(player.getUniqueId());
        if (session != null) {
            session.gui().onClose(session);
            cleanupSharedAttributes(session.gui());
        }
    }

    private void cleanupSharedAttributes(Gui<Player, ItemStack> gui) {
        if (gui.scope() != GuiScope.SHARED) {
            return;
        }
        String sharedKey = gui.sharedKey();
        for (BukkitGuiSession session : sessions.values()) {
            if (session.gui().sharedKey().equals(sharedKey)) {
                return;
            }
        }
        sharedAttributes.remove(sharedKey);
    }

    private static int normalizeSize(int requestedSize) {
        int normalized = Math.max(9, Math.min(54, requestedSize));
        return ((normalized + 8) / 9) * 9;
    }
}
