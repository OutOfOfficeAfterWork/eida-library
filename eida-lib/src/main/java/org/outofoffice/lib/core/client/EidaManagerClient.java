package org.outofoffice.lib.core.client;

import lombok.RequiredArgsConstructor;
import org.outofoffice.lib.core.socket.EidaSocketClient;
import org.outofoffice.lib.core.query.EidaDllGenerator;


@RequiredArgsConstructor
public class EidaManagerClient {

    private final EidaDllGenerator dllGenerator;
    private final EidaSocketClient eidaClient;

    private final String managerServerUrl;


    public String getSources(String tableName) {
        String dll = dllGenerator.createGetAllShardUrlsQuery(tableName);
        return eidaClient.request(managerServerUrl, dll);
    }

    public String getDestination(String tableName) {
        String dll = dllGenerator.createGetDestinationShardUrlQuery(tableName);
        return eidaClient.request(managerServerUrl, dll);
    }

    public <ID> String getSource(String tableName, ID id) {
        String dll = dllGenerator.createGetSourceShardUrlQuery(tableName, id);
        return eidaClient.request(managerServerUrl, dll);
    }

    public <ID> void postShardUrl(String shardUrl, String tableName, ID id) {
        String dll = dllGenerator.createReportInsertShardUrlQuery(shardUrl, tableName, id);
        eidaClient.request(managerServerUrl, dll);
    }

    public <ID> void deleteShardUrl(String shardUrl, String tableName, ID id) {
        String dll = dllGenerator.createReportDeleteShardUrlQuery(shardUrl, tableName, id);
        eidaClient.request(managerServerUrl, dll);
    }

    public Long nextVal() {
        String query = "nextval";
        String value = eidaClient.request(managerServerUrl, query);
        return Long.parseLong(value);
    }

}
