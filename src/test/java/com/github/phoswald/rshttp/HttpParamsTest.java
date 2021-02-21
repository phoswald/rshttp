package com.github.phoswald.rshttp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class HttpParamsTest {
    // TODO URL encoding

    @Test
    void builder_minimal_success() {
        HttpParams params = HttpParams.builder().build();

        assertTrue(params.names().isEmpty());
        assertFalse(params.firstValue("X").isPresent());
        assertTrue(params.allValues("X").isEmpty());
    }

    @Test
    void builder_maximal_success() {
        HttpParams params = HttpParams.builder()
                .param("X", "A")
                .param("X", "B")
                .param("Y", "C")
                .build();

        assertEquals("[X, Y]", params.names().toString());
        assertEquals("A", params.firstValue("X").get());
        assertEquals("C", params.firstValue("Y").get());
        assertFalse(params.firstValue("Z").isPresent());
        assertEquals(Arrays.asList("A", "B"), params.allValues("X"));
        assertEquals(Arrays.asList("C"), params.allValues("Y"));
        assertTrue(params.allValues("Z").isEmpty());
    }

    @Test
    void toBuilder_fromInstance_newInstance() {
        HttpParams paramsBefore = HttpParams.builder()
                .param("X", "A")
                .build();
        HttpParams params = paramsBefore.toBuilder()
                .param("X", "B")
                .param("Y", "C")
                .build();

        assertNotSame(paramsBefore, params);
        assertEquals("[X, Y]", params.names().toString());
        assertEquals("A", params.firstValue("X").get());
        assertEquals("C", params.firstValue("Y").get());
        assertFalse(params.firstValue("Z").isPresent());
        assertEquals(Arrays.asList("A", "B"), params.allValues("X"));
        assertEquals(Arrays.asList("C"), params.allValues("Y"));
        assertTrue(params.allValues("Z").isEmpty());
    }

    @Test
    void fromQuery_null_empty() {
        HttpParams params = HttpParams.fromQuery(null);
        assertTrue(params.names().isEmpty());
    }

    @Test
    void fromQuery_empty_empty() {
        HttpParams params = HttpParams.fromQuery("");
        assertTrue(params.names().isEmpty());
    }

    @Test
    void fromQuery_single_success() {
        HttpParams params = HttpParams.fromQuery("var=val");
        assertEquals("val", params.firstValue("var").get());
    }

    @Test
    void fromQuery_multiple_success() {
        HttpParams params = HttpParams.fromQuery("varA=val1&varB=val2&varC=val3");
        assertEquals("[varA, varB, varC]", params.names().toString());
        assertEquals("val1", params.firstValue("varA").get());
        assertEquals("val2", params.firstValue("varB").get());
        assertEquals("val3", params.firstValue("varC").get());
    }

    @Test
    void fromQuery_multipleRepeated_success() {
        HttpParams params = HttpParams.fromQuery("varA=val1&varB=val2&varA=val3");
        assertEquals("[varA, varB]", params.names().toString());
        assertEquals(Arrays.asList("val1", "val3"), params.allValues("varA"));
        assertEquals(Arrays.asList("val2"), params.allValues("varB"));
    }

    @Test
    void fromQuery_noOrEmptyValue_success() {
        HttpParams params = HttpParams.fromQuery("&&varA&varB=&&");
        assertEquals("[varA, varB]", params.names().toString());
        assertEquals(Arrays.asList(""), params.allValues("varA"));
        assertEquals(Arrays.asList(""), params.allValues("varB"));
    }
}
