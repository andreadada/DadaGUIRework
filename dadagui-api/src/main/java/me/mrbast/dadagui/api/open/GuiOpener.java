package me.mrbast.dadagui.api.open;

import me.mrbast.dadagui.api.Gui;
import me.mrbast.dadagui.api.GuiSession;

/**
 * Platform-neutral bridge used by fluent open requests.
 *
 * <p>The API module does not know Bukkit, Paper or any other runtime. Platform
 * modules implement this interface so higher-level GUI types can expose a
 * convenient {@code .open(viewer).show(opener)} flow without depending on the
 * platform directly.</p>
 *
 * @param <C> viewer/player type
 * @param <I> platform item type
 */
public interface GuiOpener<C, I> {

    /**
     * Opens a GUI for a viewer using default open options.
     *
     * @param viewer viewer/player
     * @param gui GUI definition
     * @return created session
     */
    GuiSession<C, I> open(C viewer, Gui<C, I> gui);

    /**
     * Opens a GUI for a viewer using per-open attributes and lifecycle hooks.
     *
     * @param viewer viewer/player
     * @param gui GUI definition
     * @param options open options
     * @return created session
     */
    GuiSession<C, I> open(C viewer, Gui<C, I> gui, GuiOpenOptions<C, I> options);
}
