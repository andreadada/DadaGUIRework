package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.MaterialKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Small catalog + fake balance store for the shop examples.
 *
 * <p>It is deliberately simple. The GUI code depends on this tiny abstraction,
 * not on Bukkit internals, keeping the example low-coupled.</p>
 */
public final class ExampleShopCatalog {
    private final List<ExampleShopItem> items = new ArrayList<>();
    private final Map<UUID, Integer> balances = new HashMap<>();

    public ExampleShopCatalog() {
        add("stone-pack", "blocks", "Stone Pack", MaterialKey.STONE, 25);
        add("glass-frame", "blocks", "Glass Frame", MaterialKey.WHITE_STAINED_GLASS_PANE, 35);
        add("builder-chest", "blocks", "Builder Chest", MaterialKey.CHEST, 90);

        add("diamond-token", "rare", "Diamond Token", MaterialKey.DIAMOND, 250);
        add("emerald-token", "rare", "Emerald Token", MaterialKey.EMERALD, 200);
        add("gold-bundle", "rare", "Gold Bundle", MaterialKey.GOLD_INGOT, 120);

        add("recipe-paper", "utility", "Recipe Paper", MaterialKey.PAPER, 15);
        add("guide-book", "utility", "Guide Book", MaterialKey.BOOK, 30);
        add("crafting-kit", "utility", "Crafting Kit", MaterialKey.CRAFTING_TABLE, 60);
    }

    private void add(String id, String category, String displayName, MaterialKey icon, int price) {
        items.add(new ExampleShopItem(id, category, displayName, icon, price));
    }

    public List<String> categories() {
        return Arrays.asList("blocks", "rare", "utility");
    }

    public List<ExampleShopItem> itemsByCategory(String category) {
        List<ExampleShopItem> result = new ArrayList<>();
        for (ExampleShopItem item : items) {
            if (item.category().equalsIgnoreCase(category)) {
                result.add(item);
            }
        }
        return result;
    }

    public int balance(UUID playerId) {
        Integer value = balances.get(playerId);
        if (value == null) {
            value = 500;
            balances.put(playerId, value);
        }
        return value;
    }

    public boolean buy(UUID playerId, ExampleShopItem item) {
        int current = balance(playerId);
        if (current < item.price()) {
            return false;
        }
        balances.put(playerId, current - item.price());
        return true;
    }
}
