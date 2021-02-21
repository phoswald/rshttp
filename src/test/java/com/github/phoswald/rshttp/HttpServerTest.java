package com.github.phoswald.rshttp;

import static com.github.phoswald.rshttp.HttpServer.route;
import static com.github.phoswald.rshttp.HttpServer.routes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HttpServerTest {

    static final int port = new PortFinder().findPort();
    static final TestClient client = new TestClient(port);
    static HttpServer server;

    @BeforeAll
    static void start() {
        HttpHandler handler = routes(
                route("/hello-simple")
                        .get(HttpServerTest::getHello),
                route("/hello-query")
                        .get(HttpServerTest::getHello));

        server = HttpServer.builder()
                .port(port)
                .handler(handler)
                .build();
    }

    @AfterAll
    static void stop() {
        server.close();
    }

    @Test
    void get_simple_success() {
        HttpResponse response = client.get("/hello-simple");

        assertEquals(201, response.status());
        assertEquals("text/plain", response.headers().firstValue("content-type").get());
        assertEquals(Arrays.asList("value-1", "value-2"), response.headers().allValues("x-test"));
        assertEquals("Hello, world!\n", b2s(response.body().get()));
    }

    @Test
    void get_query_success() {
        HttpResponse response = client.get("/hello-query?name=client");

        assertEquals(201, response.status());
        assertEquals("Hello, client!\n", b2s(response.body().get()));
    }

    @Test
    void get_notExisting_notFound() {
        assertThrows(UncheckedIOException.class, () -> client.get("/not-existing"));
    }

    private static String b2s(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static byte[] s2b(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    private static HttpResponse getHello(HttpRequest request) {
        return HttpResponse.builder()
                .status(201)
                .header("content-type", "text/plain")
                .header("x-test", "value-1")
                .header("x-test", "value-2")
                .body(s2b("Hello, " + request.params().firstValue("name").orElse("world") + "!\n"))
                .build();
    }
}
