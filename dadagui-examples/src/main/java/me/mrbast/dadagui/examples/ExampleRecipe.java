package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.MaterialKey;

/**
 * Small domain object used by the item-oriented entry examples.
 */
public final class ExampleRecipe {
    private final String key;
    private final String displayName;
    private final MaterialKey icon;
    private final int difficulty;

    public ExampleRecipe(String key, String displayName, MaterialKey icon, int difficulty) {
        this.key = key;
        this.displayName = displayName;
        this.icon = icon;
        this.difficulty = difficulty;
    }

    public String key() {
        return key;
    }

    public String displayName() {
        return displayName;
    }

    public MaterialKey icon() {
        return icon;
    }

    public int difficulty() {
        return difficulty;
    }
}
