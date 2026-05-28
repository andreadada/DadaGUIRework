package me.mrbast.dadagui.version.modern;

import me.mrbast.dadagui.bukkit.version.BukkitVersionAdapter;
import org.bukkit.Material;
import org.bukkit.Server;

/**
 * Adapter for flattened modern Bukkit versions, roughly 1.13.x - 1.21.x.
 */
public final class ModernBukkitVersionAdapter implements BukkitVersionAdapter {

    @Override
    public String id() {
        return "modern-1_13-to-1_21";
    }

    @Override
    public int priority() {
        return 50;
    }

    @Override
    public boolean supports(Server server) {
        String version = server.getBukkitVersion();
        return version.startsWith("1.13")
                || version.startsWith("1.14")
                || version.startsWith("1.15")
                || version.startsWith("1.16")
                || version.startsWith("1.17")
                || version.startsWith("1.18")
                || version.startsWith("1.19")
                || version.startsWith("1.20")
                || version.startsWith("1.21");
    }

    @Override
    public Material resolveMaterial(String modernName, String legacyName, Material fallback) {
        Material material = match(modernName);
        if (material == null) {
            material = match(legacyName);
        }
        return material == null ? fallback : material;
    }

    private Material match(String name) {
        return name == null || name.trim().isEmpty() ? null : Material.matchMaterial(name);
    }
}
