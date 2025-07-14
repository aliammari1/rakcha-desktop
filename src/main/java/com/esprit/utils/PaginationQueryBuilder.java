package com.esprit.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility class for building paginated SQL queries.
 * 
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class PaginationQueryBuilder {

    /**
     * Builds a paginated SELECT query from a base query.
     * 
     * @param baseQuery   the base SELECT query (without ORDER BY, LIMIT, OFFSET)
     * @param pageRequest the pagination parameters
     * @return the paginated query
     */
    public static String buildPaginatedQuery(String baseQuery, PageRequest pageRequest) {
        StringBuilder query = new StringBuilder(baseQuery);

        // Add ORDER BY clause if sorting is specified
        if (pageRequest.hasSorting()) {
            query.append(" ORDER BY ").append(pageRequest.getSortBy())
                    .append(" ").append(pageRequest.getSortDirection());
        }

        // Add LIMIT and OFFSET
        query.append(" LIMIT ").append(pageRequest.getSize())
                .append(" OFFSET ").append(pageRequest.getOffset());

        return query.toString();
    }

    /**
     * Builds a COUNT query from a base query to get total elements.
     * 
     * @param baseQuery the base SELECT query
     * @return the count query
     */
    public static String buildCountQuery(String baseQuery) {
        // Extract the FROM clause and everything after it
        String lowerCaseQuery = baseQuery.toLowerCase().trim();
        int fromIndex = lowerCaseQuery.indexOf("from");

        if (fromIndex == -1) {
            throw new IllegalArgumentException("Invalid query: FROM clause not found");
        }

        String fromClause = baseQuery.substring(fromIndex);

        // Remove ORDER BY clause if present for count query
        int orderByIndex = fromClause.toLowerCase().indexOf("order by");
        if (orderByIndex != -1) {
            fromClause = fromClause.substring(0, orderByIndex);
        }

        return "SELECT COUNT(*) " + fromClause;
    }

    /**
     * Executes a count query to get the total number of elements.
     * 
     * @param connection the database connection
     * @param countQuery the count query
     * @return the total count
     * @throws SQLException if there's an error executing the query
     */
    public static long executeCountQuery(Connection connection, String countQuery) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(countQuery);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        }
    }

    /**
     * Validates a column name for sorting to prevent SQL injection.
     * 
     * @param columnName     the column name to validate
     * @param allowedColumns array of allowed column names
     * @return true if the column is allowed, false otherwise
     */
    public static boolean isValidSortColumn(String columnName, String[] allowedColumns) {
        if (columnName == null || allowedColumns == null) {
            return false;
        }

        for (String allowed : allowedColumns) {
            if (allowed.equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }
}
