package com.github.phoswald.rshttp;

import static com.github.phoswald.rshttp.HttpServer.route;
import static com.github.phoswald.rshttp.HttpServer.routes;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class HttpRoutesTest {

    private final HttpHandler handler = routes(
            route("/")
                    .get(req -> buildRes("TEST-ROOT")),
            route("/path1")
                    .get(req -> buildRes("TEST-PATH"))
                    .put(req -> HttpResponse.builder().status(201).build()),
            route("/path1/subpath1")
                    .get(req -> buildRes("TEST-SUBPATH")),
            route("/path2/([a-z]+)")
                    .get(req -> buildRes("TEST-REGEX " + req.params().firstValue("$1").orElse(null))),
            route("/path3")
                    .get(req -> buildRes("TEST-METHOD GET"))
                    .head(req -> buildRes("TEST-METHOD HEAD"))
                    .post(req -> buildRes("TEST-METHOD POST"))
                    .put(req -> buildRes("TEST-METHOD PUT"))
                    .patch(req -> buildRes("TEST-METHOD PATCH"))
                    .delete(req -> buildRes("TEST-METHOD DELETE")),
            route("/static1/").toFileSystem("src/test/resources/content"),
            route("/static2/").toClasspath("content")
    );

    @Test
    void routes_getValid_success() {
        assertEquals("TEST-ROOT", parseRes(handler.handle(HttpRequest.builder().get().path("/").build())));
        assertEquals("TEST-PATH", parseRes(handler.handle(HttpRequest.builder().get().path("/path1").build())));
        assertEquals("TEST-SUBPATH", parseRes(handler.handle(HttpRequest.builder().get().path("/path1/subpath1").build())));
        assertEquals("TEST-REGEX test", parseRes(handler.handle(HttpRequest.builder().get().path("/path2/test").build())));
    }

    @Test
    void routes_putValid_success() {
        assertEquals(201, handler.handle(HttpRequest.builder().put().path("/path1").build()).status());
    }

    @Test
    void routes_methodsValid_success() {
        assertEquals("TEST-METHOD GET", parseRes(handler.handle(HttpRequest.builder().get().path("/path3").build())));
        assertEquals("TEST-METHOD HEAD", parseRes(handler.handle(HttpRequest.builder().head().path("/path3").build())));
        assertEquals("TEST-METHOD POST", parseRes(handler.handle(HttpRequest.builder().post().path("/path3").build())));
        assertEquals("TEST-METHOD PUT", parseRes(handler.handle(HttpRequest.builder().put().path("/path3").build())));
        assertEquals("TEST-METHOD PATCH", parseRes(handler.handle(HttpRequest.builder().patch().path("/path3").build())));
        assertEquals("TEST-METHOD DELETE", parseRes(handler.handle(HttpRequest.builder().delete().path("/path3").build())));
    }

    @Test
    void routes_getStatic_success() {
        assertEquals("text/html", handler.handle(HttpRequest.builder().get().path("/static1/").build()).headers().firstValue("content-type").orElse(null));
        assertEquals("text/html", handler.handle(HttpRequest.builder().get().path("/static2/").build()).headers().firstValue("content-type").orElse(null));
        assertEquals("text/html", handler.handle(HttpRequest.builder().get().path("/static1/index.html").build()).headers().firstValue("content-type").orElse(null));
        assertEquals("text/html", handler.handle(HttpRequest.builder().get().path("/static2/index.html").build()).headers().firstValue("content-type").orElse(null));
    }

    @Test
    void routes_invalid_notFound() {
        assertEquals(404, handler.handle(HttpRequest.builder().put().path("/").build()).status());
        assertEquals(404, handler.handle(HttpRequest.builder().get().path("/not-existing").build()).status());
        assertEquals(404, handler.handle(HttpRequest.builder().put().path("/not-existing").build()).status());
    }

    private static HttpResponse buildRes(String message) {
        return HttpResponse.builder()
                .body(message.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    private static String parseRes(HttpResponse response) {
        return new String(response.body().get(), StandardCharsets.UTF_8);
    }
}
