package dev.aurelium.auraskills.common.storage.sql.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract class AbstractSqlDialect implements SqlDialect {

    private static final String[] TABLE_TYPE = {"TABLE"};

    /**
     * Renders {@code INSERT INTO table (a, b, c) VALUES (?, ?, ?)} with one placeholder per column.
     */
    protected String insertInto(String table, List<String> columns) {
        return "INSERT INTO " + table + " (" + String.join(", ", columns) + ") VALUES ("
                + String.join(", ", Collections.nCopies(columns.size(), "?")) + ")";
    }

    /**
     * Renders the assignment list shared by both upsert syntaxes, given how each dialect refers to
     * the row that was proposed for insertion.
     */
    protected String assignments(List<String> updateColumns, Map<String, String> literalUpdates,
                                 java.util.function.UnaryOperator<String> proposedValue) {
        StringBuilder builder = new StringBuilder();
        for (String column : updateColumns) {
            if (!builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(column).append(" = ").append(proposedValue.apply(column));
        }
        for (Map.Entry<String, String> entry : literalUpdates.entrySet()) {
            if (!builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(entry.getKey()).append(" = ").append(entry.getValue());
        }
        return builder.toString();
    }

    protected static void validate(List<String> columns, List<String> updateColumns) {
        for (String column : updateColumns) {
            if (!columns.contains(column)) {
                throw new IllegalArgumentException("Updated column " + column + " is not one of the inserted columns " + columns);
            }
        }
    }

    protected boolean tableExists(Connection connection, String catalog, String schema, String table) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(catalog, schema, table, TABLE_TYPE)) {
            return tables.next();
        }
    }

}
