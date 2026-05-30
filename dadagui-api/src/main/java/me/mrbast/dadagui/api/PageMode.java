package me.mrbast.dadagui.api;

/**
 * Defines how pagination state is stored when a GUI is shared.
 */
public enum PageMode {
    /**
     * Each viewer can browse pages independently.
     */
    PER_PLAYER,

    /**
     * All viewers see and change the same current page.
     */
    SHARED
}
