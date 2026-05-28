package me.mrbast.dadagui.bukkit.session;

import me.mrbast.dadagui.api.ClickContext;
import me.mrbast.dadagui.api.GuiSession;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Bukkit click context wrapper.
 */
public final class BukkitClickContext implements ClickContext<Player, ItemStack> {
    private final BukkitGuiSession session;
    private final InventoryClickEvent event;

    public BukkitClickContext(BukkitGuiSession session, InventoryClickEvent event) {
        this.session = session;
        this.event = event;
    }

    @Override
    public GuiSession<Player, ItemStack> session() {
        return session;
    }

    @Override
    public Player viewer() {
        return session.viewer();
    }

    @Override
    public int rawSlot() {
        return event.getRawSlot();
    }

    @Override
    public boolean isLeftClick() {
        return event.isLeftClick();
    }

    @Override
    public boolean isRightClick() {
        return event.isRightClick();
    }

    @Override
    public boolean isShiftClick() {
        return event.isShiftClick();
    }

    @Override
    public Object nativeEvent() {
        return event;
    }

    public InventoryClickEvent bukkitEvent() {
        return event;
    }
}
