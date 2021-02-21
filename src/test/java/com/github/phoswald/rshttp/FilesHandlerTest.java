package com.github.phoswald.rshttp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FilesHandlerTest {

    // TODO escape prevention (..)

    @Test
    void forFilesystem_valid_success() {
        assertIndexHtml(FilesHandler.forFilesystem("src/test/resources/content", "/").handle(get("/index.html")));
        assertIndexHtml(FilesHandler.forFilesystem("src/test/resources/content/", "/").handle(get("/index.html")));
        assertIndexHtml(FilesHandler.forFilesystem("src/test/resources/content/", "/").handle(get("/")));
    }

    @Test
    void forFilesystem_validPrefix_success() {
        assertIndexHtml(FilesHandler.forFilesystem("src/test/resources/content", "static").handle(get("/static/index.html")));
        assertIndexHtml(FilesHandler.forFilesystem("src/test/resources/content", "static/").handle(get("/static/index.html")));
        assertIndexHtml(FilesHandler.forFilesystem("src/test/resources/content", "/static").handle(get("/static/index.html")));
        assertIndexHtml(FilesHandler.forFilesystem("src/test/resources/content", "/static/").handle(get("/static/index.html")));
        assertIndexHtml(FilesHandler.forFilesystem("src/test/resources/content", "/static/").handle(get("/static/")));
    }

    @Test
    void forFilesystem_put_forbidden() {
        assertStatus(403, FilesHandler.forFilesystem("src/test/resources/content", "/").handle(put("/index.html")));
    }

    @Test
    void forClasspathDir_valid_success() {
        assertIndexHtml(FilesHandler.forClasspath("content", "/").handle(get("/index.html")));
        assertIndexHtml(FilesHandler.forClasspath("content/", "/").handle(get("/index.html")));
        assertIndexHtml(FilesHandler.forClasspath("/content", "/").handle(get("/index.html")));
        assertIndexHtml(FilesHandler.forClasspath("/content/", "/").handle(get("/index.html")));
        assertIndexHtml(FilesHandler.forClasspath("/content/", "/").handle(get("/")));
    }

    private HttpRequest get(String path) {
        return HttpRequest.builder().get().path(path).build();
    }

    private HttpRequest put(String path) {
        return HttpRequest.builder().put().path(path).build();
    }

    private void assertIndexHtml(HttpResponse response) {
        assertEquals(200, response.status());
        assertEquals("text/html", response.headers().firstValue("content-type").orElse(null));
        assertEquals(81, response.body().get().length);
    }

    private void assertStatus(int status, HttpResponse response) {
        assertEquals(status, response.status());
    }
}
