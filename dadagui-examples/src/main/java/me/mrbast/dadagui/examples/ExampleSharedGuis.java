package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.api.MaterialKey;
import me.mrbast.dadagui.api.builder.DadaGui;
import me.mrbast.dadagui.bukkit.ingredient.BukkitIngredients;
import me.mrbast.dadagui.bukkit.ingredient.BukkitNavigation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared-state GUI examples.
 */
public final class ExampleSharedGuis {
    private final BukkitIngredients ingredients;
    private final BukkitNavigation navigation;
    private final AtomicInteger sharedClicks = new AtomicInteger();
    private final ExampleVoteState voteState = new ExampleVoteState();

    public ExampleSharedGuis(BukkitIngredients ingredients, BukkitNavigation navigation) {
        this.ingredients = ingredients;
        this.navigation = navigation;
    }

    /**
     * Shared counter: every viewer sees the same counter and can refresh everyone.
     */
    public Gui<Player, ItemStack> sharedCounter() {
        return DadaGui.<Player, ItemStack>staticGui()
                .title("§8DadaGUI | Shared Counter")
                .layout(
                        "# # # # # # # # #",
                        "# # # C # R # X #",
                        "# # # # # # # # #")
                .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
                .ingredient('C', ingredients.dynamicSlot(context -> GuiSlot.<Player, ItemStack>builder(
                                ingredients.stack(MaterialKey.GOLD_INGOT, 1,
                                        "§6Shared clicks: §f" + sharedClicks.get(),
                                        "§7Click to increment for every viewer."))
                        .button()
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

    /**
     * Shared voting board: players vote independently, but all viewers see the same totals.
     */
    public Gui<Player, ItemStack> voteBoard() {
        return DadaGui.<Player, ItemStack>staticGui()
                .title("§8Vote Board | Shared")
                .layout(
                        "# # # # # # # # #",
                        "# A # B # C # T #",
                        "# # # R # X # # #",
                        "# # # # # # # # #")
                .ingredient('#', ingredients.filler(MaterialKey.GRAY_STAINED_GLASS_PANE))
                .ingredient('A', voteButton("A", MaterialKey.DIAMOND))
                .ingredient('B', voteButton("B", MaterialKey.EMERALD))
                .ingredient('C', voteButton("C", MaterialKey.GOLD_INGOT))
                .ingredient('T', ingredients.dynamicSlot(context -> GuiSlot.<Player, ItemStack>builder(
                                ingredients.stack(MaterialKey.BOOK, 1,
                                        "§fTotal votes: §e" + voteState.total(),
                                        "§7A: §f" + voteState.count("A"),
                                        "§7B: §f" + voteState.count("B"),
                                        "§7C: §f" + voteState.count("C")))
                        .display()
                        .build()))
                .ingredient('R', ingredients.clickable(MaterialKey.ARROW, "§bRefresh all", click -> click.refreshAllViewers()))
                .ingredient('X', navigation.close())
                .scope(GuiScope.SHARED)
                .sharedKey("examples:vote-board")
                .build();
    }

    private me.mrbast.dadagui.api.layout.GuiIngredient<Player, ItemStack> voteButton(String option, MaterialKey icon) {
        return (context, slotIndex) -> GuiSlot.<Player, ItemStack>builder(ingredients.stack(
                        icon,
                        1,
                        "§eVote " + option,
                        "§7Current votes: §f" + voteState.count(option),
                        "§aClick to vote."))
                .button()
                .onClick(click -> {
                    voteState.vote(click.viewer().getUniqueId(), option);
                    click.viewer().sendMessage("§aYou voted " + option + ".");
                    click.refreshAllViewers();
                })
                .build();
    }
}
