package me.mrbast.dadagui.examples;

import org.bukkit.entity.Player;

/**
 * Example runtime context similar to a crafting/session object in a real plugin.
 */
public final class ExampleCraftingContext {
    private final Player player;
    private boolean busy;

    public ExampleCraftingContext(Player player) {
        this.player = player;
        this.busy = true;
    }

    public Player player() {
        return player;
    }

    public void craft(ExampleRecipe recipe) {
        player.sendMessage("§aCrafting recipe: §f" + recipe.displayName());
        busy = false;
    }

    public void finishBusy() {
        if (busy) {
            player.sendMessage("§7Crafting selection closed.");
        }
        busy = false;
    }

    public boolean busy() {
        return busy;
    }
}
