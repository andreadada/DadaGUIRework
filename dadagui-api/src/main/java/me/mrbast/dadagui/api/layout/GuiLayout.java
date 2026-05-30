package me.mrbast.dadagui.api.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Immutable 9-column inventory layout based on marker characters.
 */
public final class GuiLayout {
    public static final int COLUMNS = 9;
    public static final int MAX_ROWS = 6;

    private final List<String> rows;

    private GuiLayout(List<String> rows) {
        this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
    }

    /**
     * Creates a layout from human-readable rows. Spaces are ignored so both
     * {@code "#########"} and {@code "# # # # # # # # #"} are valid.
     *
     * @param rows layout rows
     * @return parsed layout
     */
    public static GuiLayout of(String... rows) {
        if (rows == null || rows.length == 0) {
            throw new IllegalArgumentException("A GUI layout requires at least one row.");
        }
        if (rows.length > MAX_ROWS) {
            throw new IllegalArgumentException("A GUI layout can have at most " + MAX_ROWS + " rows.");
        }

        List<String> normalized = new ArrayList<>();
        for (int i = 0; i < rows.length; i++) {
            String row = normalize(rows[i]);
            if (row.length() != COLUMNS) {
                throw new IllegalArgumentException("Layout row " + i + " must contain exactly " + COLUMNS + " markers after spaces are removed: " + row);
            }
            normalized.add(row);
        }
        return new GuiLayout(normalized);
    }

    private static String normalize(String row) {
        if (row == null) {
            return "";
        }
        return row.replace(" ", "").replace("\t", "");
    }

    /**
     * @return number of rows
     */
    public int rows() {
        return rows.size();
    }

    /**
     * @return inventory size represented by this layout
     */
    public int size() {
        return rows.size() * COLUMNS;
    }

    /**
     * @return immutable row list without separator spaces
     */
    public List<String> rowDefinitions() {
        return rows;
    }

    /**
     * Returns the marker at the given slot.
     *
     * @param slot slot index
     * @return marker character
     */
    public char markerAt(int slot) {
        if (slot < 0 || slot >= size()) {
            throw new IndexOutOfBoundsException("Slot out of layout bounds: " + slot);
        }
        return rows.get(slot / COLUMNS).charAt(slot % COLUMNS);
    }

    /**
     * Finds every slot containing the given marker.
     *
     * @param marker marker to search
     * @return slot indexes
     */
    public List<Integer> positionsOf(char marker) {
        List<Integer> slots = new ArrayList<>();
        for (int slot = 0; slot < size(); slot++) {
            if (markerAt(slot) == marker) {
                slots.add(slot);
            }
        }
        return slots;
    }

    /**
     * @return markers used by the layout, preserving first-seen order
     */
    public Set<Character> markers() {
        Set<Character> markers = new LinkedHashSet<>();
        for (int slot = 0; slot < size(); slot++) {
            markers.add(markerAt(slot));
        }
        return markers;
    }
}
