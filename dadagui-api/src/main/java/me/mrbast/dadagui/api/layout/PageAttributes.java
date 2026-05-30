package me.mrbast.dadagui.api.layout;

/**
 * Well-known session attributes exposed while rendering a paginated GUI.
 */
public final class PageAttributes {
    public static final String CURRENT_PAGE = "dadagui.page.current";
    public static final String MAX_PAGE = "dadagui.page.max";
    public static final String HAS_PREVIOUS = "dadagui.page.hasPrevious";
    public static final String HAS_NEXT = "dadagui.page.hasNext";

    private PageAttributes() {
    }
}
