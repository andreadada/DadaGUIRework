package me.mrbast.dadagui.bukkit.ingredient;

import me.mrbast.dadagui.api.GuiSlot;
import me.mrbast.dadagui.api.MaterialKey;
import me.mrbast.dadagui.api.layout.GuiIngredient;
import me.mrbast.dadagui.api.layout.PageAttributes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Ready-to-use Bukkit navigation ingredients.
 */
public final class BukkitNavigation {
    private final BukkitIngredients ingredients;

    public BukkitNavigation(BukkitIngredients ingredients) {
        this.ingredients = ingredients;
    }

    public GuiIngredient<Player, ItemStack> previousPage() {
        return (context, slotIndex) -> {
            boolean enabled = context.session().getAttribute(PageAttributes.HAS_PREVIOUS, Boolean.class).orElse(false);
            int page = context.session().getAttribute(PageAttributes.CURRENT_PAGE, Integer.class).orElse(0);
            int maxPage = context.session().getAttribute(PageAttributes.MAX_PAGE, Integer.class).orElse(0);
            return GuiSlot.<Player, ItemStack>builder(ingredients.stack(
                            enabled ? MaterialKey.RED_STAINED_GLASS_PANE : MaterialKey.GRAY_STAINED_GLASS_PANE,
                            1,
                            enabled ? "§cPagina precedente" : "§8Nessuna pagina precedente",
                            "§7Pagina corrente: §f" + (page + 1) + "§7/§f" + (maxPage + 1)))
                    .button()
                    .build();
        };
    }

    public GuiIngredient<Player, ItemStack> nextPage() {
        return (context, slotIndex) -> {
            boolean enabled = context.session().getAttribute(PageAttributes.HAS_NEXT, Boolean.class).orElse(false);
            int page = context.session().getAttribute(PageAttributes.CURRENT_PAGE, Integer.class).orElse(0);
            int maxPage = context.session().getAttribute(PageAttributes.MAX_PAGE, Integer.class).orElse(0);
            return GuiSlot.<Player, ItemStack>builder(ingredients.stack(
                            enabled ? MaterialKey.GREEN_STAINED_GLASS_PANE : MaterialKey.GRAY_STAINED_GLASS_PANE,
                            1,
                            enabled ? "§aPagina successiva" : "§8Nessuna pagina successiva",
                            "§7Pagina corrente: §f" + (page + 1) + "§7/§f" + (maxPage + 1)))
                    .button()
                    .build();
        };
    }

    public GuiIngredient<Player, ItemStack> close() {
        return ingredients.clickable(MaterialKey.BARRIER, "§cChiudi", click -> click.close(), "§7Chiude questa GUI.");
    }
}
