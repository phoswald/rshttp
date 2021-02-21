package com.github.phoswald.rshttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

class TestClient {

    private final int port;

    TestClient(int port) {
        this.port = port;
    }

    HttpResponse get(String path) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + port + path).openConnection();

            HttpResponse.Builder builder = HttpResponse.builder();
            builder.status(connection.getResponseCode());
            for(Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                for(int i = entry.getValue().size() - 1; i >= 0; i--) {
                    String value = entry.getValue().get(i);
                    if(entry.getKey() != null) {
                        builder.header(entry.getKey().toLowerCase(), value);
                    }
                }
            }
            try(InputStream responseStream = connection.getInputStream()) {
                ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
                int character;
                while((character = responseStream.read()) != -1) {
                    responseBuffer.write(character);
                }
                builder.body(responseBuffer.toByteArray());
            }
            return builder.build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
