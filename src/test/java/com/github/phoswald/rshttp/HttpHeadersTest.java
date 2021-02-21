package com.github.phoswald.rshttp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class HttpHeadersTest {
    // TODO case insensitivity

    @Test
    void builder_minimal_success() {
        HttpHeaders headers = HttpHeaders.builder().build();

        assertTrue(headers.names().isEmpty());
        assertFalse(headers.firstValue("X").isPresent());
        assertTrue(headers.allValues("X").isEmpty());
    }

    @Test
    void builder_maximal_success() {
        HttpHeaders headers = HttpHeaders.builder()
                .header("X", "A")
                .header("X", "B")
                .header("Y", "C")
                .build();

        assertEquals("[X, Y]", headers.names().toString());
        assertEquals("A", headers.firstValue("X").get());
        assertEquals("C", headers.firstValue("Y").get());
        assertFalse(headers.firstValue("Z").isPresent());
        assertEquals(Arrays.asList("A", "B"), headers.allValues("X"));
        assertEquals(Arrays.asList("C"), headers.allValues("Y"));
        assertTrue(headers.allValues("Z").isEmpty());
    }

    @Test
    void toBuilder_fromInstance_newInstance() {
        HttpHeaders headersBefore = HttpHeaders.builder()
                .header("X", "A")
                .build();
        HttpHeaders headers = headersBefore.toBuilder()
                .header("X", "B")
                .header("Y", "C")
                .build();

        assertNotSame(headersBefore, headers);
        assertEquals("[X, Y]", headers.names().toString());
        assertEquals("A", headers.firstValue("X").get());
        assertEquals("C", headers.firstValue("Y").get());
        assertFalse(headers.firstValue("Z").isPresent());
        assertEquals(Arrays.asList("A", "B"), headers.allValues("X"));
        assertEquals(Arrays.asList("C"), headers.allValues("Y"));
        assertTrue(headers.allValues("Z").isEmpty());
    }
}
