package me.mrbast.dadagui.version.legacy;

import me.mrbast.dadagui.api.MaterialKey;
import me.mrbast.dadagui.bukkit.version.BukkitVersionAdapter;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for pre-flattening Bukkit versions, roughly 1.8.x - 1.12.x.
 */
public final class LegacyBukkitVersionAdapter implements BukkitVersionAdapter {
    private static final Map<String, String> LEGACY_ALIASES = new HashMap<>();

    static {
        LEGACY_ALIASES.put("PLAYER_HEAD", "SKULL_ITEM");
        LEGACY_ALIASES.put("OAK_SIGN", "SIGN");
        LEGACY_ALIASES.put("WHITE_WOOL", "WOOL");
        LEGACY_ALIASES.put("BLACK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE");
        LEGACY_ALIASES.put("GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE");
        LEGACY_ALIASES.put("WHITE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE");
        LEGACY_ALIASES.put("GREEN_STAINED_GLASS_PANE", "STAINED_GLASS_PANE");
        LEGACY_ALIASES.put("LIME_STAINED_GLASS_PANE", "STAINED_GLASS_PANE");
        LEGACY_ALIASES.put("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE");
        LEGACY_ALIASES.put("CRAFTING_TABLE", "WORKBENCH");
    }

    @Override
    public String id() {
        return "legacy-1_8-to-1_12";
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public boolean supports(Server server) {
        String version = server.getBukkitVersion();
        return version.startsWith("1.8")
                || version.startsWith("1.9")
                || version.startsWith("1.10")
                || version.startsWith("1.11")
                || version.startsWith("1.12");
    }

    @Override
    public Material resolveMaterial(String modernName, String legacyName, Material fallback) {
        Material material = match(legacyName);
        if (material == null) {
            material = match(LEGACY_ALIASES.get(modernName));
        }
        if (material == null) {
            material = match(modernName);
        }
        return material == null ? fallback : material;
    }


    @Override
    @SuppressWarnings("deprecation")
    public ItemStack item(MaterialKey key, int amount) {
        Material fallback = resolveMaterial(key.fallbackName(), key.legacyName(), Material.STONE);
        Material material = resolveMaterial(key.modernName(), key.legacyName(), fallback);
        if (key.legacyData() >= 0) {
            return new ItemStack(material, amount, (short) key.legacyData());
        }
        return new ItemStack(material, amount);
    }

    @Override
    public String normalizeInventoryTitle(String title) {
        if (title == null) {
            return "";
        }
        return title.length() > 32 ? title.substring(0, 32) : title;
    }

    private Material match(String name) {
        return name == null || name.trim().isEmpty() ? null : Material.matchMaterial(name);
    }
}
