package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.api.MaterialKey;
import me.mrbast.dadagui.api.PageMode;
import me.mrbast.dadagui.api.builder.DadaGui;
import me.mrbast.dadagui.bukkit.BukkitGuiManager;
import me.mrbast.dadagui.bukkit.ingredient.BukkitIngredients;
import me.mrbast.dadagui.bukkit.ingredient.BukkitNavigation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Shop examples: category menu, paged products and confirmation dialog.
 */
public final class ExampleShopGuis {
    private final BukkitGuiManager guiManager;
    private final BukkitIngredients ingredients;
    private final BukkitNavigation navigation;
    private final ExampleShopCatalog catalog;

    public ExampleShopGuis(BukkitGuiManager guiManager,
                           BukkitIngredients ingredients,
                           BukkitNavigation navigation,
                           ExampleShopCatalog catalog) {
        this.guiManager = guiManager;
        this.ingredients = ingredients;
        this.navigation = navigation;
        this.catalog = catalog;
    }

    /**
     * Static shop home with category buttons.
     */
    public Gui<Player, ItemStack> shopHome() {
        return DadaGui.<Player, ItemStack>staticGui()
                .title(player -> "§8Shop | Balance " + catalog.balance(player.getUniqueId()))
                .layout(
                        "# # # # # # # # #",
                        "# B # R # U # I #",
                        "# # # # # # # X #",
                        "# # # # # # # # #")
                .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
                .ingredient('B', categoryButton("blocks", MaterialKey.CHEST, "§6Blocks"))
                .ingredient('R', categoryButton("rare", MaterialKey.DIAMOND, "§bRare"))
                .ingredient('U', categoryButton("utility", MaterialKey.BOOK, "§eUtility"))
                .ingredient('I', ingredients.dynamicSlot(context -> GuiSlot.<Player, ItemStack>builder(
                                ingredients.stack(MaterialKey.EMERALD, 1,
                                        "§aYour balance",
                                        "§f" + catalog.balance(context.viewer().getUniqueId()) + " coins"))
                        .display()
                        .build()))
                .ingredient('X', navigation.close())
                .scope(GuiScope.PER_PLAYER)
                .build();
    }

    /**
     * Paged products for one category. Same definition can be recreated cheaply when a category is selected.
     */
    public Gui<Player, ItemStack> shopCategory(String category) {
        return DadaGui.<Player, ItemStack, ExampleShopItem>paginated('x')
                .title(player -> "§8Shop | " + category + " | " + catalog.balance(player.getUniqueId()))
                .layout(
                        "# # # # # # # # #",
                        "# x x x x x x x #",
                        "# x x x x x x x #",
                        "# # # < H > # # C")
                .ingredient('#', ingredients.filler(MaterialKey.GRAY_STAINED_GLASS_PANE))
                .ingredient('<', navigation.previousPage())
                .ingredient('>', navigation.nextPage())
                .ingredient('H', ingredients.clickable(MaterialKey.BOOK, "§eBack to categories", click ->
                        guiManager.open(click.viewer(), shopHome())))
                .ingredient('C', navigation.close())
                .contentProvider(context -> catalog.itemsByCategory(category))
                .contentRenderer((context, item, index) -> ingredients.clickable(
                        item.icon(),
                        "§f" + item.displayName(),
                        click -> guiManager.open(click.viewer(), confirmPurchase(item, shopCategory(category))),
                        "§7Price: §e" + item.price() + " coins",
                        "§7Your balance: §a" + catalog.balance(context.viewer().getUniqueId()),
                        "§aClick to buy."))
                .emptyIngredient(ingredients.display(MaterialKey.BLACK_STAINED_GLASS_PANE, " "))
                .scope(GuiScope.PER_PLAYER)
                .pageMode(PageMode.PER_PLAYER)
                .pageAttributeKey("examples.shop." + category + ".page")
                .build();
    }

    /**
     * Confirmation dialog for a product. This is a normal static layout with runtime title.
     */
    public Gui<Player, ItemStack> confirmPurchase(ExampleShopItem item, Gui<Player, ItemStack> previousGui) {
        return DadaGui.<Player, ItemStack>staticGui()
                .title(player -> "§8Buy | " + item.displayName())
                .layout(
                        "# # # # # # # # #",
                        "# # Y # I # N # #",
                        "# # # # # # # # #")
                .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
                .ingredient('I', ingredients.display(item.icon(), "§f" + item.displayName(),
                        "§7Price: §e" + item.price() + " coins"))
                .ingredient('Y', ingredients.clickable(MaterialKey.LIME_STAINED_GLASS_PANE, "§aConfirm purchase", click -> {
                    boolean success = catalog.buy(click.viewer().getUniqueId(), item);
                    if (!success) {
                        click.viewer().sendMessage("§cYou do not have enough coins.");
                        guiManager.open(click.viewer(), previousGui);
                        return;
                    }
                    ItemStack reward = ingredients.stack(item.icon(), 1, "§f" + item.displayName());
                    click.viewer().getInventory().addItem(reward);
                    click.viewer().sendMessage("§aPurchased §f" + item.displayName() + "§a.");
                    guiManager.open(click.viewer(), previousGui);
                }, "§7Buy this item."))
                .ingredient('N', ingredients.clickable(MaterialKey.RED_STAINED_GLASS_PANE, "§cCancel", click ->
                        guiManager.open(click.viewer(), previousGui), "§7Return without buying."))
                .scope(GuiScope.PER_PLAYER)
                .build();
    }

    private me.mrbast.dadagui.api.layout.GuiIngredient<Player, ItemStack> categoryButton(String category,
                                                                                         MaterialKey icon,
                                                                                         String name) {
        return ingredients.clickable(icon, name, click -> guiManager.open(click.viewer(), shopCategory(category)),
                "§7Open paged product list.",
                "§7Category: §f" + category);
    }
}
