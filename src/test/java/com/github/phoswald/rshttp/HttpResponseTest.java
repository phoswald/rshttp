package com.github.phoswald.rshttp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void builder_minimal_success() {
        HttpResponse response = HttpResponse.builder().build();

        assertEquals(200, response.status());
        assertNotNull(response.headers());
        assertTrue(response.headers().names().isEmpty());
        assertFalse(response.body().isPresent());
    }

    @Test
    void builder_maximal_success() {
        HttpResponse response = HttpResponse.builder()
                .status(201)
                .header("X", "A")
                .header("X", "B")
                .header("Y", "C")
                .body(new byte[42])
                .build();

        assertEquals(201, response.status());
        assertNotNull(response.headers());
        assertEquals("[X, Y]", response.headers().names().toString());
        assertEquals(Arrays.asList("A", "B"), response.headers().allValues("X"));
        assertEquals(Arrays.asList("C"), response.headers().allValues("Y"));
        assertTrue(response.headers().allValues("Z").isEmpty());
        assertEquals(42, response.body().get().length);
    }
}
