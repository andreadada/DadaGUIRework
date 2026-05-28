package me.mrbast.dadagui.bukkit.version;

import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Loads the best BukkitVersionAdapter through Java ServiceLoader.
 */
public final class BukkitVersionAdapters {
    private BukkitVersionAdapters() {
    }

    public static BukkitVersionAdapter bestFor(Server server) {
        List<BukkitVersionAdapter> adapters = new ArrayList<>();
        for (BukkitVersionAdapter adapter : ServiceLoader.load(BukkitVersionAdapter.class)) {
            adapters.add(adapter);
        }
        adapters.add(new DefaultBukkitVersionAdapter());
        adapters.sort(Comparator.comparingInt(BukkitVersionAdapter::priority).reversed());
        for (BukkitVersionAdapter adapter : adapters) {
            if (adapter.supports(server)) {
                return adapter;
            }
        }
        return new DefaultBukkitVersionAdapter();
    }
}
