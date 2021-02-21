package com.github.phoswald.rshttp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class HttpHeaders {

    private final Map<String, List<String>> map;

    private HttpHeaders(Map<String, List<String>> map) {
        this.map = map;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Set<String> names() {
        return Collections.unmodifiableSet(map.keySet());
    }

    public Optional<String> firstValue(String name) {
        Objects.requireNonNull(name, "name");
        return map.containsKey(name) ? Optional.of(map.get(name).get(0)) : Optional.empty();
    }

    public List<String> allValues(String name) {
        Objects.requireNonNull(name, "name");
        return map.containsKey(name) ? Collections.unmodifiableList(map.get(name)) : Collections.emptyList();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {

        private final Map<String, List<String>> map;

        private Builder() {
            this.map = new LinkedHashMap<>();
        }

        private Builder(HttpHeaders instance) {
            this.map = new LinkedHashMap<>(instance.map);
        }

        public Builder header(String name, String value) {
            Objects.requireNonNull(name, "name"); // TODO require non-empty name
            Objects.requireNonNull(value, "value"); // TODO require non-empty value
            this.map.computeIfAbsent(name, n -> new ArrayList<>()).add(value);
            return this;
        }

        public HttpHeaders build() {
            return new HttpHeaders(this.map);
        }
    }
}
