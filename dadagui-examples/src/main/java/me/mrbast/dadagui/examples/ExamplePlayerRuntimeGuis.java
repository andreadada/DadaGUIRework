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

/**
 * Per-player runtime examples.
 */
public final class ExamplePlayerRuntimeGuis {
    private final BukkitIngredients ingredients;
    private final BukkitNavigation navigation;
    private final ExamplePlayerSettings settings;

    public ExamplePlayerRuntimeGuis(BukkitIngredients ingredients,
                                    BukkitNavigation navigation,
                                    ExamplePlayerSettings settings) {
        this.ingredients = ingredients;
        this.navigation = navigation;
        this.settings = settings;
    }

    /**
     * Dynamic profile/settings view. One GUI definition, different render per viewer.
     */
    public Gui<Player, ItemStack> playerSettings() {
        return DadaGui.<Player, ItemStack>staticGui()
                .title(player -> "§8Settings | " + player.getName())
                .layout(
                        "# # # # # # # # #",
                        "# N # W # B # # #",
                        "# T # S # R # X #",
                        "# # # # # # # # #")
                .ingredient('#', ingredients.filler(MaterialKey.GRAY_STAINED_GLASS_PANE))
                .ingredient('N', ingredients.dynamicSlot(context -> GuiSlot.<Player, ItemStack>builder(
                                ingredients.stack(MaterialKey.PLAYER_HEAD, 1,
                                        "§e" + context.viewer().getName(),
                                        "§7Rendered from the current viewer."))
                        .display()
                        .build()))
                .ingredient('W', ingredients.dynamicSlot(context -> GuiSlot.<Player, ItemStack>builder(
                                ingredients.stack(MaterialKey.BOOK, 1,
                                        "§bWorld",
                                        "§7" + context.viewer().getWorld().getName()))
                        .display()
                        .build()))
                .ingredient('B', ingredients.dynamicSlot(context -> GuiSlot.<Player, ItemStack>builder(
                                ingredients.stack(MaterialKey.EMERALD, 1,
                                        "§aBalance preview",
                                        "§7This could read any external service."))
                        .display()
                        .build()))
                .ingredient('T', ingredients.dynamicSlot(context -> {
                    boolean enabled = settings.notifications(context.viewer().getUniqueId());
                    return GuiSlot.<Player, ItemStack>builder(ingredients.stack(
                                    enabled ? MaterialKey.LIME_STAINED_GLASS_PANE : MaterialKey.RED_STAINED_GLASS_PANE,
                                    1,
                                    "§eNotifications: " + (enabled ? "§aON" : "§cOFF"),
                                    "§7Click to toggle only your state."))
                            .button()
                            .onClick(click -> {
                                boolean next = settings.toggleNotifications(click.viewer().getUniqueId());
                                click.viewer().sendMessage("§eNotifications: " + (next ? "§aON" : "§cOFF"));
                                click.refresh();
                            })
                            .build();
                }))
                .ingredient('S', ingredients.dynamicSlot(context -> {
                    boolean enabled = settings.sounds(context.viewer().getUniqueId());
                    return GuiSlot.<Player, ItemStack>builder(ingredients.stack(
                                    enabled ? MaterialKey.LIME_STAINED_GLASS_PANE : MaterialKey.RED_STAINED_GLASS_PANE,
                                    1,
                                    "§eSounds: " + (enabled ? "§aON" : "§cOFF"),
                                    "§7Another per-player runtime value."))
                            .button()
                            .onClick(click -> {
                                boolean next = settings.toggleSounds(click.viewer().getUniqueId());
                                click.viewer().sendMessage("§eSounds: " + (next ? "§aON" : "§cOFF"));
                                click.refresh();
                            })
                            .build();
                }))
                .ingredient('R', ingredients.clickable(MaterialKey.ARROW, "§bRefresh", click -> click.refresh(),
                        "§7Refreshes only this viewer session."))
                .ingredient('X', navigation.close())
                .scope(GuiScope.PER_PLAYER)
                .build();
    }
}
