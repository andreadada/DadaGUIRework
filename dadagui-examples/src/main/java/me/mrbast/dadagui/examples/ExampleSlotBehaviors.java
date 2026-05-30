package me.mrbast.dadagui.examples;

import me.mrbast.dadagui.api.ClickContext;
import me.mrbast.dadagui.api.behavior.SlotBehavior;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Example of a custom behavior implemented outside the framework core.
 *
 * <p>No enum value and no framework switch are required: the slot simply has
 * this behavior in addition to its item.</p>
 */
public final class ExampleSlotBehaviors {
    private ExampleSlotBehaviors() {
    }

    public static SlotBehavior<Player, ItemStack> auditMessage(final String label) {
        return new SlotBehavior<Player, ItemStack>() {
            @Override
            public String key() {
                return "example:audit-message";
            }

            @Override
            public boolean shouldCancelClick(ClickContext<Player, ItemStack> context) {
                return true;
            }

            @Override
            public void onClick(ClickContext<Player, ItemStack> context) {
                context.viewer().sendMessage("§7Custom SlotBehavior executed: §f" + label);
            }
        };
    }
}
