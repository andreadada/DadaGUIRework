package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiScope;
import me.mrbast.dadagui.api.MaterialKey;
import me.mrbast.dadagui.api.builder.DadaGui;
import me.mrbast.dadagui.api.storage.SimpleStorageContainer;
import me.mrbast.dadagui.bukkit.ingredient.BukkitIngredients;
import me.mrbast.dadagui.bukkit.ingredient.BukkitNavigation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Cohesive factory for storage/vault GUI examples.
 */
public final class ExampleVaultGuis {
    private final BukkitIngredients ingredients;
    private final BukkitNavigation navigation;
    private final ExampleVaultRepository repository;

    public ExampleVaultGuis(BukkitIngredients ingredients,
                            BukkitNavigation navigation,
                            ExampleVaultRepository repository) {
        this.ingredients = ingredients;
        this.navigation = navigation;
        this.repository = repository;
    }

    /**
     * Personal vault: only the 'v' slots are mutable storage slots.
     */
    public Gui<Player, ItemStack> personalVault() {
        return DadaGui.<Player, ItemStack>storage('v')
                .title(player -> "§8Vault | " + player.getName())
                .layout(
                        "# # # # # # # # #",
                        "# v v v v v v v #",
                        "# v v v v v v v #",
                        "# v v v v v v v #",
                        "# # # I # C # # #")
                .ingredient('#', ingredients.filler(MaterialKey.BLACK_STAINED_GLASS_PANE))
                .ingredient('I', ingredients.display(MaterialKey.BOOK, "§eCome funziona",
                        "§7Solo gli slot con marker §fv §7sono storage.",
                        "§7Decorazioni e bottoni sono protetti."))
                .ingredient('C', navigation.close())
                .storageProvider(context -> repository.personal(context.viewer().getUniqueId()))
                .onSave((session, storage) -> repository.savePersonal(
                        session.viewer().getUniqueId(),
                        (SimpleStorageContainer<ItemStack>) storage))
                .scope(GuiScope.PER_PLAYER)
                .storageKey("examples:personal-vault")
                .build();
    }

    /**
     * Shared vault: every viewer sees and modifies the same backing storage.
     */
    public Gui<Player, ItemStack> sharedVault() {
        return DadaGui.<Player, ItemStack>storage('v')
                .title("§8Team Vault")
                .layout(
                        "# # # # # # # # #",
                        "# v v v v v v v #",
                        "# v v v v v v v #",
                        "# v v v v v v v #",
                        "# # # R # C # # #")
                .ingredient('#', ingredients.filler(MaterialKey.GRAY_STAINED_GLASS_PANE))
                .ingredient('R', ingredients.clickable(MaterialKey.EMERALD, "§aRefresh viewers", click -> {
                    click.refreshAllViewers();
                    click.viewer().sendMessage("§aTeam vault refreshed for all viewers.");
                }, "§7Useful when multiple players are watching."))
                .ingredient('C', navigation.close())
                .storageProvider(context -> repository.shared("team-demo"))
                .onSave((session, storage) -> repository.saveShared(
                        "team-demo",
                        (SimpleStorageContainer<ItemStack>) storage))
                .scope(GuiScope.SHARED)
                .sharedKey("examples:team-vault")
                .storageKey("examples:team-vault-storage")
                .build();
    }
}
