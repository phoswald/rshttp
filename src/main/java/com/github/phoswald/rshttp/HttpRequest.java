package com.github.phoswald.rshttp;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public final class HttpRequest {

    private final HttpMethod method;
    private final URI uri;
    private final HttpVersion version;
    private final HttpHeaders headers;
    private final HttpParams params;
    private final Duration timeout;
    private final byte[] body;

    private HttpRequest(Builder builder) {
        this.method = Objects.requireNonNull(builder.method, "method");
        this.uri = Objects.requireNonNull(builder.uri, "uri");
        this.version = builder.version;
        this.headers = builder.headers.build();
        this.params = builder.params.build();
        this.timeout = builder.timeout;
        this.body = builder.body;
    }

    public static Builder builder() {
        return new Builder();
    }

    public HttpMethod method() {
        return method;
    }

    public URI uri() {
        return uri;
    }

    public String scheme() {
        return uri.getScheme();
    }

    public String host() {
        return uri.getHost();
    }

    public int port() {
        return uri.getPort();
    }

    public String path() {
        return uri.getPath();
    }

    public String query() {
        return uri.getQuery();
    }

    public String fragment() {
        return uri.getFragment();
    }

    public Optional<HttpVersion> version() {
        return Optional.ofNullable(version);
    }

    public HttpHeaders headers() {
        return headers;
    }

    public HttpParams params() {
        return params;
    }

    public Optional<Duration> timeout() {
        return Optional.ofNullable(timeout);
    }

    public Optional<byte[]> body() {
        return Optional.ofNullable(body);
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    private static URI parseUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("uri", e);
        }
    }

    public static final class Builder {

        private HttpMethod method;
        private URI uri;
        private HttpVersion version;
        private HttpHeaders.Builder headers = HttpHeaders.builder();
        private HttpParams.Builder params = HttpParams.builder();
        private Duration timeout;
        private byte[] body;

        private Builder() { }

        private Builder(HttpRequest instance) {
            this.method = instance.method;
            this.uri = instance.uri;
            this.version = instance.version;
            this.headers = instance.headers.toBuilder();
            this.params = instance.params.toBuilder();
            this.timeout = instance.timeout;
            this.body = instance.body;
        }

        public Builder get() {
            return method(HttpMethod.GET);
        }

        public Builder head() {
            return method(HttpMethod.HEAD);
        }

        public Builder post() {
            return method(HttpMethod.POST);
        }

        public Builder put() {
            return method(HttpMethod.PUT);
        }

        public Builder patch() {
            return method(HttpMethod.PATCH);
        }

        public Builder delete() {
            return method(HttpMethod.DELETE);
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder uri(String uri) {
            return uri(parseUri(uri));
        }

        public Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }

        public Builder path(String path) {
            this.uri = (uri == null) ? parseUri(path) : uri.resolve(path);
            return this;
        }

        public Builder version(HttpVersion version) {
            this.version = version;
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.header(name, value);
            return this;
        }

        Builder params(HttpParams params) { // not intended for public use
            for(String name: params.names()) {
                for(String value: params.allValues(name)) {
                    param(name, value);
                }
            }
            return this;
        }

        private Builder param(String name, String value) {
            this.params.param(name, value);
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}
