package me.mrbast.dadagui.bukkit.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Small ItemStack builder deliberately based only on stable Bukkit API.
 */
public final class BukkitItemBuilder {
    private final ItemStack item;

    private BukkitItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
    }

    public static BukkitItemBuilder of(Material material) {
        return new BukkitItemBuilder(material, 1);
    }

    public static BukkitItemBuilder of(Material material, int amount) {
        return new BukkitItemBuilder(material, amount);
    }

    public BukkitItemBuilder name(String displayName) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return this;
    }

    public BukkitItemBuilder lore(String... lines) {
        return lore(Arrays.asList(lines));
    }

    public BukkitItemBuilder lore(List<String> lines) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(lines);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemStack build() {
        return item;
    }
}
