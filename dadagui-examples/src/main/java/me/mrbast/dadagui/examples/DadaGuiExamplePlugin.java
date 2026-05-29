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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Example plugin showing the supported GUI styles:
 * static, per-player runtime, shared and paginated.
 */
public final class DadaGuiExamplePlugin extends JavaPlugin {
    private BukkitGuiManager guiManager;
    private BukkitIngredients ingredients;
    private BukkitNavigation navigation;
    private Gui<Player, ItemStack> staticMenu;
    private Gui<Player, ItemStack> sharedCounterGui;
    private final AtomicInteger sharedClicks = new AtomicInteger();

    @Override
    public void onEnable() {
        this.guiManager = new BukkitGuiManager(this);
        this.guiManager.register();
        this.ingredients = new BukkitIngredients(guiManager.versionAdapter());
        this.navigation = new BukkitNavigation(ingredients);
        this.staticMenu = createStaticMenu();
        this.sharedCounterGui = createSharedCounterGui();
        getLogger().info("DadaGUI enabled with adapter: " + guiManager.versionAdapter().id());
    }

    @Override
    public void onDisable() {
        if (guiManager != null) {
            guiManager.unregister();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can open this GUI.");
            return true;
        }

        Player player = (Player) sender;
        String mode = args.length == 0 ? "paged" : args[0].toLowerCase();

        if ("static".equals(mode)) {
            guiManager.open(player, staticMenu);
            return true;
        }
        if ("player".equals(mode) || "runtime".equals(mode)) {
            guiManager.open(player, createPerPlayerRuntimeGui());
            return true;
        }
        if ("shared".equals(mode)) {
            guiManager.open(player, sharedCounterGui);
            return true;
        }
        if ("paged".equals(mode) || "page".equals(mode)) {
            guiManager.open(player, createRecipePagedGui());
            return true;
        }

        player.sendMessage("§eDadaGUI examples:");
        player.sendMessage("§7/dadagui static §8- GUI statica");
        player.sendMessage("§7/dadagui player §8- GUI runtime per player");
        player.sendMessage("§7/dadagui shared §8- GUI condivisa tra player");
        player.sendMessage("§7/dadagui paged §8- GUI paginata");
        return true;
    }

    private Gui<Player, ItemStack> createStaticMenu() {
        return DadaGui.<Player, ItemStack>staticGui()
                .title("§8DadaGUI | Static")
                .layout(
                        "# # # # # # # # #",
                        "# S # P # H # X #",
                        "# # # # # # # # #")
                .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
                .ingredient('S', ingredients.clickable(MaterialKey.DIAMOND, "§bStatic slot", click ->
                        click.viewer().sendMessage("§aStatic slot clicked."), "§7Same item for every player."))
                .ingredient('P', ingredients.clickable(MaterialKey.PLAYER_HEAD, "§eOpen player GUI", click ->
                        guiManager.open(click.viewer(), createPerPlayerRuntimeGui()), "§7Runtime view based on you."))
                .ingredient('H', ingredients.clickable(MaterialKey.CHEST, "§6Open shared GUI", click ->
                        guiManager.open(click.viewer(), sharedCounterGui), "§7Shared state between viewers."))
                .ingredient('X', navigation.close())
                .scope(GuiScope.STATIC)
                .build();
    }

    private Gui<Player, ItemStack> createPerPlayerRuntimeGui() {
        return DadaGui.<Player, ItemStack>staticGui()
                .title(player -> "§8Profile | " + player.getName())
                .layout(
                        "# # # # # # # # #",
                        "# N # W # R # X #",
                        "# # # # # # # # #")
                .ingredient('#', ingredients.filler(MaterialKey.GRAY_STAINED_GLASS_PANE))
                .ingredient('N', ingredients.dynamicSlot(context -> GuiSlot.<Player, ItemStack>builder(
                                ingredients.stack(MaterialKey.PLAYER_HEAD, 1, "§e" + context.viewer().getName(),
                                        "§7This item is rendered at runtime."))
                        .build()))
                .ingredient('W', ingredients.dynamicSlot(context -> GuiSlot.<Player, ItemStack>builder(
                                ingredients.stack(MaterialKey.EMERALD, 1, "§aWorld", "§7" + context.viewer().getWorld().getName()))
                        .build()))
                .ingredient('R', ingredients.clickable(MaterialKey.ARROW, "§bRefresh", click -> {
                    click.viewer().sendMessage("§aRefreshing only your GUI...");
                    click.refresh();
                }, "§7Re-renders this session."))
                .ingredient('X', navigation.close())
                .scope(GuiScope.PER_PLAYER)
                .build();
    }

    private Gui<Player, ItemStack> createSharedCounterGui() {
        return DadaGui.<Player, ItemStack>staticGui()
                .title("§8DadaGUI | Shared")
                .layout(
                        "# # # # # # # # #",
                        "# # # C # R # X #",
                        "# # # # # # # # #")
                .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
                .ingredient('C', ingredients.dynamicSlot(context -> GuiSlot.<Player, ItemStack>builder(
                                ingredients.stack(MaterialKey.GOLD_INGOT, 1, "§6Shared clicks: §f" + sharedClicks.get(),
                                        "§7Click to increment for everyone."))
                        .onClick(click -> {
                            int value = sharedClicks.incrementAndGet();
                            click.viewer().sendMessage("§aShared counter is now " + value + ".");
                            click.refreshAllViewers();
                        })
                        .build()))
                .ingredient('R', ingredients.clickable(MaterialKey.ARROW, "§bRefresh everyone", click ->
                        click.refreshAllViewers(), "§7Re-renders all viewers of this GUI."))
                .ingredient('X', navigation.close())
                .scope(GuiScope.SHARED)
                .sharedKey("examples:shared-counter")
                .build();
    }

    private Gui<Player, ItemStack> createRecipePagedGui() {
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

    private List<String> recipesFor(Player player) {
        List<String> recipes = new ArrayList<>();
        List<String> base = Arrays.asList("Sword", "Pickaxe", "Axe", "Helmet", "Chestplate", "Boots", "Shield", "Bow");
        for (int i = 1; i <= 48; i++) {
            recipes.add(base.get((i - 1) % base.size()) + " Recipe " + i + " for " + player.getName());
        }
        return recipes;
    }
}
