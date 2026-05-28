package me.mrbast.dadagui.bukkit.version;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

/**
 * SPI loaded through ServiceLoader to isolate version-specific behavior.
 */
public interface BukkitVersionAdapter {

    /**
     * @return human-readable adapter id
     */
    String id();

    /**
     * Higher priority wins when multiple adapters support the same server.
     *
     * @return adapter priority
     */
    int priority();

    /**
     * Checks whether this adapter supports the running server.
     *
     * @param server Bukkit server
     * @return true when supported
     */
    boolean supports(Server server);

    /**
     * Resolves a material safely across renamed Bukkit materials.
     *
     * @param modernName material name used by modern versions
     * @param legacyName material name used by legacy versions
     * @param fallback fallback material
     * @return resolved material
     */
    Material resolveMaterial(String modernName, String legacyName, Material fallback);

    /**
     * Creates a safe item stack using resolved material names.
     *
     * @param modernName modern material name
     * @param legacyName legacy material name
     * @param fallback fallback material
     * @param amount stack amount
     * @return item stack
     */
    default ItemStack item(String modernName, String legacyName, Material fallback, int amount) {
        return new ItemStack(resolveMaterial(modernName, legacyName, fallback), amount);
    }

    /**
     * Normalizes inventory titles for version limits.
     *
     * @param title requested title
     * @return safe title
     */
    default String normalizeInventoryTitle(String title) {
        return title == null ? "" : title;
    }
}
