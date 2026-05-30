package me.mrbast.dadagui.api;

/**
 * Builder preset for common slot behaviors.
 *
 * <p>This enum is intentionally not the core slot model anymore. It exists only
 * for concise builder calls and backwards compatibility. Runtime logic should
 * inspect slot behaviors instead of switching on this enum.</p>
 */
public enum SlotType {
    /** Decorative slot. */
    FILLER,

    /** Read-only visual slot. */
    DISPLAY,

    /** Action slot. */
    BUTTON,

    /** Paginated content slot. */
    CONTENT,

    /** Mutable storage slot. */
    STORAGE,

    /** Explicitly locked slot. */
    LOCKED,

    /** Custom behavior composition not represented by a built-in preset. */
    CUSTOM;

    public static SlotType fromBehaviorKey(String key) {
        if ("filler".equals(key)) {
            return FILLER;
        }
        if ("display".equals(key)) {
            return DISPLAY;
        }
        if ("button".equals(key) || "click".equals(key)) {
            return BUTTON;
        }
        if ("content".equals(key)) {
            return CONTENT;
        }
        if ("storage".equals(key)) {
            return STORAGE;
        }
        if ("locked".equals(key)) {
            return LOCKED;
        }
        return CUSTOM;
    }
}
