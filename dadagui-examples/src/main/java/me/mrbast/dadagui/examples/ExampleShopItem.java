package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.MaterialKey;

/**
 * Immutable DTO used by the shop examples.
 */
public final class ExampleShopItem {
    private final String id;
    private final String category;
    private final String displayName;
    private final MaterialKey icon;
    private final int price;

    public ExampleShopItem(String id, String category, String displayName, MaterialKey icon, int price) {
        this.id = id;
        this.category = category;
        this.displayName = displayName;
        this.icon = icon;
        this.price = price;
    }

    public String id() {
        return id;
    }

    public String category() {
        return category;
    }

    public String displayName() {
        return displayName;
    }

    public MaterialKey icon() {
        return icon;
    }

    public int price() {
        return price;
    }
}
