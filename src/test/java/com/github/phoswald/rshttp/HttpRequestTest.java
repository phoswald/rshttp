package com.github.phoswald.rshttp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void builder_minimal_success() {
        HttpRequest request = HttpRequest.builder()
                .get()
                .path("/")
                .build();

        assertSame(HttpMethod.GET, request.method());
        assertEquals("/", request.uri().toString());
        assertFalse(request.version().isPresent());
        assertNotNull(request.headers());
        assertTrue(request.headers().names().isEmpty());
        assertNotNull(request.params());
        assertTrue(request.params().names().isEmpty());
        assertFalse(request.timeout().isPresent());
        assertFalse(request.body().isPresent());
    }

    @Test
    void builder_maximal_success() {
        HttpRequest request = HttpRequest.builder()
                .post()
                .path("/path")
                .version(HttpVersion.HTTP_2)
                .timeout(Duration.ofMinutes(2))
                .header("X", "A")
                .header("X", "B")
                .header("Y", "C")
                .body(new byte[42])
                .build();

        assertSame(HttpMethod.POST, request.method());
        assertEquals("/path", request.uri().toString());
        assertSame(HttpVersion.HTTP_2, request.version().get());
        assertNotNull(request.headers());
        assertEquals("[X, Y]", request.headers().names().toString());
        assertEquals(Arrays.asList("A", "B"), request.headers().allValues("X"));
        assertEquals(Arrays.asList("C"), request.headers().allValues("Y"));
        assertTrue(request.headers().allValues("Z").isEmpty());
        assertNotNull(request.params());
        assertEquals(Duration.ofMinutes(2), request.timeout().get());
        assertEquals(42, request.body().get().length);
    }

    @Test
    void toBuilder_fromInstance_newInstance() {
        HttpRequest requestBefore = HttpRequest.builder()
                .get()
                .path("/path")
                .params(HttpParams.builder().param("key1", "value1").build())
                .build();
        HttpRequest request = requestBefore.toBuilder()
                .params(HttpParams.builder().param("key2", "value2").build())
                .build();

        assertNotSame(requestBefore, request);
        assertEquals("/path", request.path());
        assertEquals("[key1, key2]", request.params().names().toString());
        assertEquals("value1", request.params().firstValue("key1").get());
        assertEquals("value2", request.params().firstValue("key2").get());
    }

    @Test
    void uri_maximal_success() {
        HttpRequest request = HttpRequest.builder()
                .get()
                .uri("https://server.domain.tld:1234/path/subpath/file?query=foo&bar=baz#fragile")
                .build();

        assertSame(HttpMethod.GET, request.method());
        assertEquals("https://server.domain.tld:1234/path/subpath/file?query=foo&bar=baz#fragile", request.uri().toString());
        assertEquals("https", request.scheme());
        assertEquals("server.domain.tld", request.host());
        assertEquals(1234, request.port());
        assertEquals("/path/subpath/file", request.path());
        assertEquals("query=foo&bar=baz", request.query());
        assertEquals("fragile", request.fragment());
    }
}
