package me.mrbast.dadagui.version.v26_1;

import me.mrbast.dadagui.bukkit.version.BukkitVersionAdapter;
import org.bukkit.Material;
import org.bukkit.Server;

/**
 * Dedicated adapter for the 26.1.x line.
 * Keep all future 26.1-specific API calls isolated in this module.
 */
public final class Minecraft261BukkitVersionAdapter implements BukkitVersionAdapter {

    @Override
    public String id() {
        return "minecraft-26_1";
    }

    @Override
    public int priority() {
        return 200;
    }

    @Override
    public boolean supports(Server server) {
        String bukkitVersion = server.getBukkitVersion();
        String serverVersion = server.getVersion();
        return bukkitVersion.startsWith("26.1") || serverVersion.contains("26.1");
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
