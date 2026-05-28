package me.mrbast.dadagui.bukkit.version;

import org.bukkit.Material;
import org.bukkit.Server;

/**
 * Fallback adapter that avoids hard crashes when no specific adapter is available.
 */
public final class DefaultBukkitVersionAdapter implements BukkitVersionAdapter {

    @Override
    public String id() {
        return "default";
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean supports(Server server) {
        return true;
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
