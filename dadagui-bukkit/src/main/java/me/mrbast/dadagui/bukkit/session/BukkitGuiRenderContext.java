package me.mrbast.dadagui.bukkit.session;

import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiSession;
import me.mrbast.dadagui.api.GuiSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Bukkit render context backed by a native Inventory.
 */
public final class BukkitGuiRenderContext implements GuiRenderContext<Player, ItemStack> {
    private final BukkitGuiSession session;
    private final Inventory inventory;

    public BukkitGuiRenderContext(BukkitGuiSession session, Inventory inventory) {
        this.session = session;
        this.inventory = inventory;
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
    public void setSlot(int index, GuiSlot<Player, ItemStack> slot) {
        if (slot == null || index < 0 || index >= inventory.getSize()) {
            return;
        }
        inventory.setItem(index, slot.item());
        session.bind(index, slot);
    }
}
