package me.mrbast.dadagui.bukkit.ingredient;

import me.mrbast.dadagui.api.ClickContext;
import me.mrbast.dadagui.api.GuiRenderContext;
import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.api.MaterialKey;
import me.mrbast.dadagui.api.SlotType;
import me.mrbast.dadagui.api.behavior.SlotBehavior;
import me.mrbast.dadagui.api.layout.GuiIngredient;
import me.mrbast.dadagui.bukkit.version.BukkitVersionAdapter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Bukkit implementation of reusable GUI ingredients.
 *
 * <p>This class is a small factory/facade for common ingredients. It does not
 * decide platform event behavior directly: it creates {@link GuiSlot}s composed
 * with slot behaviors, keeping the Bukkit runtime and the API loosely coupled.</p>
 */
public final class BukkitIngredients {
    private final BukkitVersionAdapter adapter;

    public BukkitIngredients(BukkitVersionAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Decorative filler. It cannot be moved or clicked.
     */
    public GuiIngredient<Player, ItemStack> filler(MaterialKey key) {
        return (context, slotIndex) -> GuiSlot.<Player, ItemStack>builder(stack(key, 1, " "))
                .filler()
                .build();
    }

    /**
     * Explicit locked slot. Useful for empty areas that must reject item placement.
     */
    public GuiIngredient<Player, ItemStack> locked() {
        return (context, slotIndex) -> GuiSlot.<Player, ItemStack>builder(null)
                .locked()
                .build();
    }

    /**
     * Read-only visual ingredient.
     */
    public GuiIngredient<Player, ItemStack> display(MaterialKey key, String name, String... lore) {
        return display(key, name, Arrays.asList(lore));
    }

    /**
     * Read-only visual ingredient.
     */
    public GuiIngredient<Player, ItemStack> display(MaterialKey key, String name, List<String> lore) {
        ItemStack item = stack(key, 1, name, lore);
        return (context, slotIndex) -> GuiSlot.<Player, ItemStack>builder(item.clone())
                .display()
                .build();
    }

    /**
     * Clickable button ingredient.
     */
    public GuiIngredient<Player, ItemStack> clickable(MaterialKey key,
                                                      String name,
                                                      Consumer<ClickContext<Player, ItemStack>> onClick,
                                                      String... lore) {
        return clickable(key, name, Arrays.asList(lore), onClick);
    }

    /**
     * Clickable button ingredient.
     */
    public GuiIngredient<Player, ItemStack> clickable(MaterialKey key,
                                                      String name,
                                                      List<String> lore,
                                                      Consumer<ClickContext<Player, ItemStack>> onClick) {
        ItemStack item = stack(key, 1, name, lore);
        return rawClickable(item, onClick);
    }

    /**
     * Uses an already built item as a button.
     */
    public GuiIngredient<Player, ItemStack> rawClickable(ItemStack item,
                                                         Consumer<ClickContext<Player, ItemStack>> onClick) {
        return (context, slotIndex) -> item == null ? null : GuiSlot.<Player, ItemStack>builder(item.clone())
                .button()
                .onClick(click -> {
                    if (onClick != null) {
                        onClick.accept(click);
                    }
                })
                .build();
    }

    /**
     * Uses an already built item with a builder preset. This exists for concise
     * migration from enum-based code; custom behavior should use the overload
     * accepting {@link SlotBehavior}.
     */
    public GuiIngredient<Player, ItemStack> rawClickable(ItemStack item,
                                                         Consumer<ClickContext<Player, ItemStack>> onClick,
                                                         SlotType preset) {
        return (context, slotIndex) -> item == null ? null : GuiSlot.<Player, ItemStack>builder(item.clone())
                .type(preset == null ? SlotType.BUTTON : preset)
                .onClick(click -> {
                    if (onClick != null) {
                        onClick.accept(click);
                    }
                })
                .build();
    }

    /**
     * Uses an already built item with an explicit custom behavior.
     */
    public GuiIngredient<Player, ItemStack> raw(ItemStack item, SlotBehavior<Player, ItemStack> behavior) {
        return (context, slotIndex) -> item == null ? null : GuiSlot.<Player, ItemStack>builder(item.clone())
                .clearBehaviors()
                .behavior(behavior)
                .build();
    }

    /**
     * Runtime item ingredient. The slot is display-only by default.
     */
    public GuiIngredient<Player, ItemStack> dynamic(Function<GuiRenderContext<Player, ItemStack>, ItemStack> itemFactory) {
        return dynamic(itemFactory, SlotType.DISPLAY);
    }

    /**
     * Runtime item ingredient with a builder preset.
     */
    public GuiIngredient<Player, ItemStack> dynamic(Function<GuiRenderContext<Player, ItemStack>, ItemStack> itemFactory,
                                                    SlotType preset) {
        return (context, slotIndex) -> {
            ItemStack item = itemFactory == null ? null : itemFactory.apply(context);
            return item == null ? null : GuiSlot.<Player, ItemStack>builder(item)
                    .type(preset == null ? SlotType.DISPLAY : preset)
                    .build();
        };
    }

    /**
     * Runtime item ingredient with an explicit custom behavior.
     */
    public GuiIngredient<Player, ItemStack> dynamic(Function<GuiRenderContext<Player, ItemStack>, ItemStack> itemFactory,
                                                    SlotBehavior<Player, ItemStack> behavior) {
        return (context, slotIndex) -> {
            ItemStack item = itemFactory == null ? null : itemFactory.apply(context);
            return item == null ? null : GuiSlot.<Player, ItemStack>builder(item)
                    .clearBehaviors()
                    .behavior(behavior)
                    .build();
        };
    }

    public GuiIngredient<Player, ItemStack> dynamicSlot(Function<GuiRenderContext<Player, ItemStack>, GuiSlot<Player, ItemStack>> slotFactory) {
        return (context, slotIndex) -> slotFactory == null ? null : slotFactory.apply(context);
    }

    /**
     * Read-only fixed item.
     */
    public GuiIngredient<Player, ItemStack> fixed(ItemStack item) {
        return fixed(item, SlotType.DISPLAY);
    }

    /**
     * Fixed item with a builder preset.
     */
    public GuiIngredient<Player, ItemStack> fixed(ItemStack item, SlotType preset) {
        return (context, slotIndex) -> item == null ? null : GuiSlot.<Player, ItemStack>builder(item.clone())
                .type(preset == null ? SlotType.DISPLAY : preset)
                .build();
    }

    /**
     * Fixed item with explicit custom behavior.
     */
    public GuiIngredient<Player, ItemStack> fixed(ItemStack item, SlotBehavior<Player, ItemStack> behavior) {
        return (context, slotIndex) -> item == null ? null : GuiSlot.<Player, ItemStack>builder(item.clone())
                .clearBehaviors()
                .behavior(behavior)
                .build();
    }

    /**
     * Supplied item. The slot is display-only by default.
     */
    public GuiIngredient<Player, ItemStack> supplied(Supplier<ItemStack> supplier) {
        return supplied(supplier, SlotType.DISPLAY);
    }

    /**
     * Supplied item with a builder preset.
     */
    public GuiIngredient<Player, ItemStack> supplied(Supplier<ItemStack> supplier, SlotType preset) {
        return (context, slotIndex) -> {
            ItemStack item = supplier == null ? null : supplier.get();
            return item == null ? null : GuiSlot.<Player, ItemStack>builder(item)
                    .type(preset == null ? SlotType.DISPLAY : preset)
                    .build();
        };
    }

    public ItemStack stack(MaterialKey key, int amount, String name, String... lore) {
        return stack(key, amount, name, Arrays.asList(lore));
    }

    public ItemStack stack(MaterialKey key, int amount, String name, List<String> lore) {
        ItemStack item = adapter.item(key, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(name);
            }
            if (lore != null && !lore.isEmpty()) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
