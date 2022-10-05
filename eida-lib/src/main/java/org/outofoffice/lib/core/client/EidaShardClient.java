package org.outofoffice.lib.core.client;

import lombok.RequiredArgsConstructor;
import org.outofoffice.common.socket.EidaSocketClient;
import org.outofoffice.lib.core.query.EidaDmlGenerator;


@RequiredArgsConstructor
public class EidaShardClient {

    private final EidaDmlGenerator dmlGenerator;
    private final EidaSocketClient eidaClient;


    public String selectAll(String shardUrl, String tableName) {
        String dml = dmlGenerator.createSelectAllQuery(tableName);
        return eidaClient.request(shardUrl, dml);
    }

    public <ID> String selectById(String shardUrl, String tableName, ID id) {
        String dml = dmlGenerator.createSelectByIdQuery(tableName, id);
        return eidaClient.request(shardUrl, dml);
    }

    public void insert(String shardUrl, String tableName, String serialized) {
        String dml = dmlGenerator.createInsertQuery(tableName, serialized);
        eidaClient.request(shardUrl, dml);
    }

    public void update(String shardUrl, String tableName, String serialized) {
        String dml = dmlGenerator.createUpdateQuery(tableName, serialized);
        eidaClient.request(shardUrl, dml);
    }

    public <ID> void delete(String shardUrl, String tableName, ID id) {
        String dml = dmlGenerator.createDeleteQuery(tableName, id);
        eidaClient.request(shardUrl, dml);
    }

}
