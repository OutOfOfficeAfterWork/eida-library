package org.outofoffice.common.testing;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outofoffice.common.socket.EidaSocketClient;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class EidaSocketTestFacade {

    private static final EidaSocketClient eidaClient = new EidaSocketClient();


    public static void request(TestRequest req) {
        long start = System.currentTimeMillis();

        String address = req.getAddress();
        String message = req.getMessage();
        String request = address + ", " + message;
        String response = eidaClient.request(address, message);

        long end = System.currentTimeMillis();
        long duration = end - start;

        log.info("\n" +
            "\trequest: {}\n" +
            "\tresponse: {}\n" +
            "\tduration: {}", request, response, duration);
    }

}
