package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.api.pagination.PaginatedGui;
import me.mrbast.dadagui.bukkit.BukkitGuiManager;
import me.mrbast.dadagui.bukkit.item.BukkitItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example plugin showing runtime creation of a paginated GUI.
 */
public final class DadaGuiExamplePlugin extends JavaPlugin {
    private BukkitGuiManager guiManager;

    @Override
    public void onEnable() {
        this.guiManager = new BukkitGuiManager(this);
        this.guiManager.register();
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
        guiManager.open((Player) sender, createDemoGui());
        return true;
    }

    private Gui<Player, ItemStack> createDemoGui() {
        List<String> rows = new ArrayList<>();
        for (int i = 1; i <= 64; i++) {
            rows.add("Elemento " + i);
        }

        return PaginatedGui.<Player, ItemStack, String>builder("§8DadaGUI Demo", 54)
                .contentSlots(contentSlots())
                .dataProvider(viewer -> rows)
                .itemFactory((viewer, value, index) -> GuiSlot.<Player, ItemStack>builder(
                                BukkitItemBuilder.of(resolve("PAPER", "PAPER", Material.PAPER))
                                        .name("§f" + value)
                                        .lore(Arrays.asList("§7Indice: " + index, "§aClick per testare lo slot"))
                                        .build())
                        .onClick(context -> context.viewer().sendMessage("§aHai cliccato " + value))
                        .build())
                .emptySlot(GuiSlot.<Player, ItemStack>builder(
                        BukkitItemBuilder.of(resolve("GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE", Material.GLASS))
                                .name(" ")
                                .build()).build())
                .previousControl(45, (page, maxPage) -> GuiSlot.<Player, ItemStack>builder(
                        BukkitItemBuilder.of(resolve("ARROW", "ARROW", Material.ARROW))
                                .name("§ePagina precedente")
                                .lore("§7Pagina " + page + " di " + (maxPage + 1))
                                .build()).build())
                .nextControl(53, (page, maxPage) -> GuiSlot.<Player, ItemStack>builder(
                        BukkitItemBuilder.of(resolve("ARROW", "ARROW", Material.ARROW))
                                .name("§ePagina successiva")
                                .lore("§7Pagina " + (page + 2) + " di " + (maxPage + 1))
                                .build()).build())
                .build();
    }

    private List<Integer> contentSlots() {
        List<Integer> slots = new ArrayList<>();
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 7; col++) {
                slots.add(row * 9 + col);
            }
        }
        return slots;
    }

    private Material resolve(String modernName, String legacyName, Material fallback) {
        return guiManager.versionAdapter().resolveMaterial(modernName, legacyName, fallback);
    }
}
