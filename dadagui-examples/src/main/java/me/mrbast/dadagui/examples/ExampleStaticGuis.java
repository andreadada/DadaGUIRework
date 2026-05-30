package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.api.MaterialKey;
import me.mrbast.dadagui.api.builder.DadaGui;
import me.mrbast.dadagui.bukkit.BukkitGuiManager;
import me.mrbast.dadagui.bukkit.ingredient.BukkitIngredients;
import me.mrbast.dadagui.bukkit.ingredient.BukkitNavigation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Static GUI examples.
 *
 * <p>These views are built once at plugin startup and reused for every player.
 * Runtime behavior is injected through click callbacks and custom slot behaviors.</p>
 */
public final class ExampleStaticGuis {
    private final BukkitGuiManager guiManager;
    private final BukkitIngredients ingredients;
    private final BukkitNavigation navigation;
    private Gui<Player, ItemStack> playerSettings;
    private Gui<Player, ItemStack> sharedCounter;
    private Gui<Player, ItemStack> pagedRecipes;
    private Gui<Player, ItemStack> personalVault;
    private Gui<Player, ItemStack> shopHome;

    public ExampleStaticGuis(BukkitGuiManager guiManager,
                             BukkitIngredients ingredients,
                             BukkitNavigation navigation) {
        this.guiManager = guiManager;
        this.ingredients = ingredients;
        this.navigation = navigation;
    }

    public ExampleStaticGuis links(Gui<Player, ItemStack> playerSettings,
                                   Gui<Player, ItemStack> sharedCounter,
                                   Gui<Player, ItemStack> pagedRecipes,
                                   Gui<Player, ItemStack> personalVault,
                                   Gui<Player, ItemStack> shopHome) {
        this.playerSettings = playerSettings;
        this.sharedCounter = sharedCounter;
        this.pagedRecipes = pagedRecipes;
        this.personalVault = personalVault;
        this.shopHome = shopHome;
        return this;
    }

    /**
     * Main hub: static layout, buttons point to other example GUIs.
     */
    public Gui<Player, ItemStack> mainHub() {
        return DadaGui.<Player, ItemStack>staticGui()
                .title("§8DadaGUI | Examples")
                .layout(
                        "# # # # # # # # #",
                        "# S P H V M # # #",
                        "# A # # # # # X #",
                        "# # # # # # # # #")
                .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
                .ingredient('S', ingredients.display(MaterialKey.DIAMOND, "§bStatic GUI",
                        "§7This menu is one definition reused",
                        "§7for every player."))
                .ingredient('P', ingredients.clickable(MaterialKey.PLAYER_HEAD, "§ePlayer runtime GUI", click ->
                        guiManager.open(click.viewer(), playerSettings),
                        "§7Data changes according to the viewer."))
                .ingredient('H', ingredients.clickable(MaterialKey.GOLD_INGOT, "§6Shared GUI", click ->
                        guiManager.open(click.viewer(), sharedCounter),
                        "§7Multiple players see the same state."))
                .ingredient('V', ingredients.clickable(MaterialKey.CHEST, "§aPersonal vault", click ->
                        guiManager.open(click.viewer(), personalVault),
                        "§7Only selected slots are mutable storage."))
                .ingredient('M', ingredients.clickable(MaterialKey.EMERALD, "§2Shop", click ->
                        guiManager.open(click.viewer(), shopHome),
                        "§7Category menu + paged product list."))
                .ingredient('A', ingredients.fixed(
                        ingredients.stack(MaterialKey.BOOK, 1, "§dCustom behavior",
                                "§7Composition example:",
                                "§fGuiSlot HAS-A SlotBehavior"),
                        ExampleSlotBehaviors.auditMessage("main-hub/custom-behavior")))
                .ingredient('X', navigation.close())
                .scope(GuiScope.STATIC)
                .build();
    }

    /**
     * Small static confirmation dialog. It demonstrates a static GUI with two buttons.
     */
    public Gui<Player, ItemStack> confirmResetDialog() {
        return DadaGui.<Player, ItemStack>staticGui()
                .title("§8Confirm Action")
                .layout(
                        "# # # # # # # # #",
                        "# # Y # # # N # #",
                        "# # # # # # # # #")
                .ingredient('#', ingredients.filler(MaterialKey.GRAY_STAINED_GLASS_PANE))
                .ingredient('Y', ingredients.clickable(MaterialKey.LIME_STAINED_GLASS_PANE, "§aConfirm", click -> {
                    click.viewer().sendMessage("§aConfirmed.");
                    click.close();
                }, "§7Execute the action."))
                .ingredient('N', ingredients.clickable(MaterialKey.RED_STAINED_GLASS_PANE, "§cCancel", click -> {
                    click.viewer().sendMessage("§cCancelled.");
                    guiManager.open(click.viewer(), mainHub());
                }, "§7Go back to the hub."))
                .scope(GuiScope.STATIC)
                .build();
    }
}
