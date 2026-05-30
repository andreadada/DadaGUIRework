package me.mrbast.dadagui.api;

/**
 * Version-neutral material keys used by platform adapters.
 *
 * <p>The API module does not depend on Bukkit's {@code Material}. The Bukkit
 * module resolves these keys at runtime according to the running server.</p>
 */
public enum MaterialKey {
    AIR("AIR", "AIR", -1, "AIR"),
    STONE("STONE", "STONE", -1, "STONE"),
    PAPER("PAPER", "PAPER", -1, "PAPER"),
    BOOK("BOOK", "BOOK", -1, "BOOK"),
    CHEST("CHEST", "CHEST", -1, "CHEST"),
    DIAMOND("DIAMOND", "DIAMOND", -1, "DIAMOND"),
    EMERALD("EMERALD", "EMERALD", -1, "EMERALD"),
    GOLD_INGOT("GOLD_INGOT", "GOLD_INGOT", -1, "GOLD_INGOT"),
    ARROW("ARROW", "ARROW", -1, "ARROW"),
    BARRIER("BARRIER", "BARRIER", -1, "STONE"),
    CRAFTING_TABLE("CRAFTING_TABLE", "WORKBENCH", -1, "WORKBENCH"),
    PLAYER_HEAD("PLAYER_HEAD", "SKULL_ITEM", 3, "SKULL_ITEM"),

    BLACK_STAINED_GLASS_PANE("BLACK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 15, "GLASS"),
    RED_STAINED_GLASS_PANE("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 14, "GLASS"),
    GREEN_STAINED_GLASS_PANE("GREEN_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 13, "GLASS"),
    LIME_STAINED_GLASS_PANE("LIME_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 5, "GLASS"),
    GRAY_STAINED_GLASS_PANE("GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 7, "GLASS"),
    WHITE_STAINED_GLASS_PANE("WHITE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", 0, "GLASS");

    private final String modernName;
    private final String legacyName;
    private final int legacyData;
    private final String fallbackName;

    MaterialKey(String modernName, String legacyName, int legacyData, String fallbackName) {
        this.modernName = modernName;
        this.legacyName = legacyName;
        this.legacyData = legacyData;
        this.fallbackName = fallbackName;
    }

    public String modernName() {
        return modernName;
    }

    public String legacyName() {
        return legacyName;
    }

    public int legacyData() {
        return legacyData;
    }

    public String fallbackName() {
        return fallbackName;
    }
}
