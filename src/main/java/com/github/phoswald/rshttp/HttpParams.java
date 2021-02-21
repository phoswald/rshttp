package com.github.phoswald.rshttp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A request's combined collection of path-, query- and form-parameters as a multi-valued map.
 */
public final class HttpParams {

    private final Map<String, List<String>> map;

    private HttpParams(Map<String, List<String>> map) {
        this.map = map;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static HttpParams fromQuery(String query) {
        Builder builder = builder();
        if(query != null) {
            int current = 0;
            while(current < query.length()) {
                int pairSep = query.indexOf('&', current);
                if(pairSep == -1) {
                    pairSep = query.length();
                }
                if(pairSep > current) {
                    String pair = query.substring(current, pairSep);
                    int keySep = pair.indexOf('=');
                    if(keySep == -1) {
                        builder.param(pair, "");
                    } else {
                        builder.param(pair.substring(0, keySep), pair.substring(keySep + 1));
                    }
                }
                current = pairSep + 1;
            }
        }
        return builder.build();
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

        private Builder(HttpParams instance) {
            this.map = new LinkedHashMap<>(instance.map);
        }

        public Builder param(String name, String value) {
            Objects.requireNonNull(name, "name"); // TODO require non-empty name
            Objects.requireNonNull(value, "value");
            this.map.computeIfAbsent(name, n -> new ArrayList<>()).add(value);
            return this;
        }

        public HttpParams build() {
            return new HttpParams(this.map);
        }
    }
}
