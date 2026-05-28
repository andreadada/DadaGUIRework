package me.mrbast.dadagui.bukkit.session;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Inventory holder used to identify DadaGUI inventories without title checks.
 */
public final class BukkitGuiHolder implements InventoryHolder {
    private final BukkitGuiSession session;

    public BukkitGuiHolder(BukkitGuiSession session) {
        this.session = session;
    }

    public BukkitGuiSession session() {
        return session;
    }

    @Override
    public Inventory getInventory() {
        return session.inventory();
    }
}
