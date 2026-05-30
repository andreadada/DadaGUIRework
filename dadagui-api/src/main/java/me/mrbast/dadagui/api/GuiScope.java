package me.mrbast.dadagui.api;

/**
 * Defines how a GUI stores and shares its runtime state.
 */
public enum GuiScope {
    /**
     * The GUI is structurally static and can be reused safely.
     */
    STATIC,

    /**
     * Each viewer receives an independent runtime session/state.
     */
    PER_PLAYER,

    /**
     * Multiple viewers can observe the same logical GUI state.
     */
    SHARED
}
