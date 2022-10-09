package org.outofoffice.lib.core.socket;

import lombok.extern.slf4j.Slf4j;
import org.outofoffice.lib.exception.EidaException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringJoiner;

import static java.nio.charset.StandardCharsets.UTF_8;


@Slf4j
public class EidaSocketClient {

    public String request(String address, String message) {
        try (
            Socket socket = createSocket(address);
            PrintWriter writer = getWriter(socket);
            BufferedReader reader = getReader(socket)
        ) {
            log.debug("request(address: {}, message: {})", address, message);

            writer.println(message);
            String response = readResponse(reader);

            log.debug("response: {}", response.replace("\n", "\\n"));
            return response;
        } catch (Exception e) {
            throw new EidaException(e);
        }
    }


    private Socket createSocket(String address) throws IOException {
        String[] addressTokens = address.split(":");
        String host = addressTokens[0];
        int port = Integer.parseInt(addressTokens[1]);
        return new Socket(host, port);
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true, UTF_8);
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
    }


    private String readResponse(BufferedReader reader) throws IOException {
        StringJoiner responseJoiner = new StringJoiner("\n");
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            responseJoiner.add(line);
        }
        String responseBody = responseJoiner.toString();

        String[] tokens = responseBody.split("\n", 2);

        String code = tokens[0];
        if (!code.equals("OK")) throw new EidaException(code);
        return tokens[1];
    }

}
