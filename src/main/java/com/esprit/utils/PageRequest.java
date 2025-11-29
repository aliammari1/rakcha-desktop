package com.esprit.utils;

/**
 * Utility class for pagination parameters.
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class PageRequest {
    private final int page;
    private final int size;
    private final String sortBy;
    private final String sortDirection;

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
    public static final String DEFAULT_SORT_DIRECTION = "ASC";

    public PageRequest(int page, int size) {
        this(page, size, null, DEFAULT_SORT_DIRECTION);
    }


    public PageRequest(int page, int size, String sortBy, String sortDirection) {
        this.page = Math.max(0, page);
        this.size = Math.min(Math.max(1, size), MAX_SIZE);
        this.sortBy = sortBy;
        this.sortDirection = sortDirection != null &&
                ("DESC".equalsIgnoreCase(sortDirection) || "ASC".equalsIgnoreCase(sortDirection))
                        ? sortDirection.toUpperCase()
                        : DEFAULT_SORT_DIRECTION;
    }


    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size);
    }


    public static PageRequest of(int page, int size, String sortBy, String sortDirection) {
        return new PageRequest(page, size, sortBy, sortDirection);
    }


    public static PageRequest defaultPage() {
        return new PageRequest(DEFAULT_PAGE, DEFAULT_SIZE);
    }


    public int getPage() {
        return page;
    }


    public int getSize() {
        return size;
    }


    public String getSortBy() {
        return sortBy;
    }


    public String getSortDirection() {
        return sortDirection;
    }


    public long getOffset() {
        return (long) page * size;
    }


    public boolean hasSorting() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }

}

