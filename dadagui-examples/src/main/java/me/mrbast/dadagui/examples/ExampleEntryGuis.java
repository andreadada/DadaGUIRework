package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.MaterialKey;
import me.mrbast.dadagui.api.PageMode;
import me.mrbast.dadagui.api.builder.DadaGui;
import me.mrbast.dadagui.api.entry.GuiEntry;
import me.mrbast.dadagui.api.entry.PagedEntryGui;
import me.mrbast.dadagui.bukkit.BukkitGuiManager;
import me.mrbast.dadagui.bukkit.ingredient.BukkitIngredients;
import me.mrbast.dadagui.bukkit.ingredient.BukkitNavigation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Item-oriented examples.
 *
 * <p>This is the friendliest API when the content object should own its own
 * visual item and click action, like old AbstractItem based systems.</p>
 */
public final class ExampleEntryGuis {
    private final BukkitGuiManager guiManager;
    private final BukkitIngredients ingredients;
    private final BukkitNavigation navigation;
    private final PagedEntryGui<Player, ItemStack> recipePicker;
    private final PagedEntryGui<Player, ItemStack> quickShop;

    public ExampleEntryGuis(BukkitGuiManager guiManager,
                            BukkitIngredients ingredients,
                            BukkitNavigation navigation) {
        this.guiManager = guiManager;
        this.ingredients = ingredients;
        this.navigation = navigation;
        this.recipePicker = createRecipePicker();
        this.quickShop = createQuickShop();
    }

    /**
     * Opens a recipe picker with per-open entries and a per-open close handler.
     */
    public void openRecipePicker(Player player) {
        ExampleCraftingContext craftingContext = new ExampleCraftingContext(player);
        List<ExampleRecipe> validRecipes = recipesFor(player);

        recipePicker.open(player)
                .entries(validRecipes, (recipe, index) -> new RecipeEntry(craftingContext, recipe, ingredients))
                .onClose(session -> craftingContext.finishBusy())
                .show(guiManager);
    }

    /**
     * Opens a shop using tiny entries built from domain objects.
     */
    public void openQuickShop(Player player) {
        List<ExampleShopItem> items = Arrays.asList(
                new ExampleShopItem("stone", "utility", "Stone Pack", MaterialKey.STONE, 25),
                new ExampleShopItem("book", "utility", "Guide Book", MaterialKey.BOOK, 15),
                new ExampleShopItem("diamond", "rare", "Diamond", MaterialKey.DIAMOND, 300),
                new ExampleShopItem("emerald", "rare", "Emerald", MaterialKey.EMERALD, 180),
                new ExampleShopItem("nether_star", "rare", "Nether Star", MaterialKey.NETHER_STAR, 1200)
        );

        quickShop.open(player)
                .entries(items, (item, index) -> ingredients.entry(
                        item,
                        value -> ingredients.stack(value.icon(), 1,
                                "§f" + value.displayName(),
                                "§7Price: §e" + value.price() + " coins",
                                "§8Entry index: " + index,
                                "§aClick to buy."),
                        (click, value) -> {
                            click.viewer().sendMessage("§aBought example item: §f" + value.displayName());
                            click.close();
                        }))
                .show(guiManager);
    }

    public PagedEntryGui<Player, ItemStack> recipePicker() {
        return recipePicker;
    }

    public PagedEntryGui<Player, ItemStack> quickShop() {
        return quickShop;
    }

    private PagedEntryGui<Player, ItemStack> createRecipePicker() {
        return DadaGui.<Player, ItemStack>pagedEntries('x')
                .title("§8Pick The Recipe To Craft")
                .layout(
                        "# # # # # # # # #",
                        "# x x x x x x x #",
                        "# x x x x x x x #",
                        "# # # < # > # # C")
                .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
                .ingredient('<', navigation.previousPage())
                .ingredient('>', navigation.nextPage())
                .ingredient('C', navigation.close())
                .emptyIngredient(ingredients.display(MaterialKey.GRAY_STAINED_GLASS_PANE, " "))
                .scope(GuiScope.PER_PLAYER)
                .pageMode(PageMode.PER_PLAYER)
                .entriesAttributeKey("examples.recipe.entries")
                .pageAttributeKey("examples.recipe.page")
                .build();
    }

    private PagedEntryGui<Player, ItemStack> createQuickShop() {
        return DadaGui.<Player, ItemStack>pagedEntries('x')
                .title("§8Quick Shop | Entry API")
                .layout(
                        "# # # # # # # # #",
                        "# x x x x x x x #",
                        "# x x x x x x x #",
                        "# # # < # > # # C")
                .ingredient('#', ingredients.filler(MaterialKey.GRAY_STAINED_GLASS_PANE))
                .ingredient('<', navigation.previousPage())
                .ingredient('>', navigation.nextPage())
                .ingredient('C', navigation.close())
                .emptyIngredient(ingredients.display(MaterialKey.BLACK_STAINED_GLASS_PANE, " "))
                .scope(GuiScope.PER_PLAYER)
                .pageMode(PageMode.PER_PLAYER)
                .entriesAttributeKey("examples.shop.entries")
                .pageAttributeKey("examples.shop.entry.page")
                .build();
    }

    private List<ExampleRecipe> recipesFor(Player player) {
        List<ExampleRecipe> recipes = new ArrayList<>();
        List<MaterialKey> icons = Arrays.asList(
                MaterialKey.NETHER_STAR,
                MaterialKey.CRAFTING_TABLE,
                MaterialKey.DIAMOND,
                MaterialKey.EMERALD,
                MaterialKey.BOOK,
                MaterialKey.CHEST
        );
        for (int i = 1; i <= 36; i++) {
            MaterialKey icon = icons.get((i - 1) % icons.size());
            recipes.add(new ExampleRecipe(
                    "recipe_" + i,
                    "Recipe " + i + " for " + player.getName(),
                    icon,
                    1 + (i % 5)));
        }
        return recipes;
    }

    /**
     * Example equivalent to an old RecipeItem extends AbstractItem class.
     */
    private static final class RecipeEntry implements GuiEntry<Player, ItemStack> {
        private final ExampleCraftingContext context;
        private final ExampleRecipe recipe;
        private final BukkitIngredients ingredients;

        private RecipeEntry(ExampleCraftingContext context, ExampleRecipe recipe, BukkitIngredients ingredients) {
            this.context = context;
            this.recipe = recipe;
            this.ingredients = ingredients;
        }

        @Override
        public GuiSlot<Player, ItemStack> toSlot(GuiRenderContext<Player, ItemStack> renderContext, int slotIndex) {
            ItemStack item = ingredients.stack(recipe.icon(), 1,
                    "§f" + recipe.displayName(),
                    "§7Key: §8" + recipe.key(),
                    "§7Difficulty: §e" + recipe.difficulty(),
                    "§aClick to craft.");

            return GuiSlot.<Player, ItemStack>builder(item)
                    .button()
                    .onClick(click -> {
                        context.craft(recipe);
                        click.close();
                    })
                    .build();
        }
    }
}
