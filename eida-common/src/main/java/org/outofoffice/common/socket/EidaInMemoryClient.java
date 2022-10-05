package org.outofoffice.common.socket;

import lombok.extern.slf4j.Slf4j;
import org.outofoffice.common.exception.EidaException;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class EidaInMemoryClient extends EidaSocketClient {

    private static final String KEY_DELIMITER = ":::";
    private final Map<String, String> mappings = new HashMap<>();


    public void put(String address, String message, String result) {
        mappings.put(compositeKey(address, message), result);
    }

    @Override
    public String request(String address, String message) {
        log.debug("request(address: {}, message: {})", address, message);

        String response = mappings.get(compositeKey(address, message));
        if (response == null) {
            prettyPrintOfMappings();
            throw new EidaException("no result mapped by " + address + " and '" + message + "'");
        }

        log.debug("response: {}", response.replace("\n", "\\n"));
        return response;
    }

    private String compositeKey(String address, String message) {
        return address + KEY_DELIMITER + message;
    }


    private void prettyPrintOfMappings() {
        final String title = "[GIVEN MAPPINGS]";
        final String lineFormat = "%-30s [&] %-30s [->] %-30s\n";

        String header = String.format(lineFormat, "address", "message", "result");

        StringBuilder body = new StringBuilder();
        mappings.forEach((k, v) -> {
            String[] keyToken = k.split(KEY_DELIMITER);
            String sanitizedValue = mappings.get(k).replace("\n", "\\n");
            String prettyRow = String.format(lineFormat, keyToken[0], keyToken[1], sanitizedValue);
            body.append(prettyRow);
        });

        log.debug("\n" + title + "\n{}{}", header, body);
    }

}
