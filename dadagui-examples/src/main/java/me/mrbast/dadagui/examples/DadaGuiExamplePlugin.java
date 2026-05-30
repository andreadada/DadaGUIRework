package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.bukkit.BukkitGuiManager;
import me.mrbast.dadagui.bukkit.ingredient.BukkitIngredients;
import me.mrbast.dadagui.bukkit.ingredient.BukkitNavigation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Example plugin showing the supported GUI families:
 * static, runtime per-player, shared, paginated, shop and storage/vault.
 */
public final class DadaGuiExamplePlugin extends JavaPlugin {
    private BukkitGuiManager guiManager;
    private BukkitIngredients ingredients;
    private BukkitNavigation navigation;

    private Gui<Player, ItemStack> mainHub;
    private Gui<Player, ItemStack> confirmDialog;
    private Gui<Player, ItemStack> playerSettings;
    private Gui<Player, ItemStack> sharedCounter;
    private Gui<Player, ItemStack> voteBoard;
    private Gui<Player, ItemStack> pagedRecipes;
    private Gui<Player, ItemStack> pagedOnlinePlayers;
    private Gui<Player, ItemStack> personalVault;
    private Gui<Player, ItemStack> sharedVault;
    private Gui<Player, ItemStack> shopHome;

    private ExampleShopGuis shopGuis;

    @Override
    public void onEnable() {
        this.guiManager = new BukkitGuiManager(this);
        this.guiManager.register();
        this.ingredients = new BukkitIngredients(guiManager.versionAdapter());
        this.navigation = new BukkitNavigation(ingredients);

        ExamplePlayerSettings settings = new ExamplePlayerSettings();
        ExampleVaultRepository vaultRepository = new ExampleVaultRepository(21);
        ExampleShopCatalog shopCatalog = new ExampleShopCatalog();

        ExamplePlayerRuntimeGuis runtimeGuis = new ExamplePlayerRuntimeGuis(ingredients, navigation, settings);
        ExampleSharedGuis sharedGuis = new ExampleSharedGuis(ingredients, navigation);
        ExamplePagedGuis pagedGuis = new ExamplePagedGuis(ingredients, navigation);
        ExampleVaultGuis vaultGuis = new ExampleVaultGuis(ingredients, navigation, vaultRepository);
        this.shopGuis = new ExampleShopGuis(guiManager, ingredients, navigation, shopCatalog);

        this.playerSettings = runtimeGuis.playerSettings();
        this.sharedCounter = sharedGuis.sharedCounter();
        this.voteBoard = sharedGuis.voteBoard();
        this.pagedRecipes = pagedGuis.recipes();
        this.pagedOnlinePlayers = pagedGuis.onlinePlayersSharedPage();
        this.personalVault = vaultGuis.personalVault();
        this.sharedVault = vaultGuis.sharedVault();
        this.shopHome = shopGuis.shopHome();

        ExampleStaticGuis staticGuis = new ExampleStaticGuis(guiManager, ingredients, navigation)
                .links(playerSettings, sharedCounter, pagedRecipes, personalVault, shopHome);
        this.mainHub = staticGuis.mainHub();
        this.confirmDialog = staticGuis.confirmResetDialog();

        getLogger().info("DadaGUI examples enabled with adapter: " + guiManager.versionAdapter().id());
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
        String mode = args.length == 0 ? "hub" : args[0].toLowerCase();

        if ("hub".equals(mode) || "menu".equals(mode)) {
            guiManager.open(player, mainHub);
            return true;
        }
        if ("static".equals(mode)) {
            guiManager.open(player, mainHub);
            return true;
        }
        if ("confirm".equals(mode)) {
            guiManager.open(player, confirmDialog);
            return true;
        }
        if ("player".equals(mode) || "runtime".equals(mode) || "settings".equals(mode)) {
            guiManager.open(player, playerSettings);
            return true;
        }
        if ("shared".equals(mode) || "counter".equals(mode)) {
            guiManager.open(player, sharedCounter);
            return true;
        }
        if ("vote".equals(mode) || "voteboard".equals(mode)) {
            guiManager.open(player, voteBoard);
            return true;
        }
        if ("paged".equals(mode) || "page".equals(mode) || "recipes".equals(mode)) {
            guiManager.open(player, pagedRecipes);
            return true;
        }
        if ("online".equals(mode) || "onlinepaged".equals(mode)) {
            guiManager.open(player, pagedOnlinePlayers);
            return true;
        }
        if ("shop".equals(mode)) {
            guiManager.open(player, shopHome);
            return true;
        }
        if ("shopblocks".equals(mode)) {
            guiManager.open(player, shopGuis.shopCategory("blocks"));
            return true;
        }
        if ("shoprare".equals(mode)) {
            guiManager.open(player, shopGuis.shopCategory("rare"));
            return true;
        }
        if ("shoputility".equals(mode)) {
            guiManager.open(player, shopGuis.shopCategory("utility"));
            return true;
        }
        if ("vault".equals(mode) || "personalvault".equals(mode)) {
            guiManager.open(player, personalVault);
            return true;
        }
        if ("teamvault".equals(mode) || "sharedvault".equals(mode)) {
            guiManager.open(player, sharedVault);
            return true;
        }

        sendHelp(player);
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage("§eDadaGUI examples:");
        player.sendMessage("§7/dadagui hub §8- example hub");
        player.sendMessage("§7/dadagui static §8- static reusable GUI");
        player.sendMessage("§7/dadagui confirm §8- confirmation dialog");
        player.sendMessage("§7/dadagui player §8- runtime per-player GUI");
        player.sendMessage("§7/dadagui shared §8- shared counter GUI");
        player.sendMessage("§7/dadagui vote §8- shared vote board");
        player.sendMessage("§7/dadagui paged §8- personal paged recipe list");
        player.sendMessage("§7/dadagui online §8- shared paged online-player list");
        player.sendMessage("§7/dadagui shop §8- category shop");
        player.sendMessage("§7/dadagui shopblocks §8- paged shop category");
        player.sendMessage("§7/dadagui vault §8- personal storage vault");
        player.sendMessage("§7/dadagui teamvault §8- shared storage vault");
    }
}
