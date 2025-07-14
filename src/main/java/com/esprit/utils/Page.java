package com.esprit.utils;

import java.util.List;

/**
 * Utility class to represent paginated results.
 * 
 * @param <T> the type of entities in the page
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class Page<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public Page(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasNext() {
        return page < totalPages - 1;
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean isFirst() {
        return page == 0;
    }

    public boolean isLast() {
        return page == totalPages - 1;
    }

    public int getNumberOfElements() {
        return content.size();
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}
