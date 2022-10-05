package org.outofoffice.lib.core.query;


public class EidaDmlGenerator {

    public String createSelectAllQuery(String tableName) {
        return "select all, " + tableName;
    }

    public <ID> String createSelectByIdQuery(String tableName, ID id) {
        return "select, " + tableName + " " + id;
    }

    public String createInsertQuery(String tableName, String serialized) {
        return "insert, " + tableName + " " + serialized;
    }

    public String createUpdateQuery(String tableName, String serialized) {
        return "update, " + tableName + " " + serialized;
    }

    public <ID> String createDeleteQuery(String tableName, ID id) {
        return "delete, " + tableName + " " + id;
    }
}
