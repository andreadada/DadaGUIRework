package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.MaterialKey;
import me.mrbast.dadagui.api.PageMode;
import me.mrbast.dadagui.api.builder.DadaGui;
import me.mrbast.dadagui.bukkit.ingredient.BukkitIngredients;
import me.mrbast.dadagui.bukkit.ingredient.BukkitNavigation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Paginated GUI examples.
 */
public final class ExamplePagedGuis {
    private final BukkitIngredients ingredients;
    private final BukkitNavigation navigation;

    public ExamplePagedGuis(BukkitIngredients ingredients, BukkitNavigation navigation) {
        this.ingredients = ingredients;
        this.navigation = navigation;
    }

    /**
     * Personal paged recipe list. Each player receives a list rendered from their own context.
     */
    public Gui<Player, ItemStack> recipes() {
        return DadaGui.<Player, ItemStack, String>paginated('x')
                .title("§8Pick The Recipe To Craft")
                .layout(
                        "# # # # # # # # #",
                        "# x x x x x x x #",
                        "# x x x x x x x #",
                        "# x x x x x x x #",
                        "# # # < # > # # C")
                .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
                .ingredient('<', navigation.previousPage())
                .ingredient('>', navigation.nextPage())
                .ingredient('C', navigation.close())
                .contentProvider(context -> recipesFor(context.viewer()))
                .contentRenderer((context, recipe, index) -> ingredients.clickable(
                        MaterialKey.CRAFTING_TABLE,
                        "§f" + recipe,
                        click -> click.viewer().sendMessage("§aSelected recipe: §f" + recipe),
                        "§7Absolute index: §f" + index,
                        "§aClick to select."))
                .emptyIngredient(ingredients.display(MaterialKey.GRAY_STAINED_GLASS_PANE, " "))
                .scope(GuiScope.PER_PLAYER)
                .pageMode(PageMode.PER_PLAYER)
                .build();
    }

    /**
     * Shared paged online-player list. Page is shared, so everyone sees page changes together.
     */
    public Gui<Player, ItemStack> onlinePlayersSharedPage() {
        return DadaGui.<Player, ItemStack, Player>paginated('p')
                .title("§8Online Players | Shared Page")
                .layout(
                        "# # # # # # # # #",
                        "# p p p p p p p #",
                        "# p p p p p p p #",
                        "# # # < # > # # C")
                .ingredient('#', ingredients.filler(MaterialKey.GRAY_STAINED_GLASS_PANE))
                .ingredient('<', navigation.previousPage())
                .ingredient('>', navigation.nextPage())
                .ingredient('C', navigation.close())
                .contentProvider(context -> new ArrayList<Player>(Bukkit.getOnlinePlayers()))
                .contentRenderer((context, target, index) -> ingredients.clickable(
                        MaterialKey.PLAYER_HEAD,
                        "§e" + target.getName(),
                        click -> click.viewer().sendMessage("§7Player: §f" + target.getName()),
                        "§7Index: §f" + index,
                        "§7This page is shared between viewers."))
                .emptyIngredient(ingredients.display(MaterialKey.BLACK_STAINED_GLASS_PANE, " "))
                .scope(GuiScope.SHARED)
                .sharedKey("examples:online-players")
                .pageMode(PageMode.SHARED)
                .pageAttributeKey("examples.online-players.page")
                .build();
    }

    private List<String> recipesFor(Player player) {
        List<String> recipes = new ArrayList<>();
        List<String> base = Arrays.asList("Sword", "Pickaxe", "Axe", "Helmet", "Chestplate", "Boots", "Shield", "Bow");
        for (int i = 1; i <= 48; i++) {
            recipes.add(base.get((i - 1) % base.size()) + " Recipe " + i + " for " + player.getName());
        }
        return recipes;
    }
}
